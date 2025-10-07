package com.bwt.recipes;

import com.bwt.utils.Id;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlockIngredient implements CustomIngredient {
    protected final BlockIngredient.Entry[] entries;
    protected Block[] matchingBlocks;
    public static final BlockIngredient EMPTY = new BlockIngredient(Stream.empty());
    public static final Serializer SERIALIZER = new Serializer();

    protected BlockIngredient(Stream<? extends BlockIngredient.Entry> entries) {
        this.entries = entries.toArray(Entry[]::new);
    }

    protected BlockIngredient(BlockIngredient.Entry[] entries) {
        this.entries = entries;
    }

    public static BlockIngredient fromBlocks(Block... blocks) {
        return ofEntries(Arrays.stream(blocks).map(BlockIngredient.BlockEntry::new));
    }

    public static BlockIngredient fromBlock(Block block) {
        return fromBlocks(block);
    }

    protected static BlockIngredient ofEntries(Stream<? extends BlockIngredient.Entry> entries) {
        BlockIngredient ingredient = new BlockIngredient(entries);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static BlockIngredient empty() {
        return EMPTY;
    }

    public static BlockIngredient fromTag(TagKey<Block> tag) {
        return ofEntries(Stream.of(new BlockIngredient.TagEntry(tag)));
    }

    public boolean isEmpty() {
        return this.entries.length == 0;
    }


    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
            return test(blockItem.getBlock());
        }
        return false;
    }

    public boolean test(Block block) {
        if (block == null) {
            return false;
        }
        return this.getMatchingBlocks().stream()
                .anyMatch(matchingBlock -> matchingBlock.equals(block));
    }

    public Stream<? extends BlockIngredient.Entry> streamEntries() {
        return Arrays.stream(entries);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of();
    }

    public List<Block> getMatchingBlocks() {
        if (this.matchingBlocks == null) {
            this.matchingBlocks = this.streamEntries()
                    .flatMap(entry -> entry.getBlocks()
                            .filter(Objects::nonNull)
                            .distinct()
                    ).toArray(Block[]::new);
        }

        return List.of(this.matchingBlocks);
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<BlockIngredient> {
        private static final Identifier ID = Id.of("block_ingredient");
        public static final Codec<BlockIngredient> CODEC = createCodec();
        public static final MapCodec<BlockIngredient> MAP_CODEC = CODEC.fieldOf("ingredient");


        public static final PacketCodec<RegistryByteBuf, BlockIngredient> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public static Codec<BlockIngredient> createCodec() {
            Codec<BlockIngredient.Entry[]> codec = Codec.list(BlockIngredient.Entry.CODEC)
                    .comapFlatMap(
                            entries -> entries.isEmpty()
                                    ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                                    : DataResult.success(entries.toArray(new Entry[0])),
                            List::of
                    );
            return Codec.either(codec, BlockIngredient.Entry.CODEC)
                    .flatComapMap(
                            either -> either.map(BlockIngredient::new, entry -> new BlockIngredient(new BlockIngredient.Entry[]{entry})),
                            ingredient -> {
                                if (ingredient.entries.length == 1) {
                                    return DataResult.success(Either.right(ingredient.entries[0]));
                                } else {
                                    return ingredient.entries.length == 0
                                            ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                                            : DataResult.success(Either.left(ingredient.entries));
                                }
                            }
                    );
        }

        @Override
        public Identifier getIdentifier() {
            return ID;
        }


        @Override
        public PacketCodec<RegistryByteBuf, BlockIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }

        @Override
        public MapCodec<BlockIngredient> getCodec(boolean allowEmpty) {
            return MAP_CODEC;
        }

        public static BlockIngredient read(RegistryByteBuf buf) {
            return PACKET_CODEC.decode(buf);
        }

        public static void write(RegistryByteBuf buf, BlockIngredient ingredient) {
            PACKET_CODEC.encode(buf, ingredient);
        }
    }

    public interface Entry {
        Codec<BlockIngredient.Entry> CODEC = Codec.xor(BlockIngredient.BlockEntry.CODEC, BlockIngredient.TagEntry.CODEC)
                .xmap(either -> either.map(blockEntry -> blockEntry, tagEntry -> tagEntry), entry -> {
                    if (entry instanceof BlockIngredient.TagEntry tagEntry) {
                        return Either.right(tagEntry);
                    } else if (entry instanceof BlockIngredient.BlockEntry stackEntry) {
                        return Either.left(stackEntry);
                    } else {
                        throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
                    }
                });

        Stream<Block> getBlocks();
    }

    public record BlockEntry(Block block) implements BlockIngredient.Entry {
        static final Codec<Block> BLOCK_CODEC = Registries.BLOCK
                .getEntryCodec()
                .validate(entry -> {
                    Optional<RegistryKey<Block>> air = Registries.BLOCK.getEntry(Blocks.AIR).getKey();
                    if (air.isEmpty() || entry.matchesKey(air.get())) {
                        return DataResult.error(() -> "Item must not be minecraft:air");
                    }
                    return DataResult.success(entry);
                })
                .xmap(RegistryEntry::value, Registries.BLOCK::getEntry);

        static final Codec<BlockIngredient.BlockEntry> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BLOCK_CODEC
                                .fieldOf("block")
                                .forGetter(BlockEntry::block)
                ).apply(instance, BlockIngredient.BlockEntry::new)
        );

        public boolean equals(Object o) {
            return o instanceof BlockEntry blockEntry && blockEntry.block.equals(this.block);
        }

        @Override
        public Stream<Block> getBlocks() {
            return Stream.of(this.block);
        }
    }

    public record TagEntry(TagKey<Block> tag) implements BlockIngredient.Entry {
        static final Codec<BlockIngredient.TagEntry> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        TagKey.codec(RegistryKeys.BLOCK)
                                .fieldOf("blockTag")
                                .forGetter(entry -> entry.tag)
                ).apply(instance, BlockIngredient.TagEntry::new)
        );

        public boolean equals(Object o) {
            return o instanceof TagEntry tagEntry && tagEntry.tag.id().equals(this.tag.id());
        }

        @Override
        public Stream<Block> getBlocks() {
            return StreamSupport.stream(Registries.BLOCK.iterateEntries(this.tag).spliterator(), false)
                    .map(RegistryEntry::value);
        }
    }
}

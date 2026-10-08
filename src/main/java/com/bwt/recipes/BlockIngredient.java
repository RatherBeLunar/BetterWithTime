package com.bwt.recipes;

import com.bwt.utils.Id;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record BlockIngredient(HolderSet<Block> values) implements CustomIngredient {
    public static final Codec<Holder<Block>> BLOCK_CODEC = BuiltInRegistries.BLOCK
            .holderByNameCodec()
            .validate(holder -> holder.is(Blocks.AIR.builtInRegistryHolder()) ? DataResult.error(() -> "Block must not be minecraft:air") : DataResult.success(holder));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.BLOCK)
            .map(BlockIngredient::new, BlockIngredient::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<BlockIngredient>> OPTIONAL_CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.BLOCK)
            .map(
                    holderSet -> holderSet.size() == 0 ? Optional.empty() : Optional.of(new BlockIngredient(holderSet)),
                    optional -> optional.map(BlockIngredient::values).orElse(HolderSet.direct())
            );
    public static final Codec<HolderSet<Block>> NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.BLOCK, BLOCK_CODEC, false);
    public static final Codec<BlockIngredient> CODEC = ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(BlockIngredient::new, BlockIngredient::values);

    public static final Serializer SERIALIZER = new Serializer();

    public static BlockIngredient of(Block block) {
        return new BlockIngredient(HolderSet.direct(block.builtInRegistryHolder()));
    }

    public static BlockIngredient of(Block... blocks) {
        return of(Arrays.stream(blocks));
    }

    public static BlockIngredient of(Stream<? extends Block> stream) {
        return new BlockIngredient(HolderSet.direct(stream.map(Block::builtInRegistryHolder).toList()));
    }

    public static BlockIngredient of(HolderSet<Block> holderSet) {
        return new BlockIngredient(holderSet);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return test(blockItem.getBlock());
        }
        return false;
    }

    public boolean test(Block block) {
        BlockState state = block.defaultBlockState();
        return this.values.stream().anyMatch(state::is);
    }

    public List<Holder<Block>> getMatchingBlocks() {
        return ImmutableList.copyOf(this.values);
    }

    @Override
    public List<Holder<Item>> getMatchingItems() {
        return getMatchingBlocks().stream().map(Holder::value).map(Block::asItem).map(Holder::direct).toList();
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
        private static final ResourceLocation ID = Id.of("block_ingredient");
        public static final MapCodec<BlockIngredient> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("values").forGetter(BlockIngredient::values)
                ).apply(instance, BlockIngredient::new)
        );

        @Override
        public ResourceLocation getIdentifier() {
            return ID;
        }


        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> getPacketCodec() {
            return CONTENTS_STREAM_CODEC;
        }

        @Override
        public MapCodec<BlockIngredient> getCodec() {
            return CODEC;
        }
    }
}

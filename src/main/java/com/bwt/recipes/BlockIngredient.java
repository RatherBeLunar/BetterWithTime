package com.bwt.recipes;

import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BlockIngredient(Optional<TagKey<Block>> optionalBlockTagKey, Optional<Block> optionalBlock) implements CustomIngredient {
    public static final Serializer SERIALIZER = new Serializer();

    public static BlockIngredient fromBlock(Block block) {
        return new BlockIngredient(Optional.empty(), Optional.of(block));
    }

    public static BlockIngredient fromTag(TagKey<Block> tagKey) {
        return new BlockIngredient(Optional.of(tagKey), Optional.empty());
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return test(blockItem.getBlock());
        }
        return false;
    }

    public boolean test(Block block) {
        return optionalBlockTagKey.filter(blockTagKey -> block.getDefaultState().isIn(blockTagKey)).isPresent()
                || optionalBlock.filter(block::equals).isPresent();
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of();
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
        public static final MapCodec<BlockIngredient> CODEC = createCodec();
        public static final PacketCodec<RegistryByteBuf, BlockIngredient> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public static MapCodec<BlockIngredient> createCodec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            TagKey.codec(RegistryKeys.BLOCK).optionalFieldOf("blockTag").forGetter(blockIngredient -> blockIngredient.optionalBlockTagKey),
                            Registries.BLOCK.getCodec().optionalFieldOf("block").forGetter(blockIngredient -> blockIngredient.optionalBlock)
                    ).apply(instance, BlockIngredient::new)
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
            return CODEC;
        }

        public static BlockIngredient read(RegistryByteBuf buf) {
            Identifier blockTagKeyId = buf.readIdentifier();
            Optional<TagKey<Block>> blockTagKey = blockTagKeyId.getNamespace().isBlank() ? Optional.empty() : Optional.of(TagKey.of(RegistryKeys.BLOCK, blockTagKeyId));
            Identifier blockId = buf.readIdentifier();
            Block block = Registries.BLOCK.get(blockId);
            return new BlockIngredient(blockTagKey, block.equals(Blocks.AIR) ? Optional.empty() : Optional.of(block));
        }

        public static void write(RegistryByteBuf buf, BlockIngredient ingredient) {
            buf.writeIdentifier(ingredient.optionalBlockTagKey.map(TagKey::id).orElse(Id.of("", "")));
            buf.writeIdentifier(ingredient.optionalBlock.map(Registries.BLOCK::getId).orElse(Id.of("", "")));
        }
    }
}

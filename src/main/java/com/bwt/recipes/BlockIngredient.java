package com.bwt.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BlockIngredient(Optional<TagKey<Block>> optionalBlockTagKey, Optional<Block> optionalBlock) implements CustomIngredient {
    public static final BlockIngredient.Serializer SERIALIZER = new BlockIngredient.Serializer();

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
    @Nullable
    public List<ItemStack> getMatchingStacks() {
        return null;
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
        private static final Identifier ID = new Identifier("bwt", "block_ingredient");
        public static final Codec<BlockIngredient> CODEC = createCodec();

        public static Codec<BlockIngredient> createCodec() {
            return RecordCodecBuilder.create(instance ->
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
        public Codec<BlockIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public BlockIngredient read(PacketByteBuf buf) {
            Identifier blockTagKeyId = buf.readIdentifier();
            Optional<TagKey<Block>> blockTagKey = blockTagKeyId.getNamespace().isBlank() ? Optional.empty() : Optional.of(TagKey.of(RegistryKeys.BLOCK, blockTagKeyId));
            Identifier blockId = buf.readIdentifier();
            Block block = Registries.BLOCK.get(blockId);
            return new BlockIngredient(blockTagKey, block.equals(Blocks.AIR) ? Optional.empty() : Optional.of(block));
        }

        @Override
        public void write(PacketByteBuf buf, BlockIngredient ingredient) {
            buf.writeIdentifier(ingredient.optionalBlockTagKey.map(TagKey::id).orElse(new Identifier("", "")));
            buf.writeIdentifier(ingredient.optionalBlock.map(Registries.BLOCK::getId).orElse(new Identifier("", "")));
        }
    }
}

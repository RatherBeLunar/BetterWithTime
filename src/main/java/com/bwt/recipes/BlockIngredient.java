package com.bwt.recipes;

import com.bwt.utils.Id;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        return optionalBlockTagKey.filter(blockTagKey -> block.defaultBlockState().is(blockTagKey)).isPresent()
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
        private static final ResourceLocation ID = Id.of("block_ingredient");
        public static final MapCodec<BlockIngredient> CODEC = createCodec();
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
        );

        public static MapCodec<BlockIngredient> createCodec() {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            TagKey.hashedCodec(Registries.BLOCK).optionalFieldOf("blockTag").forGetter(blockIngredient -> blockIngredient.optionalBlockTagKey),
                            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block").forGetter(blockIngredient -> blockIngredient.optionalBlock)
                    ).apply(instance, BlockIngredient::new)
            );
        }

        @Override
        public ResourceLocation getIdentifier() {
            return ID;
        }


        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }

        @Override
        public MapCodec<BlockIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        public static BlockIngredient read(RegistryFriendlyByteBuf buf) {
            ResourceLocation blockTagKeyId = buf.readResourceLocation();
            Optional<TagKey<Block>> blockTagKey = blockTagKeyId.getNamespace().isBlank() ? Optional.empty() : Optional.of(TagKey.create(Registries.BLOCK, blockTagKeyId));
            ResourceLocation blockId = buf.readResourceLocation();
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            return new BlockIngredient(blockTagKey, block.equals(Blocks.AIR) ? Optional.empty() : Optional.of(block));
        }

        public static void write(RegistryFriendlyByteBuf buf, BlockIngredient ingredient) {
            buf.writeResourceLocation(ingredient.optionalBlockTagKey.map(TagKey::location).orElse(Id.of("", "")));
            buf.writeResourceLocation(ingredient.optionalBlock.map(BuiltInRegistries.BLOCK::getKey).orElse(Id.of("", "")));
        }
    }
}

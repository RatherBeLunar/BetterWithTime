package com.bwt.recipes;

import com.bwt.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record IngredientWithCount(Ingredient ingredient, int count) implements CustomIngredient {
    public static final Serializer SERIALIZER = new Serializer();
    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.EMPTY, 0);

    public static IngredientWithCount fromStack(ItemStack stack) {
        return new IngredientWithCount(Ingredient.ofStacks(stack), stack.getCount());
    }

    public static IngredientWithCount fromTag(TagKey<Item> itemTag, int count) {
        return new IngredientWithCount(Ingredient.fromTag(itemTag), count);
    }

    public static IngredientWithCount fromTag(TagKey<Item> itemTag) {
        return new IngredientWithCount(Ingredient.fromTag(itemTag), 1);
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!ingredient.test(stack)) return false;
        return stack.getCount() >= count;
    }

    public boolean test(ItemVariant itemVariant) {
        return ingredient.test(itemVariant.toStack());
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        List<ItemStack> stacks = new ArrayList<>(List.of(ingredient.getMatchingStacks()));
        stacks.replaceAll(stack -> stack.copyWithCount(count));
        stacks.removeIf(stack -> !ingredient.test(stack));
        return stacks;
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<IngredientWithCount> {
        private static final Identifier ID = Id.of("ingredient_with_count");
        public static final MapCodec<IngredientWithCount> ALLOW_EMPTY_CODEC = createCodec(Ingredient.ALLOW_EMPTY_CODEC);
        public static final MapCodec<IngredientWithCount> DISALLOW_EMPTY_CODEC = createCodec(Ingredient.DISALLOW_EMPTY_CODEC);
        public static final PacketCodec<RegistryByteBuf, IngredientWithCount> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public static MapCodec<IngredientWithCount> createCodec(Codec<Ingredient> ingredientCodec) {
            return RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ingredientCodec.fieldOf("ingredient").forGetter(IngredientWithCount::ingredient),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
                    ).apply(instance, IngredientWithCount::new)
            );
        }

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<IngredientWithCount> getCodec(boolean allowEmpty) {
            return allowEmpty ? ALLOW_EMPTY_CODEC : DISALLOW_EMPTY_CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, IngredientWithCount> getPacketCodec() {
            return PACKET_CODEC;
        }

        public static IngredientWithCount read(RegistryByteBuf buf) {
            Ingredient base = Ingredient.PACKET_CODEC.decode(buf);
            int count = buf.readInt();
            return new IngredientWithCount(base, count);
        }

        public static void write(RegistryByteBuf buf, IngredientWithCount ingredient) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient.ingredient);
            buf.writeInt(ingredient.count);
        }
    }
}

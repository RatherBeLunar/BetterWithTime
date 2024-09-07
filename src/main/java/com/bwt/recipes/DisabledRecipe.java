package com.bwt.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record DisabledRecipe(String group) implements Recipe<RecipeInput> {
    public DisabledRecipe() {
        this("");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.AIR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.DISABLED_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(@Nullable RecipeInput input, World world) {
        return false;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.DISABLED_RECIPE_TYPE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<DisabledRecipe> {
        public static final MapCodec<DisabledRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance
                        .group(Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group))
                        .apply(instance, DisabledRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, DisabledRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<DisabledRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, DisabledRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static DisabledRecipe read(RegistryByteBuf buf) {
            String group = buf.readString();
            return new DisabledRecipe(group);
        }

        private static void write(RegistryByteBuf buf, DisabledRecipe recipe) {
            buf.writeString(recipe.group);
        }
    }

    public interface RecipeFactory<T extends DisabledRecipe> {
        T create(String group);
    }
}

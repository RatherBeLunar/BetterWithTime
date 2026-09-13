package com.bwt.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public record DisabledRecipe(String group) implements Recipe<RecipeInput> {
    public DisabledRecipe() {
        this("");
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.AIR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.DISABLED_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(@Nullable RecipeInput input, Level level) {
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
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
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<DisabledRecipe> {
        public static final MapCodec<DisabledRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance
                        .group(Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group))
                        .apply(instance, DisabledRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, DisabledRecipe> PACKET_CODEC = StreamCodec.of(
                Serializer::write, Serializer::read
        );

        public Serializer() {
        }

        @Override
        public MapCodec<DisabledRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DisabledRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        private static DisabledRecipe read(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            return new DisabledRecipe(group);
        }

        private static void write(RegistryFriendlyByteBuf buf, DisabledRecipe recipe) {
            buf.writeUtf(recipe.group);
        }
    }

    public interface RecipeFactory<T extends DisabledRecipe> {
        T create(String group);
    }
}

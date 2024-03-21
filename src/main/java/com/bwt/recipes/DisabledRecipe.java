package com.bwt.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record DisabledRecipe(String group) implements Recipe<Inventory> {

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.AIR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.DISABLED_RECIPE_SERIALIZER;
    }

    @Override
    public boolean matches(@Nullable Inventory inventory, World world) {
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
    public ItemStack craft(@Nullable Inventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<DisabledRecipe> {
        private final DisabledRecipe.RecipeFactory<DisabledRecipe> recipeFactory;
        private final Codec<DisabledRecipe> codec;

        public Serializer(DisabledRecipe.RecipeFactory<DisabledRecipe> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.create(
                    instance->instance.group(
                            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "")
                                    .forGetter(recipe -> recipe.group)
                    ).apply(instance, recipeFactory::create)
            );
        }

        @Override
        public Codec<DisabledRecipe> codec() {
            return codec;
        }

        @Override
        public DisabledRecipe read(PacketByteBuf buf) {
            String group = buf.readString();
            return this.recipeFactory.create(group);
        }

        @Override
        public void write(PacketByteBuf buf, DisabledRecipe recipe) {
            buf.writeString(recipe.group);
        }
    }

    public interface RecipeFactory<T extends DisabledRecipe> {
        T create(String group);
    }
}

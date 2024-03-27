package com.bwt.recipes.soul_forge;

import com.bwt.recipes.BwtRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class SoulForgeShapedRecipe implements CraftingRecipe {
    protected final SoulForgeRawShapedRecipe raw;
    protected final ItemStack result;
    protected final String group;
    protected final CraftingRecipeCategory category;
    protected final boolean showNotification;

    public SoulForgeShapedRecipe(String group, CraftingRecipeCategory category, SoulForgeRawShapedRecipe raw, ItemStack result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.raw = raw;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.SOUL_FORGE_SHAPED_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BwtRecipes.SOUL_FORGE_SHAPED_RECIPE_TYPE;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
        return this.result;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.raw.ingredients();
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.raw.width() && height >= this.raw.height();
    }

    @Override
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        return this.raw.matches(recipeInputInventory);
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        return this.getResult(dynamicRegistryManager).copy();
    }

    public int getWidth() {
        return this.raw.width();
    }

    public int getHeight() {
        return this.raw.height();
    }

    @Override
    public boolean isEmpty() {
        DefaultedList<Ingredient> defaultedList = this.getIngredients();
        return defaultedList.isEmpty() || defaultedList.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ingredient -> ingredient.getMatchingStacks().length == 0);
    }

    public static class Serializer implements RecipeSerializer<SoulForgeShapedRecipe> {
        public static final Codec<SoulForgeShapedRecipe> CODEC = RecordCodecBuilder
                .create(instance -> instance.group(
                        Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
                        CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.category),
                        SoulForgeRawShapedRecipe.CODEC.forGetter(recipe -> recipe.raw),
                        (ItemStack.RECIPE_RESULT_CODEC.fieldOf("result")).forGetter(recipe -> recipe.result),
                        Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "show_notification", true).forGetter(recipe -> recipe.showNotification)
                ).apply(instance, SoulForgeShapedRecipe::new));

        @Override
        public Codec<SoulForgeShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public SoulForgeShapedRecipe read(PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
            SoulForgeRawShapedRecipe rawShapedRecipe = SoulForgeRawShapedRecipe.readFromBuf(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            boolean bl = packetByteBuf.readBoolean();
            return new SoulForgeShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, SoulForgeShapedRecipe shapedRecipe) {
            packetByteBuf.writeString(shapedRecipe.group);
            packetByteBuf.writeEnumConstant(shapedRecipe.category);
            shapedRecipe.raw.writeToBuf(packetByteBuf);
            packetByteBuf.writeItemStack(shapedRecipe.result);
            packetByteBuf.writeBoolean(shapedRecipe.showNotification);
        }
    }
}


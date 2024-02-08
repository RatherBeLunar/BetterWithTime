package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.cauldron.CauldronBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;

public class CauldronRecipe extends AbstractCookingPotRecipe<CauldronBlockEntity> {
    public CauldronRecipe(String group, CookingRecipeCategory category, DefaultedList<IngredientWithCount> ingredients, ItemStack result) {
        super(BwtRecipes.CAULDRON_RECIPE_TYPE, group, category, ingredients, result);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.cauldronBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.CAULDRON_RECIPE_SERIALIZER;
    }
}



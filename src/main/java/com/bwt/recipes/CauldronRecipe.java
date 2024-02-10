package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;

import java.util.List;

public class CauldronRecipe extends AbstractCookingPotRecipe {
    public CauldronRecipe(String group, CookingRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.CAULDRON_RECIPE_TYPE, group, category, ingredients, results);
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



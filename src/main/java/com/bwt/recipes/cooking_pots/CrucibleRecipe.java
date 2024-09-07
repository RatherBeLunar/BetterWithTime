package com.bwt.recipes.cooking_pots;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.List;

public class CrucibleRecipe extends AbstractCookingPotRecipe {
    public CrucibleRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.CRUCIBLE_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.crucibleBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.CRUCIBLE_RECIPE_SERIALIZER;
    }

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<CrucibleRecipe> {
        public static CrucibleRecipe.JsonBuilder create() {
            return (CrucibleRecipe.JsonBuilder) new CrucibleRecipe.JsonBuilder().category(RecipeCategory.MISC);
        }

        public static CrucibleRecipe.JsonBuilder createFood() {
            return (CrucibleRecipe.JsonBuilder) create().category(RecipeCategory.FOOD).cookingCategory(CookingPotRecipeCategory.FOOD);
        }

        @Override
        protected RecipeFactory<CrucibleRecipe> getRecipeFactory() {
            return CrucibleRecipe::new;
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, RecipeProvider.getItemPath(results.get(0).getItem()) + "_from_crucible");
        }
    }
}



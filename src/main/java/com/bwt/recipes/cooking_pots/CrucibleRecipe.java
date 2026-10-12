package com.bwt.recipes.cooking_pots;

import com.bwt.blocks.BwtBlocks;
import com.bwt.generation.SharedRecipeProvider;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CrucibleRecipe extends AbstractCookingPotRecipe {
    public CrucibleRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.CRUCIBLE_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public RecipeSerializer<? extends AbstractCookingPotRecipe> getSerializer() {
        return BwtRecipes.CRUCIBLE_RECIPE_SERIALIZER;
    }

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<CrucibleRecipe> {
        protected JsonBuilder(SharedRecipeProvider recipeProvider) {
            super(recipeProvider);
        }

        public static CrucibleRecipe.JsonBuilder create(SharedRecipeProvider recipeProvider) {
            return (CrucibleRecipe.JsonBuilder) new CrucibleRecipe.JsonBuilder(recipeProvider).category(RecipeCategory.MISC);
        }

        public static CrucibleRecipe.JsonBuilder createFood(SharedRecipeProvider recipeProvider) {
            return (CrucibleRecipe.JsonBuilder) create(recipeProvider).category(RecipeCategory.FOOD).cookingCategory(CookingPotRecipeCategory.FOOD);
        }

        @Override
        protected RecipeFactory<CrucibleRecipe> getRecipeFactory() {
            return CrucibleRecipe::new;
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, RecipeProvider.getItemName(results.getFirst().getItem()) + "_from_crucible");
        }
    }
}



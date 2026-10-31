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
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public class CauldronRecipe extends AbstractCookingPotRecipe {
    public CauldronRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.CAULDRON_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public RecipeSerializer<? extends AbstractCookingPotRecipe> getSerializer() {
        return BwtRecipes.CAULDRON_RECIPE_SERIALIZER;
    }

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<CauldronRecipe> {
        protected JsonBuilder(SharedRecipeProvider recipeProvider) {
            super(recipeProvider);
        }

        public static CauldronRecipe.JsonBuilder create(SharedRecipeProvider recipeProvider) {
            return (CauldronRecipe.JsonBuilder) new CauldronRecipe.JsonBuilder(recipeProvider).category(RecipeCategory.MISC);
        }

        public static CauldronRecipe.JsonBuilder createFood(SharedRecipeProvider recipeProvider) {
            return (CauldronRecipe.JsonBuilder) create(recipeProvider).category(RecipeCategory.FOOD).cookingCategory(CookingPotRecipeCategory.FOOD);
        }

        @Override
        protected RecipeFactory<CauldronRecipe> getRecipeFactory() {
            return CauldronRecipe::new;
        }

        @Override
        public void save(RecipeOutput exporter) {
            this.save(exporter, RecipeProvider.getItemName(results.getFirst().getItem()) + "_from_cauldron");
        }
    }
}



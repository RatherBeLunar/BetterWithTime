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

public class CauldronRecipe extends AbstractCookingPotRecipe {
    public CauldronRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
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

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<CauldronRecipe> {
        public static CauldronRecipe.JsonBuilder create() {
            return (CauldronRecipe.JsonBuilder) new CauldronRecipe.JsonBuilder().category(RecipeCategory.MISC);
        }

        public static CauldronRecipe.JsonBuilder createFood() {
            return (CauldronRecipe.JsonBuilder) create().category(RecipeCategory.FOOD).cookingCategory(CookingPotRecipeCategory.FOOD);
        }

        @Override
        protected RecipeFactory<CauldronRecipe> getRecipeFactory() {
            return CauldronRecipe::new;
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, RecipeProvider.getItemPath(results.get(0).getItem()) + "_from_cauldron");
        }
    }
}



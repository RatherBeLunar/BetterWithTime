package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

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

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<CauldronRecipe> {
        public static JsonBuilder create() {
            return ((JsonBuilder) new JsonBuilder().category(RecipeCategory.MISC));
        }

        public static JsonBuilder createFood() {
            return (JsonBuilder) create().category(RecipeCategory.FOOD).cookingCategory(CookingRecipeCategory.FOOD);
        }

        @Override
        RecipeFactory<CauldronRecipe> getRecipeFactory() {
            return CauldronRecipe::new;
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, RecipeProvider.getItemPath(results.get(0).getItem()) + "_from_cauldron");
        }
    }
}



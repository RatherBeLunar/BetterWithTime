package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.List;

public class StokedCrucibleRecipe extends AbstractCookingPotRecipe {
    public StokedCrucibleRecipe(String group, CookingRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.crucibleBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.STOKED_CRUCIBLE_RECIPE_SERIALIZER;
    }

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<StokedCrucibleRecipe> {
        public static JsonBuilder create() {
            return ((JsonBuilder) new JsonBuilder().category(RecipeCategory.MISC));
        }

        @Override
        RecipeFactory<StokedCrucibleRecipe> getRecipeFactory() {
            return StokedCrucibleRecipe::new;
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "smelt_" + RecipeProvider.getItemPath(ingredients.get(0).getMatchingStacks().get(0).getItem()) + "_in_crucible");
        }
    }
}



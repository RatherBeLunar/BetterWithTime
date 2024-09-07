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

public class StokedCrucibleRecipe extends AbstractCookingPotRecipe {
    public StokedCrucibleRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
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
        public static StokedCrucibleRecipe.JsonBuilder create() {
            return (StokedCrucibleRecipe.JsonBuilder) new StokedCrucibleRecipe.JsonBuilder().category(RecipeCategory.MISC);
        }

        @Override
        protected RecipeFactory<StokedCrucibleRecipe> getRecipeFactory() {
            return StokedCrucibleRecipe::new;
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, "bwt:smelt_" + RecipeProvider.getItemPath(ingredients.get(0).getMatchingStacks().get(0).getItem()) + "_in_crucible");
        }
    }
}



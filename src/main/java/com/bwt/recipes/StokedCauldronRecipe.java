package com.bwt.recipes;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.List;

public class StokedCauldronRecipe extends AbstractCookingPotRecipe {
    public StokedCauldronRecipe(String group, CookingRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.STOKED_CAULDRON_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(BwtBlocks.cauldronBlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BwtRecipes.STOKED_CAULDRON_RECIPE_SERIALIZER;
    }

    public static class JsonBuilder extends AbstractCookingPotRecipe.JsonBuilder<StokedCauldronRecipe> {
        public static JsonBuilder create() {
            return ((JsonBuilder) new JsonBuilder().category(RecipeCategory.MISC));
        }

        @Override
        RecipeFactory<StokedCauldronRecipe> getRecipeFactory() {
            return StokedCauldronRecipe::new;
        }

        @Override
        public void offerTo(RecipeExporter exporter) {
            this.offerTo(exporter, RecipeProvider.getItemPath(results.get(0).getItem()) + "_from_stoked_cauldron");
        }
    }
}



package com.bwt.recipes.cooking_pots;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import java.util.List;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class StokedCrucibleRecipe extends AbstractCookingPotRecipe {
    public StokedCrucibleRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public ItemStack getToastSymbol() {
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
        public void save(RecipeOutput exporter) {
            this.save(exporter, Id.of("smelt_" + RecipeProvider.getItemName(ingredients.get(0).getMatchingStacks().get(0).getItem()) + "_in_crucible"));
        }
    }
}



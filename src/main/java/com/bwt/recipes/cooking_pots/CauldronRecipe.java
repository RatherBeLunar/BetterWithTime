package com.bwt.recipes.cooking_pots;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.IngredientWithCount;
import java.util.List;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CauldronRecipe extends AbstractCookingPotRecipe {
    public CauldronRecipe(String group, CookingPotRecipeCategory category, List<IngredientWithCount> ingredients, List<ItemStack> results) {
        super(BwtRecipes.CAULDRON_RECIPE_TYPE, group, category, ingredients, results);
    }

    @Override
    public ItemStack getToastSymbol() {
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
        public void save(RecipeOutput exporter) {
            this.save(exporter, RecipeProvider.getItemName(results.get(0).getItem()) + "_from_cauldron");
        }
    }
}



package com.bwt.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public interface IngredientsWithCountRecipe<T extends RecipeInput> extends Recipe<T> {
    List<IngredientWithCount> getIngredientsWithCount();
}

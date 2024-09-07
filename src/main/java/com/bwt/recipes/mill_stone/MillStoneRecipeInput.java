package com.bwt.recipes.mill_stone;

import com.bwt.recipes.IngredientWithCount;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record MillStoneRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Override
    public int getSize() {
        return items.size();
    }
}

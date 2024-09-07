package com.bwt.recipes.hopper_filter;

import com.bwt.recipes.IngredientWithCount;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record HopperFilterRecipeInput(Item filterItem, ItemStack itemStack) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? filterItem.getDefaultStack() : itemStack;
    }

    @Override
    public int getSize() {
        return 2;
    }
}

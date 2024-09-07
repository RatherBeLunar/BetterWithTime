package com.bwt.recipes.block_dispenser_clump;

import com.bwt.recipes.IngredientWithCount;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record BlockDispenserClumpRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Override
    public int getSize() {
        return items.size();
    }
}

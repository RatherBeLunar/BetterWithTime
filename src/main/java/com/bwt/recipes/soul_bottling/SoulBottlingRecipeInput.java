package com.bwt.recipes.soul_bottling;

import com.bwt.recipes.IngredientWithCount;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record SoulBottlingRecipeInput(Block block) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return block.asItem().getDefaultStack();
    }

    @Override
    public int getSize() {
        return 1;
    }
}
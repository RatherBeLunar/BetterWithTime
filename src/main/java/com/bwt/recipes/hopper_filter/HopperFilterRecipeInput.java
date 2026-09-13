package com.bwt.recipes.hopper_filter;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record HopperFilterRecipeInput(Item filterItem, ItemStack itemStack) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? filterItem.getDefaultInstance() : itemStack;
    }

    @Override
    public int size() {
        return 2;
    }
}

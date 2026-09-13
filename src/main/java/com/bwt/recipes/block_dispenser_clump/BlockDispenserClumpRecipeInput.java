package com.bwt.recipes.block_dispenser_clump;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record BlockDispenserClumpRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public int size() {
        return items.size();
    }
}

package com.bwt.recipes.saw;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;

public record SawRecipeInput(Block block) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return block.asItem().getDefaultInstance();
    }

    @Override
    public int size() {
        return 1;
    }
}

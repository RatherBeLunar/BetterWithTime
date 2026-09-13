package com.bwt.recipes.turntable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;

public record TurntableRecipeInput(Block block) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return block.asItem().getDefaultInstance();
    }

    @Override
    public int size() {
        return 1;
    }
}

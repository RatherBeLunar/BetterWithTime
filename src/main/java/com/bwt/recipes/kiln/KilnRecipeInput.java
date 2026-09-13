package com.bwt.recipes.kiln;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;

public record KilnRecipeInput(Block block) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return block.asItem().getDefaultInstance();
    }

    @Override
    public int size() {
        return 1;
    }
}

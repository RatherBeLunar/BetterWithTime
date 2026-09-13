package com.bwt.blocks.block_dispenser.behavior.inhale;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;

public class VoidInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockSource blockPointer) {
        return ItemStack.EMPTY;
    }

    @Override
    public void inhale(BlockSource blockPointer) {
        breakBlockNoItems(blockPointer);
    }
}

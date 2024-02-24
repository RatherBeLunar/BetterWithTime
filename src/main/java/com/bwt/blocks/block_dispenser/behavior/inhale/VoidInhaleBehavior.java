package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.behavior.inhale.BlockInhaleBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public class VoidInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        return ItemStack.EMPTY;
    }

    @Override
    public void inhale(BlockPointer blockPointer) {
        breakBlockNoItems(blockPointer.world(), blockPointer.pos());
    }
}

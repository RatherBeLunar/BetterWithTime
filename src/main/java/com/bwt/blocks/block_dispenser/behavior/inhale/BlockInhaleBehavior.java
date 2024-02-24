package com.bwt.blocks.block_dispenser.behavior.inhale;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

public interface BlockInhaleBehavior {
    BlockInhaleBehavior NOOP = new BlockInhaleBehavior() {
        @Override
        public ItemStack getInhaledItems(BlockPointer blockPointer) {
            return ItemStack.EMPTY;
        }
        @Override
        public void inhale(BlockPointer blockPointer) {}
    };

    ItemStack getInhaledItems(BlockPointer blockPointer);

    void inhale(BlockPointer blockPointer);

    default void breakBlockNoItems(ServerWorld world, BlockPos pos) {
        world.breakBlock(pos, false);
    }
}

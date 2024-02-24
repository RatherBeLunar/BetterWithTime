package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;

public class DefaultBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        Item item = blockPointer.world().getBlockState(blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING))).getBlock().asItem();
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, 1);
    }

    @Override
    public void inhale(BlockPointer blockPointer) {
        breakBlockNoItems(blockPointer.world(), blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING)));
    }
}

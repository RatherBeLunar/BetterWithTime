package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

public class DefaultItemDispenserBehavior extends ItemDispenserBehavior {
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(BlockDispenserBlock.FACING);
        Position position = BlockDispenserBlock.getOutputLocation(pointer);
        ItemStack itemStack = stack.copyWithCount(1);
        ItemDispenserBehavior.spawnItem(pointer.world(), itemStack, 6, direction, position);
        return itemStack;
    }
}

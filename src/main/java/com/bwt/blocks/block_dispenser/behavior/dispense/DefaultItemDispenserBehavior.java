package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;

public class DefaultItemDispenserBehavior extends DefaultDispenseItemBehavior {
    protected ItemStack execute(BlockSource pointer, ItemStack stack) {
        Direction direction = pointer.state().getValue(BlockDispenserBlock.FACING);
        Position position = BlockDispenserBlock.getDispensePosition(pointer);
        ItemStack itemStack = stack.copyWithCount(1);
        DefaultDispenseItemBehavior.spawnItem(pointer.level(), itemStack, 6, direction, position);
        return itemStack;
    }
}

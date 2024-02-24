package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import com.mojang.logging.LogUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;

public class BlockDispenserBehavior extends BlockPlacementDispenserBehavior {
    protected static final Logger LOGGER = LogUtils.getLogger();

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(false);
        Item item = stack.getItem();
        ItemStack placementStack = stack.copyWithCount(1);
        ItemStack returnStack = stack.copyWithCount(1);
        if (item instanceof BlockItem blockItem) {
            Direction direction = pointer.state().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);

            try {
                BlockDispenserPlacementContext context = new BlockDispenserPlacementContext(pointer.world(), blockPos, direction, placementStack, direction);
                setSuccess(blockItem.place(context).isAccepted());
            } catch (Exception exception) {
                LOGGER.error("Error trying to place block at {}", blockPos, exception);
            }
        }
        if (isSuccess()) {
            return returnStack;
        }
        else {
            return ItemStack.EMPTY;
        }
    }
}

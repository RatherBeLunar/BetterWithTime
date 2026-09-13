package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;

public class BlockDispenserBehavior extends ShulkerBoxDispenseBehavior {
    protected static final Logger LOGGER = LogUtils.getLogger();

    public static final BlockDispenserBehavior DEFAULT = new BlockDispenserBehavior();

    final boolean dropIfPlacementFails;
    public BlockDispenserBehavior(boolean dropIfPlacementFails) {
        super();
        this.dropIfPlacementFails = dropIfPlacementFails;
    }

    public BlockDispenserBehavior() {
        this(false);
    }

    @Override
    protected ItemStack execute(BlockSource pointer, ItemStack stack) {
        this.setSuccess(false);
        Item item = stack.getItem();
        ItemStack placementStack = stack.copyWithCount(1);
        ItemStack returnStack = stack.copyWithCount(1);
        if (item instanceof BlockItem blockItem) {
            Direction direction = pointer.state().getValue(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().relative(direction);

            try {
                BlockDispenserPlacementContext context = new BlockDispenserPlacementContext(pointer.level(), blockPos, direction, placementStack, direction);
                boolean accepted = blockItem.place(context).consumesAction();
                setSuccess(accepted);
                if (!accepted && dropIfPlacementFails) {
                    new DefaultItemDispenserBehavior().execute(pointer, returnStack);
                    setSuccess(true);
                }
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

    public static void registerBehaviors() {
    }
}

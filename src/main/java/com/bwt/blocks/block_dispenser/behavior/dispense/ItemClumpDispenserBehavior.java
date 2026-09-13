package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class ItemClumpDispenserBehavior extends BlockDispenserBehavior {
    public final BlockDispenserClumpRecipe recipe;
    public final Item clumpItem;

    public ItemClumpDispenserBehavior(BlockDispenserClumpRecipe recipe, Item clumpItem) {
        this.recipe = recipe;
        this.clumpItem = clumpItem;
    }

    @Override
    protected ItemStack execute(BlockSource pointer, ItemStack stack) {
        this.setSuccess(false);
        Item item = recipe.block().getItem();
        if (item instanceof BlockItem blockItem) {
            Direction direction = pointer.state().getValue(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().relative(direction);

            try {
                BlockDispenserPlacementContext context = new BlockDispenserPlacementContext(pointer.level(), blockPos, direction, recipe.block().copy(), direction);
                setSuccess(blockItem.place(context).consumesAction());
            } catch (Exception exception) {
                LOGGER.error("Error trying to place block at {}", blockPos, exception);
            }
        }
        return new ItemStack(clumpItem, recipe.getItemCount());
    }
}

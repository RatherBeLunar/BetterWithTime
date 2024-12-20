package com.bwt.blocks.block_dispenser.behavior.dispense;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ItemClumpDispenserBehavior extends BlockDispenserBehavior {
    public BlockDispenserClumpRecipe recipe;
    public Item clumpItem;

    public ItemClumpDispenserBehavior(BlockDispenserClumpRecipe recipe, Item clumpItem) {
        this.recipe = recipe;
        this.clumpItem = clumpItem;
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(false);
        Item item = recipe.block().getItem();
        if (item instanceof BlockItem blockItem) {
            Direction direction = pointer.state().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);

            try {
                BlockDispenserPlacementContext context = new BlockDispenserPlacementContext(pointer.world(), blockPos, direction, recipe.block().copy(), direction);
                setSuccess(blockItem.place(context).isAccepted());
            } catch (Exception exception) {
                LOGGER.error("Error trying to place block at {}", blockPos, exception);
            }
        }
        return new ItemStack(clumpItem, recipe.getItemCount());
    }
}

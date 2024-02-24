package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPointer;

import java.util.List;

public class CropInhaleBehavior implements BlockInhaleBehavior {
    private boolean noop = false;

    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        BlockState state = blockPointer.world().getBlockState(blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING)));
        if (!(state.getBlock() instanceof CropBlock cropBlock)) {
            return ItemStack.EMPTY;
        }
        int age = cropBlock.getAge(state);
        int maxAge = cropBlock.getMaxAge();
        if (state.isOf(BwtBlocks.hempCropBlock)) {
            // Bottom hemp blocks stay at age 7 out of 8 to keep growing.
            // allow those pieces to be inhaled as well
            maxAge -= 1;
        }
        if (age < maxAge) {
            noop = true;
            return ItemStack.EMPTY;
        }

        // pretend like we're mining with silk touch
        ItemStack toolStack = new ItemStack(Items.DIAMOND_AXE);
        toolStack.addEnchantment(Enchantments.SILK_TOUCH, 1);
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(blockPointer.world())
                .add(LootContextParameters.ORIGIN, blockPointer.pos().toCenterPos())
                .add(LootContextParameters.TOOL, toolStack);
        List<ItemStack> droppedStacks = state.getDroppedStacks(builder);
        if (droppedStacks.size() > 1) {
            return droppedStacks.stream()
                    .filter(dropStack -> !dropStack.getItem().equals(cropBlock.getPickStack(blockPointer.world(), blockPointer.pos(), state).getItem()))
                    .findAny().orElse(ItemStack.EMPTY);
        }
        else if (droppedStacks.size() == 1) {
            return droppedStacks.get(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void inhale(BlockPointer blockPointer) {
        if (!noop) {
            breakBlockNoItems(blockPointer.world(), blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING)));
        }
    }
}

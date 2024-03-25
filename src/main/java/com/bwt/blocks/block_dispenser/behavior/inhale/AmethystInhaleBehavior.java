package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AmethystInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        BlockState state = blockPointer.world().getBlockState(blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING)));
        if (!(state.isOf(Blocks.AMETHYST_CLUSTER))) {
            return ItemStack.EMPTY;
        }

        // pretend like we're mining with silk touch
        ItemStack toolStack = new ItemStack(Items.NETHERITE_PICKAXE);
        toolStack.addEnchantment(Enchantments.SILK_TOUCH, 1);
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(blockPointer.world())
                .add(LootContextParameters.ORIGIN, blockPointer.pos().toCenterPos())
                .add(LootContextParameters.TOOL, toolStack);
        List<ItemStack> droppedStacks = state.getDroppedStacks(builder);
        if (droppedStacks.size() > 1) {
            return droppedStacks.stream()
                    .filter(dropStack -> !dropStack.getItem().equals(state.getBlock().getPickStack(blockPointer.world(), blockPointer.pos(), state).getItem()))
                    .findAny().orElse(ItemStack.EMPTY);
        }
        else if (droppedStacks.size() == 1) {
            return droppedStacks.get(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void inhale(BlockPointer blockPointer) {
        BlockPos facingPos = blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING));
        BlockState facingState = blockPointer.world().getBlockState(facingPos);
        if (!(facingState.isOf(Blocks.AMETHYST_CLUSTER))) {
            return;
        }
        breakBlockNoItems(blockPointer.world(), facingState, facingPos);
    }
}

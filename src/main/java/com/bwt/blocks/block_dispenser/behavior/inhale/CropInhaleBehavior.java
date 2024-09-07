package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CropInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = blockPointer.world().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        BlockState state = blockPointer.world().getBlockState(blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING)));
        if (!(state.getBlock() instanceof CropBlock cropBlock)) {
            return ItemStack.EMPTY;
        }
        int age = cropBlock.getAge(state);
        int maxAge = cropBlock.getMaxAge();
        if (age < maxAge) {
            return ItemStack.EMPTY;
        }

        // pretend like we're mining with silk touch
        ItemStack toolStack = new ItemStack(Items.NETHERITE_PICKAXE);
        RegistryEntry.Reference<Enchantment> silkTouch = enchantmentRegistry.getOrThrow(Enchantments.SILK_TOUCH);
        toolStack.addEnchantment(silkTouch, 1);
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
        BlockPos facingPos = blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING));
        BlockState facingState = blockPointer.world().getBlockState(facingPos);
        if (!(facingState.getBlock() instanceof CropBlock cropBlock)) {
            return;
        }
        int age = cropBlock.getAge(facingState);
        int maxAge = cropBlock.getMaxAge();
        if (age < maxAge) {
            return;
        }
        breakBlockNoItems(blockPointer.world(), facingState, facingPos);
    }
}

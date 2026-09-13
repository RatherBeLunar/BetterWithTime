package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class CocoaBeanInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockSource blockPointer) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = blockPointer.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        BlockState state = blockPointer.level().getBlockState(blockPointer.pos().relative(blockPointer.state().getValue(BlockDispenserBlock.FACING)));
        if (!(state.getBlock() instanceof CocoaBlock cocoaBlock)) {
            return ItemStack.EMPTY;
        }
        int age = state.getValue(CocoaBlock.AGE);
        int maxAge = CocoaBlock.MAX_AGE;
        if (age < maxAge) {
            return ItemStack.EMPTY;
        }

        // pretend like we're mining with silk touch
        ItemStack toolStack = new ItemStack(Items.NETHERITE_AXE);
        Holder.Reference<Enchantment> silkTouch = enchantmentRegistry.getOrThrow(Enchantments.SILK_TOUCH);
        toolStack.enchant(silkTouch, 1);
        LootParams.Builder builder = new LootParams.Builder(blockPointer.level())
                .withParameter(LootContextParams.ORIGIN, blockPointer.pos().getCenter())
                .withParameter(LootContextParams.TOOL, toolStack);
        List<ItemStack> droppedStacks = state.getDrops(builder);
        if (droppedStacks.size() > 1) {
            return droppedStacks.stream()
                    .filter(dropStack -> !dropStack.getItem().equals(cocoaBlock.getCloneItemStack(blockPointer.level(), blockPointer.pos(), state).getItem()))
                    .findAny().orElse(ItemStack.EMPTY);
        }
        else if (droppedStacks.size() == 1) {
            return droppedStacks.get(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void inhale(BlockSource blockPointer) {
        BlockPos facingPos = blockPointer.pos().relative(blockPointer.state().getValue(BlockDispenserBlock.FACING));
        BlockState facingState = blockPointer.level().getBlockState(facingPos);
        if (!(facingState.getBlock() instanceof CocoaBlock cocoaBlock)) {
            return;
        }
        int age = facingState.getValue(CocoaBlock.AGE);
        int maxAge = CocoaBlock.MAX_AGE;
        if (age < maxAge) {
            return;
        }
        breakBlockNoItems(blockPointer.level(), facingState, facingPos);
    }
}

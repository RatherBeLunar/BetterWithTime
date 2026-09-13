package com.bwt.utils;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.ticks.ContainerSingleItem;

public class SimpleSingleStackInventory implements ContainerSingleItem.BlockContainerSingleItem {
    final int maxStackSize;
    protected ItemStack stack = ItemStack.EMPTY;

    public SimpleSingleStackInventory(int maxStackSize) {
        super();
        this.maxStackSize = maxStackSize;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public ItemStack getTheItem() {
        return this.stack;
    }

    @Override
    public ItemStack splitTheItem(int count) {
        ItemStack itemStack = this.stack.split(count);
        if (this.stack.isEmpty()) {
            this.stack = ItemStack.EMPTY;
        }
        return itemStack;
    }

    @Override
    public void setTheItem(ItemStack stack) {
        this.stack = stack.copyWithCount(getMaxStackSize());
        stack.shrink(getMaxStackSize());
    }

    @Override
    public BlockEntity getContainerBlockEntity() {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (getContainerBlockEntity() != null) {
            return BlockContainerSingleItem.super.stillValid(player);
        }
        return true;
    }

    public void readNbt(CompoundTag nbtCompound, HolderLookup.Provider registryLookup) {
        this.clearContent();
        if (nbtCompound.isEmpty()) {
            return;
        }
        Optional<ItemStack> itemStack = ItemStack.parse(registryLookup, nbtCompound);
        itemStack.ifPresent(this::setTheItem);
    }

    public Tag toNbt(HolderLookup.Provider registryLookup) {
        ItemStack itemStack = this.getTheItem();
        if (itemStack.isEmpty()) return new CompoundTag();
        return itemStack.save(registryLookup);
    }
}

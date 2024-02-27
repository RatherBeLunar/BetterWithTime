package com.bwt.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SimpleSingleStackInventory implements SingleStackInventory {
    int maxStackSize;
    protected ItemStack stack = ItemStack.EMPTY;

    public SimpleSingleStackInventory(int maxStackSize) {
        super();
        this.maxStackSize = maxStackSize;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public int getMaxCountPerStack() {
        return maxStackSize;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        ItemStack itemStack = this.stack.split(count);
        if (this.stack.isEmpty()) {
            this.stack = ItemStack.EMPTY;
        }
        return itemStack;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.stack = stack.copyWithCount(getMaxCountPerStack());
        stack.decrement(getMaxCountPerStack());
    }

    @Override
    public BlockEntity asBlockEntity() {
        return null;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (asBlockEntity() != null) {
            return SingleStackInventory.super.canPlayerUse(player);
        }
        return true;
    }

    public void readNbt(NbtCompound nbtCompound) {
        this.clear();
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
        if (itemStack.isEmpty()) return;
        this.setStack(itemStack);
    }

    public NbtCompound toNbt() {
        ItemStack itemStack = this.getStack();
        if (itemStack.isEmpty()) return new NbtCompound();
        return itemStack.writeNbt(new NbtCompound());
    }
}

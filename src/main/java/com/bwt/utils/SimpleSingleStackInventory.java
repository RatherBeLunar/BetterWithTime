package com.bwt.utils;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    public void readData(ValueInput valueInput, String key) {
        this.clearContent();
        Optional<ItemStack> itemStack = valueInput.read(key, ItemStack.CODEC);
        itemStack.ifPresent(this::setTheItem);
    }

    public void saveData(ValueOutput valueOutput, String key) {
        ItemStack itemStack = this.getTheItem();
        if (!itemStack.isEmpty()) {
            valueOutput.store(key, ItemStack.CODEC, itemStack);
        }
    }

    public Optional<Tag> toTag(HolderLookup.Provider provider) {
        ItemStack itemStack = this.getTheItem();
        if (itemStack.isEmpty()) return Optional.empty();
        RegistryOps<Tag> registryOps = provider.createSerializationContext(NbtOps.INSTANCE);
        return Optional.of(ItemStack.CODEC.encodeStart(registryOps, getTheItem()).getOrThrow());
    }
}

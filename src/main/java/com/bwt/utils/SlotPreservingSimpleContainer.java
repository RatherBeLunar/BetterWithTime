package com.bwt.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SlotPreservingSimpleContainer extends SimpleContainer {
    public SlotPreservingSimpleContainer(int size) {
        super(size);
    }

    @Override
    public void fromTag(ListTag listTag, HolderLookup.Provider provider) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag compoundTag = listTag.getCompoundOrEmpty(i);
            int j = compoundTag.getByteOr("Slot", (byte)0) & 255;
            if (j < this.getContainerSize()) {
                this.setItem(j, ItemStack.parse(provider, compoundTag).orElse(ItemStack.EMPTY));
            }
        }
    }

    @Override
    public ListTag createTag(HolderLookup.Provider provider) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemStack = this.getItem(i);
            if (itemStack.isEmpty()) continue;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)i);
            listTag.add(itemStack.save(provider, compoundTag));
        }
        return listTag;
    }
}

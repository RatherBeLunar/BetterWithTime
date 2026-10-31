package com.bwt.utils;

import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SlotPreservingSimpleContainer extends SimpleContainer {
    public SlotPreservingSimpleContainer(int size) {
        super(size);
    }

    public void fromSlots(ValueInput.TypedInputList<ItemStackWithSlot> typedInputList) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (ItemStackWithSlot itemStackWithSlot : typedInputList) {
            if (itemStackWithSlot.isValidInContainer(this.getContainerSize())) {
                this.setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
    }

    public void storeAsSlots(ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack itemStack = this.getItem(i);
            if (!itemStack.isEmpty()) {
                typedOutputList.add(new ItemStackWithSlot(i, itemStack));
            }
        }
    }
}

package com.bwt.blocks.mill_stone;

import com.bwt.BetterWithTime;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;

public class MillStoneScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private static final int SIZE = 3;
    private final PropertyDelegate propertyDelegate;

    public MillStoneScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(SIZE), new ArrayPropertyDelegate(1));
    }

    public MillStoneScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(BetterWithTime.millStoneScreenHandler, syncId);
        checkSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);
        this.addProperties(propertyDelegate);

        // Mill Stone inventory
        for (int m = 0; m < 3; ++m) {
            this.addSlot(new Slot(inventory, m, 8 + (m + 3) * 18, 43));
        }
        // Player inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 76 + m * 18));
            }
        }
        // Player hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 134));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < SIZE ? !this.insertItem(itemStack2, SIZE, 36 + SIZE, true) : !this.insertItem(itemStack2, 0, SIZE, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    public float getGrindProgress() {
        int grindProgress = this.propertyDelegate.get(0);
        int timeToGrind = MillStoneBlockEntity.timeToGrind;
        return MathHelper.clamp((float)grindProgress / (float)timeToGrind, 0.0f, 1.0f);
    }
}

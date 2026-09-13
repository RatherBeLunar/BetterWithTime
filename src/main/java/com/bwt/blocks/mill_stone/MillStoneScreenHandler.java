package com.bwt.blocks.mill_stone;

import com.bwt.BetterWithTime;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MillStoneScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;
    public static final int SIZE = 3;

    public MillStoneScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(SIZE), new SimpleContainerData(1));
    }

    public MillStoneScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(BetterWithTime.millStoneScreenHandler, syncId);
        checkContainerSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.startOpen(playerInventory.player);
        this.addDataSlots(propertyDelegate);

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
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot < SIZE ? !this.moveItemStackTo(itemStack2, SIZE, 36 + SIZE, true) : !this.moveItemStackTo(itemStack2, 0, SIZE, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTake(player, itemStack2);
        }
        return itemStack;
    }

    public float getGrindProgress() {
        int grindProgress = this.propertyDelegate.get(0);
        int timeToGrind = MillStoneBlockEntity.timeToGrind;
        return Mth.clamp((float)grindProgress / (float)timeToGrind, 0.0f, 1.0f);
    }
}

package com.bwt.blocks.mech_hopper;

import com.bwt.BetterWithTime;
import com.bwt.utils.SimpleSingleStackInventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MechHopperScreenHandler extends AbstractContainerMenu {
    private final SimpleSingleStackInventory filterInventory;
    private final Container inventory;
    private static final int SIZE = 19;
    private final ContainerData propertyDelegate;

    public MechHopperScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleSingleStackInventory(1), new SimpleContainer(SIZE - 1), new SimpleContainerData(1));
    }

    public MechHopperScreenHandler(int syncId, Inventory playerInventory, SimpleSingleStackInventory filterInventory, Container inventory, ContainerData propertyDelegate) {
        super(BetterWithTime.mechHopperScreenHandler, syncId);
        checkContainerSize(filterInventory, 1);
        checkContainerSize(inventory, SIZE - 1);
        this.filterInventory = filterInventory;
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.startOpen(playerInventory.player);
        this.addDataSlots(propertyDelegate);

        // Filter Slot
        this.addSlot(new FilterSlot(filterInventory, 0, 8 + 4 * 18, 37));

        int m;
        int l;
        // Hopper inventory
        for (m = 0; m < 2; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9, 8 + l * 18, 60 + m * 18));
            }
        }
        // Player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 111 + m * 18));
            }
        }
        // Player hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 169));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player) && filterInventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot < SIZE) {
                if (!this.moveItemStackTo(itemStack2, SIZE, 36 + SIZE, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                Slot slot0 = this.slots.get(0);
                if (slot0.mayPlace(itemStack2)) {
                    ItemStack result = slot0.safeInsert(itemStack2);
                    if (!result.isEmpty()) {
                        return itemStack2;
                    }
                }
                if (!this.moveItemStackTo(itemStack2, 1, SIZE, false)) {
                    return ItemStack.EMPTY;
                }
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

    public boolean isMechPowered() {
        return propertyDelegate.get(0) > 0;
    }

    protected static class FilterSlot extends Slot {
        public FilterSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.getItem().isEmpty() && MechHopperBlock.filterMap.containsKey(stack.getItem());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}

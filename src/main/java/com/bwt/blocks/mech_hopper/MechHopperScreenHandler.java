package com.bwt.blocks.mech_hopper;

import com.bwt.BetterWithTime;
import com.bwt.utils.SimpleSingleStackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class MechHopperScreenHandler extends ScreenHandler {
    private final SimpleSingleStackInventory filterInventory;
    private final Inventory inventory;
    private static final int SIZE = 19;
    private final PropertyDelegate propertyDelegate;

    public MechHopperScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleSingleStackInventory(1), new SimpleInventory(SIZE - 1), new ArrayPropertyDelegate(1));
    }

    public MechHopperScreenHandler(int syncId, PlayerInventory playerInventory, SimpleSingleStackInventory filterInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(BetterWithTime.mechHopperScreenHandler, syncId);
        checkSize(filterInventory, 1);
        checkSize(inventory, SIZE - 1);
        this.filterInventory = filterInventory;
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);
        this.addProperties(propertyDelegate);

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
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player) && filterInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < SIZE
                    ? !this.insertItem(itemStack2, SIZE, 36 + SIZE, true)
                    : !(this.slots.get(0).canInsert(itemStack2) && this.insertItem(itemStack2, 0, 1, false)) && !this.insertItem(itemStack2, 1, SIZE, false)) {
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

    public boolean isMechPowered() {
        return propertyDelegate.get(0) > 0;
    }

    protected static class FilterSlot extends Slot {
        public FilterSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return MechHopperBlock.filterMap.containsKey(stack.getItem());
        }
    }
}

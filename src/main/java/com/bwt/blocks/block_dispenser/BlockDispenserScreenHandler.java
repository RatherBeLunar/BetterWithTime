package com.bwt.blocks.block_dispenser;

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
import net.minecraft.screen.slot.SlotActionType;

public class BlockDispenserScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    private static final int SIZE = 16;

    private final PropertyDelegate propertyDelegate;

    public BlockDispenserScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(SIZE), new ArrayPropertyDelegate(1));
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public BlockDispenserScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(BetterWithTime.blockDispenserScreenHandler, syncId);
        checkSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);
        this.addProperties(propertyDelegate);

        int m;
        int l;
        // BD inventory
        for (m = 0; m < 4; ++m) {
            for (l = 0; l < 4; ++l) {
                this.addSlot(new Slot(inventory, l + m * 4, 53 + l * 18, 17 + m * 18));
            }
        }
        // Player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 102 + m * 18));
            }
        }
        // Player hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 160));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
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

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        setSelectedSlot(0);
    }

    public int getSelectedSlot() {
        return this.propertyDelegate.get(0);
    }

    public void setSelectedSlot(int slotToSelect) {
        this.propertyDelegate.set(0, slotToSelect);
    }
}



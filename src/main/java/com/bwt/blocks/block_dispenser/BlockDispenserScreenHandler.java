package com.bwt.blocks.block_dispenser;

import com.bwt.BetterWithTime;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BlockDispenserScreenHandler extends AbstractContainerMenu {
    private final Container inventory;

    private static final int SIZE = 16;

    private final ContainerData propertyDelegate;

    public BlockDispenserScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(SIZE), new SimpleContainerData(1));
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public BlockDispenserScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(BetterWithTime.blockDispenserScreenHandler, syncId);
        checkContainerSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.startOpen(playerInventory.player);
        this.addDataSlots(propertyDelegate);

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
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    // Shift + Player Inv Slot
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

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        super.clicked(slotIndex, button, actionType, player);
        setSelectedSlot(0);
    }

    public int getSelectedSlot() {
        return this.propertyDelegate.get(0);
    }

    public void setSelectedSlot(int slotToSelect) {
        this.propertyDelegate.set(0, slotToSelect);
    }
}



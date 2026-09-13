package com.bwt.blocks.pulley;

import com.bwt.BetterWithTime;
import com.bwt.items.BwtItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PulleyScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    private static final int SIZE = 4;
    private final ContainerData propertyDelegate;

    public PulleyScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(SIZE), new SimpleContainerData(1));
    }

    public PulleyScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(BetterWithTime.pulleyScreenHandler, syncId);
        checkContainerSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.startOpen(playerInventory.player);
        this.addDataSlots(propertyDelegate);

        // Pulley inventory
        for (int m = 0; m < 2; ++m) {
            for (int l = 0; l < 2; ++l) {
                this.addSlot(new Slot(inventory, m * 2 + l, 8 + 9 + (l + 3) * 18, 43 + m * 18));
            }
        }
        // Player inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 93 + m * 18));
            }
        }
        // Player hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 151));
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

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return stack.is(BwtItems.ropeItem);
    }

    public boolean isMechPowered() {
        return propertyDelegate.get(0) > 0;
    }
}

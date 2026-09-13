package com.bwt.blocks.abstract_cooking_pot;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractCookingPotScreenHandler extends AbstractContainerMenu {
    public static final int SIZE = 27;
    protected final Container inventory;
    protected final ContainerData propertyDelegate;

    protected final boolean isStoked;

    public AbstractCookingPotScreenHandler(
            MenuType<? extends AbstractCookingPotScreenHandler> screenHandlerType,
            int syncId,
            Inventory playerInventory,
            Container inventory,
            ContainerData propertyDelegate,
            AbstractCookingPotData cookingPotData
    ) {
        super(screenHandlerType, syncId);
        checkContainerSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.startOpen(playerInventory.player);
        this.addDataSlots(propertyDelegate);
        this.isStoked = cookingPotData.isStoked();

        int m;
        int l;
        // Cooking pot inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + m * 9, 8 + l * 18, 43 + m * 18));
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

    public float getCookProgress() {
        int cookProgress = this.propertyDelegate.get(0);
        int timeToCompleteCook = AbstractCookingPotBlockEntity.timeToCompleteCook;
        return Mth.clamp((float)cookProgress / (float)timeToCompleteCook, 0.0f, 1.0f);
    }

    public boolean isStoked() {
        return this.isStoked;
    }
}

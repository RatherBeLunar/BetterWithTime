package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.BetterWithTime;
import com.bwt.blocks.cauldron.CauldronBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractCookingPotScreenHandler extends ScreenHandler {
    public static final int SIZE = 27;
    protected final Inventory inventory;
    protected final PropertyDelegate propertyDelegate;

    public AbstractCookingPotScreenHandler(
            ScreenHandlerType<? extends AbstractCookingPotScreenHandler> screenHandlerType,
            int syncId,
            PlayerInventory playerInventory,
            Inventory inventory,
            PropertyDelegate propertyDelegate
    ) {
        super(screenHandlerType, syncId);
        checkSize(inventory, SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);
        this.addProperties(propertyDelegate);

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

    public float getCookProgress() {
        int cookProgress = this.propertyDelegate.get(0);
        int timeToCompleteCook = AbstractCookingPotBlockEntity.timeToCompleteCook;
        return MathHelper.clamp((float)cookProgress / (float)timeToCompleteCook, 0.0f, 1.0f);
    }

    public boolean isStoked() {
        return this.propertyDelegate.get(1) > 0;
    }
}

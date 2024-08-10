package com.bwt.blocks.infernal_enchanter;

import com.bwt.BetterWithTime;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class InfernalEnchanterScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private static final int SIZE = 2;

    public InfernalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2));
    }

    public InfernalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(BetterWithTime.infernalEnchanterScreenHandler, syncId);
        checkSize(inventory, SIZE);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // Mill Stone inventory
        this.addSlot(new Slot(inventory, 0, 17, 37));
        this.addSlot(new Slot(inventory, 1, 17, 75));

        // Player inventory
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 129 + m * 18));
            }
        }
        // Player hotbar
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 187));
        }
    }



    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}

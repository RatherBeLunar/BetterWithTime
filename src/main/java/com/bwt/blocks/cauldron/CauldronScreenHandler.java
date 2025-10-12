package com.bwt.blocks.cauldron;

import com.bwt.BetterWithTime;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;

public class CauldronScreenHandler extends AbstractCookingPotScreenHandler {
    public CauldronScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(SIZE), new ArrayPropertyDelegate(2));
    }

    public CauldronScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(BetterWithTime.cauldronScreenHandler, syncId, playerInventory, inventory, propertyDelegate);
    }
}

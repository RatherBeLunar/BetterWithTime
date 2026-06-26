package com.bwt.blocks.crucible;

import com.bwt.BetterWithTime;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotData;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;

public class CrucibleScreenHandler extends AbstractCookingPotScreenHandler {
    public CrucibleScreenHandler(int syncId, PlayerInventory playerInventory, AbstractCookingPotData cookingPotData) {
        this(syncId, playerInventory, new SimpleInventory(SIZE), new ArrayPropertyDelegate(1), cookingPotData);
    }

    public CrucibleScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, AbstractCookingPotData cookingPotData) {
        super(BetterWithTime.crucibleScreenHandler, syncId, playerInventory, inventory, propertyDelegate, cookingPotData);
    }
}

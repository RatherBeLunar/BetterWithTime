package com.bwt.blocks.cauldron;

import com.bwt.BetterWithTime;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotData;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotScreenHandler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class CauldronScreenHandler extends AbstractCookingPotScreenHandler {
    public CauldronScreenHandler(int syncId, Inventory playerInventory, AbstractCookingPotData cookingPotData) {
        this(syncId, playerInventory, new SimpleContainer(SIZE), new SimpleContainerData(1), cookingPotData);
    }

    public CauldronScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate, AbstractCookingPotData cookingPotData) {
        super(BetterWithTime.cauldronScreenHandler, syncId, playerInventory, inventory, propertyDelegate, cookingPotData);
    }
}

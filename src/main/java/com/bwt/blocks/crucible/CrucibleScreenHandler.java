package com.bwt.blocks.crucible;

import com.bwt.BetterWithTime;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotData;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotScreenHandler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class CrucibleScreenHandler extends AbstractCookingPotScreenHandler {
    public CrucibleScreenHandler(int syncId, Inventory playerInventory, AbstractCookingPotData cookingPotData) {
        this(syncId, playerInventory, new SimpleContainer(SIZE), new SimpleContainerData(1), cookingPotData);
    }

    public CrucibleScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate, AbstractCookingPotData cookingPotData) {
        super(BetterWithTime.crucibleScreenHandler, syncId, playerInventory, inventory, propertyDelegate, cookingPotData);
    }
}

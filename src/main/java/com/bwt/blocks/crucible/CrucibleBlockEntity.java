package com.bwt.blocks.crucible;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotData;
import com.bwt.recipes.BwtRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class CrucibleBlockEntity extends AbstractCookingPotBlockEntity {
    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.crucibleBlockEntity, BwtRecipes.CRUCIBLE_RECIPE_TYPE, BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE, pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        return new CrucibleScreenHandler(syncId, playerInventory, this.inventory, propertyDelegate, new AbstractCookingPotData(this.isStoked));
    }
}

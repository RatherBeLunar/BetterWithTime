package com.bwt.blocks.crucible;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.bwt.recipes.BwtRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class CrucibleBlockEntity extends AbstractCookingPotBlockEntity {
    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.crucibleBlockEntity, BwtRecipes.CRUCIBLE_RECIPE_TYPE, BwtRecipes.STOKED_CRUCIBLE_RECIPE_TYPE, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CrucibleScreenHandler(syncId, playerInventory, this.inventory, propertyDelegate);
    }
}

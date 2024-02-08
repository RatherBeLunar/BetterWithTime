package com.bwt.blocks.cauldron;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.CauldronRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CauldronBlockEntity extends AbstractCookingPotBlockEntity<CauldronBlockEntity, CauldronRecipe> {
    public CauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.cauldronBlockEntity, BwtRecipes.CAULDRON_RECIPE_TYPE, pos, state);
        this.ticksToCompleteCook = 200;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CauldronScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CauldronScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CauldronBlockEntity blockEntity) {
        blockEntity.cookProgressTicks = (blockEntity.cookProgressTicks + 1) % 200;
        blockEntity.markDirty();
    }
}

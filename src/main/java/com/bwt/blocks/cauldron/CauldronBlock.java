package com.bwt.blocks.cauldron;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CauldronBlock extends AbstractCookingPotBlock {

    public CauldronBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CauldronBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof CauldronBlockEntity cauldronBlockEntity) {
            player.openMenu(cauldronBlockEntity);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType) {
        return CauldronBlock.validateTicker(level, givenType, BwtBlockEntities.cauldronBlockEntity);
    }
}

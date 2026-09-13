package com.bwt.blocks.crucible;

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

public class CrucibleBlock extends AbstractCookingPotBlock {

    public CrucibleBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof CrucibleBlockEntity crucibleBlockEntity) {
            player.openMenu(crucibleBlockEntity);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType) {
        return CrucibleBlock.validateTicker(level, givenType, BwtBlockEntities.crucibleBlockEntity);
    }
}

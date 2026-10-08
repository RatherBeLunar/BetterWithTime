package com.bwt.blocks.unfired_pottery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public abstract class UnfiredPotteryBlock extends Block {
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");

    public UnfiredPotteryBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(COOKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COOKING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP, SupportType.RIGID);
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean notify) {
        if (level.isClientSide || !level.getBlockState(blockPos).is(this)) {
            return;
        }
        if (canSurvive(blockState, level, blockPos)) {
            return;
        }
        dropResources(blockState, level, blockPos);
        level.removeBlock(blockPos, notify);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}

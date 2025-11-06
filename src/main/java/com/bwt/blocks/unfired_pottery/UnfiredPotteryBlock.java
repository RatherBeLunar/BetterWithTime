package com.bwt.blocks.unfired_pottery;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class UnfiredPotteryBlock extends Block {
    public static final BooleanProperty COOKING = BooleanProperty.of("cooking");

    public UnfiredPotteryBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(COOKING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COOKING);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), Direction.UP, SideShapeType.RIGID);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient || !world.getBlockState(pos).isOf(this)) {
            return;
        }
        if (canPlaceAt(state, world, pos)) {
            return;
        }
        dropStacks(state, world, pos);
        world.removeBlock(pos, notify);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}

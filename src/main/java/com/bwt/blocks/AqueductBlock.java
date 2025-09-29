package com.bwt.blocks;

import com.bwt.tags.BwtFluidTags;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class AqueductBlock extends Block {
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), directions -> {
        directions.put(Direction.NORTH, NORTH);
        directions.put(Direction.EAST, EAST);
        directions.put(Direction.SOUTH, SOUTH);
        directions.put(Direction.WEST, WEST);
    }));

    public AqueductBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        state = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        if (!neighborPos.equals(pos.up())) {
            return state;
        }
        BlockState noFlowState = state
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false);
        FluidState aboveFluidState = neighborState.getFluidState();
        if (!aboveFluidState.isIn(BwtFluidTags.AQUEDUCT_FLUIDS) || aboveFluidState.isStill()) {
            return noFlowState;
        }
        for (Direction fluidAdjacentDirection : Direction.Type.HORIZONTAL) {
            BlockPos fluidAdjacentPos = neighborPos.offset(fluidAdjacentDirection);
            FluidState fluidAdjacentState = world.getFluidState(fluidAdjacentPos);
            if (!fluidAdjacentState.getFluid().matchesType(aboveFluidState.getFluid())) {
                continue;
            }
            BlockState neighborSupportingBlockState = world.getBlockState(fluidAdjacentPos.down());
            FluidState neighborSupportingFluidState = neighborSupportingBlockState.getFluidState();
            if (!neighborSupportingBlockState.isSolid() && (!neighborSupportingFluidState.getFluid().matchesType(fluidAdjacentState.getFluid()) || !neighborSupportingFluidState.isStill())) {
                continue;
            }
            if (fluidAdjacentState.getLevel() < aboveFluidState.getLevel()) {
                continue;
            }
            // If the neighbor is an aqueduct source, it won't flow into this block if this block is the one flowing into it
            if (neighborSupportingBlockState.isOf(this) && neighborSupportingBlockState.get(FACING_PROPERTIES.get(fluidAdjacentDirection.getOpposite()))) {
                continue;
            }
            state = state.with(FACING_PROPERTIES.get(fluidAdjacentDirection), true);
        }
        return state;
    }
}

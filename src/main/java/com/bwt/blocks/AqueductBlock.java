package com.bwt.blocks;

import com.bwt.tags.BwtFluidTags;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;

public class AqueductBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), directions -> {
        directions.put(Direction.NORTH, NORTH);
        directions.put(Direction.EAST, EAST);
        directions.put(Direction.SOUTH, SOUTH);
        directions.put(Direction.WEST, WEST);
    }));

    public AqueductBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        state = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (!neighborPos.equals(pos.above())) {
            return state;
        }
        BlockState noFlowState = state
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false);
        FluidState aboveFluidState = neighborState.getFluidState();
        if (!aboveFluidState.is(BwtFluidTags.AQUEDUCT_FLUIDS) || aboveFluidState.isSource()) {
            return noFlowState;
        }
        for (Direction fluidAdjacentDirection : Direction.Plane.HORIZONTAL) {
            BlockPos fluidAdjacentPos = neighborPos.relative(fluidAdjacentDirection);
            FluidState fluidAdjacentState = level.getFluidState(fluidAdjacentPos);
            if (!fluidAdjacentState.getType().isSame(aboveFluidState.getType())) {
                continue;
            }
            BlockState neighborSupportingBlockState = level.getBlockState(fluidAdjacentPos.below());
            FluidState neighborSupportingFluidState = neighborSupportingBlockState.getFluidState();
            if (!neighborSupportingBlockState.isSolid() && (!neighborSupportingFluidState.getType().isSame(fluidAdjacentState.getType()) || !neighborSupportingFluidState.isSource())) {
                continue;
            }
            if (fluidAdjacentState.getAmount() < aboveFluidState.getAmount()) {
                continue;
            }
            // If the neighbor is an aqueduct source, it won't flow into this block if this block is the one flowing into it
            if (neighborSupportingBlockState.is(this) && neighborSupportingBlockState.getValue(FACING_PROPERTIES.get(fluidAdjacentDirection.getOpposite()))) {
                continue;
            }
            state = state.setValue(FACING_PROPERTIES.get(fluidAdjacentDirection), true);
        }
        return state;
    }


    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH))
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH))
                    .setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST))
                    .setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST))
                    .setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST))
                    .setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST))
                    .setValue(WEST, state.getValue(SOUTH));
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default -> super.mirror(state, mirror);
        };
    }
}

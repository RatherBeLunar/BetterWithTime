package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineTrapBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final VoxelShape BOTTOM_SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    public static final VoxelShape TOP_SHAPE = Block.box(0, 14, 0, 16, 16, 16);


    public VineTrapBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(HALF, Half.BOTTOM).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = this.defaultBlockState();
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        Direction direction = ctx.getClickedFace();
        blockState = ctx.replacingClickedOnBlock() || !direction.getAxis().isHorizontal()
                ? blockState.setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP)
                : blockState.setValue(HALF, ctx.getClickLocation().y - (double)ctx.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
        return blockState.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return switch (type) {
            case WATER -> state.getValue(WATERLOGGED);
            case AIR, LAND -> true;
        };
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return false;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getBlockSupportShape(state, level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == Half.TOP ? TOP_SHAPE : BOTTOM_SHAPE;
    }
}

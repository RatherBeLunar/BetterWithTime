package com.bwt.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class TableBlock extends DecorativeBlock {
    public static final BooleanProperty SUPPORT = BooleanProperty.of("support");

    VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 15, 0, 16, 16, 16);
    VoxelShape SUPPORT_SHAPE = Block.createCuboidShape(6, 0, 6, 10, 15, 10);

    public TableBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
        setDefaultState(getDefaultState().with(SUPPORT, true));
    }

    public static TableBlock ofBlock(Block fullBlock) {
        return new TableBlock(Settings.copy(fullBlock), fullBlock);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SUPPORT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return state.get(SUPPORT) ? VoxelShapes.union(BASE_SHAPE, SUPPORT_SHAPE) : BASE_SHAPE;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(SUPPORT,
                Arrays.stream(Direction.Axis.values())
                        .filter(Direction.Axis::isHorizontal)
                        .noneMatch(axis -> ctx.getWorld().getBlockState(ctx.getBlockPos().offset(axis, 1)).isOf(this) && ctx.getWorld().getBlockState(ctx.getBlockPos().offset(axis, -1)).isOf(this))
        );
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(SUPPORT,
            Arrays.stream(Direction.Axis.values())
                    .filter(Direction.Axis::isHorizontal)
                    .noneMatch(axis -> world.getBlockState(pos.offset(axis, 1)).isOf(this) && world.getBlockState(pos.offset(axis, -1)).isOf(this))
        );
    }
}

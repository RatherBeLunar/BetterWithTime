package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class UrnBlock extends Block {
    public static final BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
    public static final VoxelShape outlineShape = Block.box(5, 0, 5, 11, 10, 11);
    public static final VoxelShape connectedUpOutlineShape = Block.box(5, 6, 5, 11, 16, 11);

    public UrnBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CONNECTED_UP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED_UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (ctx.getLevel().getBlockState(ctx.getClickedPos().above()).is(BwtBlocks.hopperBlock)) {
            return defaultBlockState().setValue(CONNECTED_UP, true);
        }
        return super.getStateForPlacement(ctx);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (neighborPos.equals(pos.above())) {
            return state.setValue(CONNECTED_UP, neighborState.is(BwtBlocks.hopperBlock));
        }
        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CONNECTED_UP) ? connectedUpOutlineShape : outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CONNECTED_UP) ? connectedUpOutlineShape : outlineShape;
    }
}

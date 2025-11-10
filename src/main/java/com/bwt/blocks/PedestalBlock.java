package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends DecorativeBlock {
    public static final DirectionProperty VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;

    VoxelShape UP_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 14, 16),
            Block.createCuboidShape(1, 14, 1, 15, 15, 15),
            Block.createCuboidShape(2, 15, 2, 14, 16, 14)
    );
    VoxelShape DOWN_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 2, 0, 16, 16, 16),
            Block.createCuboidShape(1, 1, 1, 15, 2, 15),
            Block.createCuboidShape(2, 0, 2, 14, 1, 14)
    );

    public PedestalBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
        setDefaultState(getDefaultState().with(VERTICAL_DIRECTION, Direction.UP));
    }

    public static PedestalBlock ofBlock(Block fullBlock) {
        return new PedestalBlock(Settings.copy(fullBlock), fullBlock);
    }

    public static PedestalBlock ofWoodBlock(Block woodBlock) {
        PedestalBlock pedestalBlock = ofBlock(woodBlock);
        pedestalBlock.isWood = true;
        return pedestalBlock;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(VERTICAL_DIRECTION);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(VERTICAL_DIRECTION, ctx.getVerticalPlayerLookDirection().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return state.get(VERTICAL_DIRECTION) == Direction.UP ? UP_SHAPE : DOWN_SHAPE;
    }
}

package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends DecorativeBlock {
    public static final DirectionProperty VERTICAL_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;

    final VoxelShape UP_SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 14, 16),
            Block.box(1, 14, 1, 15, 15, 15),
            Block.box(2, 15, 2, 14, 16, 14)
    );
    final VoxelShape DOWN_SHAPE = Shapes.or(
            Block.box(0, 2, 0, 16, 16, 16),
            Block.box(1, 1, 1, 15, 2, 15),
            Block.box(2, 0, 2, 14, 1, 14)
    );

    public PedestalBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
        registerDefaultState(defaultBlockState().setValue(VERTICAL_DIRECTION, Direction.UP));
    }

    public static PedestalBlock ofBlock(Block fullBlock) {
        return new PedestalBlock(Properties.ofFullCopy(fullBlock), fullBlock);
    }

    public static PedestalBlock ofWoodBlock(Block woodBlock) {
        PedestalBlock pedestalBlock = ofBlock(woodBlock);
        pedestalBlock.isWood = true;
        return pedestalBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VERTICAL_DIRECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(VERTICAL_DIRECTION, ctx.getNearestLookingVerticalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return state.getValue(VERTICAL_DIRECTION) == Direction.UP ? UP_SHAPE : DOWN_SHAPE;
    }
}

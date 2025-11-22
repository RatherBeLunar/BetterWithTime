package com.bwt.blocks.axles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CreativePowerSourceBlock extends Block implements AxlePowerLevelGetter {
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6f, 6f, 0f, 10f, 10f, 16f);
    protected static final VoxelShape OUTLINE = VoxelShapes.union(X_SHAPE, Y_SHAPE, Z_SHAPE);

    public CreativePowerSourceBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return OUTLINE;
    }

    @Override
    public int getMechPowerForNeighbor(BlockState state, Direction.Axis axis) {
        return 4;
    }
}


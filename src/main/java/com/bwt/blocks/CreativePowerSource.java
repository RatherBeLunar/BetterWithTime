package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CreativePowerSource extends Block implements IMechPowerBlock {

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6f, 6f, 0f, 10f, 10f, 16f);


    public CreativePowerSource(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canRepeatPower(BlockState blockState, Direction direction) {
        return true;
    }

    @Override
    public boolean canTransferPower(BlockState blockState, Direction direction) {
        return true;
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.union(X_SHAPE, Y_SHAPE, Z_SHAPE);
    }
}

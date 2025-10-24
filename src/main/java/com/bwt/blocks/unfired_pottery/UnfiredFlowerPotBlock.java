package com.bwt.blocks.unfired_pottery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class UnfiredFlowerPotBlock extends UnfiredPotteryBlock {
    public static VoxelShape outlineShape = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public UnfiredFlowerPotBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return outlineShape;
    }
}

package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PlanterBlock extends Block {
    public static VoxelShape flatTopOutlineShape = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 11, 14),
            Block.createCuboidShape(0, 11, 0, 16, 16, 16)
    ).simplify();

    public static VoxelShape indentedOutlineShape = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 11, 14),
            Block.createCuboidShape(0, 11, 0, 2, 16, 16),
            Block.createCuboidShape(14, 11, 0, 16, 16, 16),
            Block.createCuboidShape(2, 11, 14, 14, 16, 16),
            Block.createCuboidShape(2, 11, 0, 14, 16, 2)
    ).simplify();

    public PlanterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return flatTopOutlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return indentedOutlineShape;
    }
}

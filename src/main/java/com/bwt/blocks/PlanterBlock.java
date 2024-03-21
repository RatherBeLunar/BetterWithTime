package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PlanterBlock extends Block {
    public static VoxelShape outlineShape = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 11, 14),
            Block.createCuboidShape(0, 11, 0, 16, 16, 16)
    ).simplify();

    public PlanterBlock(Settings settings) {
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

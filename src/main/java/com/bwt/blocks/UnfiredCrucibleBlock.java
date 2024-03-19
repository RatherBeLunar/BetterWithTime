package com.bwt.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class UnfiredCrucibleBlock extends UnfiredPotteryBlock {
    public static VoxelShape outlineShape = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 1, 0.9375),
            VoxelShapes.cuboid(0, 0.125, 0, 1, 0.875, 1)
    ).simplify();

    public UnfiredCrucibleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }
}

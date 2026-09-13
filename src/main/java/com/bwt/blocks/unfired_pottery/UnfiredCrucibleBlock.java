package com.bwt.blocks.unfired_pottery;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UnfiredCrucibleBlock extends UnfiredPotteryBlock {
    public static final VoxelShape outlineShape = Shapes.or(
            Shapes.box(0.0625, 0, 0.0625, 0.9375, 1, 0.9375),
            Shapes.box(0, 0.125, 0, 1, 0.875, 1)
    ).optimize();

    public UnfiredCrucibleBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}

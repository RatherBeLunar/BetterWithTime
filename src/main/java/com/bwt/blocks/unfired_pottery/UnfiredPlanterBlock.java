package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.PlanterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class UnfiredPlanterBlock extends UnfiredPotteryBlock {
    public UnfiredPlanterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return PlanterBlock.flatTopOutlineShape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return PlanterBlock.indentedOutlineShape;
    }
}

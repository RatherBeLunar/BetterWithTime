package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.PlanterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UnfiredPlanterBlock extends UnfiredPotteryBlock {
    public UnfiredPlanterBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PlanterBlock.flatTopOutlineShape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PlanterBlock.indentedOutlineShape;
    }
}

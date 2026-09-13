package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlanterBlock extends Block {
    public static final VoxelShape flatTopOutlineShape = Shapes.or(
            Block.box(2, 0, 2, 14, 11, 14),
            Block.box(0, 11, 0, 16, 16, 16)
    ).optimize();

    public static final VoxelShape indentedOutlineShape = Shapes.or(
            Block.box(2, 0, 2, 14, 11, 14),
            Block.box(0, 11, 0, 2, 16, 16),
            Block.box(14, 11, 0, 16, 16, 16),
            Block.box(2, 11, 14, 14, 16, 16),
            Block.box(2, 11, 0, 14, 16, 2)
    ).optimize();

    public PlanterBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return flatTopOutlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return indentedOutlineShape;
    }
}

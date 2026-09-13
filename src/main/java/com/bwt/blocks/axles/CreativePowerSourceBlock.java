package com.bwt.blocks.axles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CreativePowerSourceBlock extends Block implements AxlePowerLevelGetter {
    protected static final VoxelShape X_SHAPE = Block.box(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.box(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.box(6f, 6f, 0f, 10f, 10f, 16f);
    protected static final VoxelShape OUTLINE = Shapes.or(X_SHAPE, Y_SHAPE, Z_SHAPE);

    public CreativePowerSourceBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return OUTLINE;
    }

    @Override
    public int getMechPowerForNeighbor(BlockState state, Direction.Axis axis) {
        return 4;
    }
}


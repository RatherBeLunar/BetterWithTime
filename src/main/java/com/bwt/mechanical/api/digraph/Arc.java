package com.bwt.mechanical.api.digraph;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface Arc  {

    Direction.Axis getAxis(World world, BlockPos pos, BlockState blockState);

    int getPowerLevel(BlockState state);

    boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction);
}

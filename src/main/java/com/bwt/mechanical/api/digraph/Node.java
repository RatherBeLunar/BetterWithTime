package com.bwt.mechanical.api.digraph;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public interface Node {
    List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState);

    boolean isReceivingInput(World world, BlockState state, BlockPos blockPos);
    boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction);
}

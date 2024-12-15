package com.bwt.mechanical.api.digraph;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public interface SinkNode extends Node {

    @Override
    boolean isReceivingInput(World world, BlockState state, BlockPos blockPos);

    default List<Direction> getOutputFaces(World world, BlockPos pos, BlockState blockState) {
        return List.of();
    }

    @Override
    default boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {
        return false;
    }
}

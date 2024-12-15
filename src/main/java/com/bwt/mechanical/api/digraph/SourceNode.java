package com.bwt.mechanical.api.digraph;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public interface SourceNode extends Node {

    @Override
    default List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return List.of();
    }

    @Override
    default boolean isReceivingInput(World world, BlockState state, BlockPos blockPos) {
        return false;
    }
}

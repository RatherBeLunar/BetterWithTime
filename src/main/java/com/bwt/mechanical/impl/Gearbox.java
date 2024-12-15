package com.bwt.mechanical.impl;

import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.digraph.DistributorNode;
import com.mojang.datafixers.types.Func;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Gearbox implements DistributorNode, MechPowered {

    @Override
    public abstract List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState);

    @Override
    public boolean isReceivingInput(World world, BlockState state, BlockPos blockPos) {
        var inputFaces = getInputFaces(world, blockPos, state);
        return MechTools.isReceivingPowerFromArcOrSource(world, state, blockPos, inputFaces);
    }

    @Override
    public boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {
        var inputFaces = getInputFaces(world, blockPos, state);
        return !inputFaces.contains(direction) && isPowered(state);
    }
}

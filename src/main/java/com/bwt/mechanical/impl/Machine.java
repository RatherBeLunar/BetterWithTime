package com.bwt.mechanical.impl;

import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.digraph.SinkNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class Machine implements SinkNode, MechPowered {

    public Machine() {
    }

    @Override
    public boolean isReceivingInput(World world, BlockState state, BlockPos blockPos) {
        var inputFaces = getInputFaces(world, blockPos, state);
        return MechTools.isReceivingPowerFromArcOrSource(world, state, blockPos, inputFaces);
    }
}

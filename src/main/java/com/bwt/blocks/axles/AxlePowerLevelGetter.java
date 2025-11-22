package com.bwt.blocks.axles;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface AxlePowerLevelGetter {
    int getMechPowerForNeighbor(BlockState state, Direction.Axis axis);
}

package com.bwt.blocks.axles;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface AxlePowerLevelGetter {
    int getMechPowerForNeighbor(BlockState state, Direction.Axis axis);
}

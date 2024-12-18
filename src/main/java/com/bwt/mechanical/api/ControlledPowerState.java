package com.bwt.mechanical.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

public class ControlledPowerState extends PowerState{

    private final boolean previousControlled;
    private final boolean nowControlled;

    public ControlledPowerState(PowerState state, boolean previousControlled, boolean nowControlled) {
        this(state.world(), state.pos(), state.state(), state.powerDirections(), state.previous(), state.now(), state.random(), previousControlled, nowControlled);
    }

    public ControlledPowerState(World world, BlockPos pos, BlockState state, List<Direction> powerDirections, boolean previous, boolean now, Random random, boolean previousControlled, boolean nowControlled) {
        super(world, pos, state, powerDirections, previous, now, random);
        this.previousControlled = previousControlled;
        this.nowControlled = nowControlled;
    }

    public boolean nowControlled() {
        return nowControlled;
    }

    public boolean previousControlled() {
        return previousControlled;
    }

    @Override
    public boolean hasChanged() {
        return super.hasChanged() || previousControlled != nowControlled;
    }

    @Override
    public boolean isPowered() {
        return this.now() && this.nowControlled();
    }
}

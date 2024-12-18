package com.bwt.mechanical.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PowerState {
    private final World world;
    private final BlockPos pos;
    private final BlockState state;
    private final List<Direction> powerDirections;
    private final boolean previous;
    private final boolean now;
    private final Random random;

    public PowerState(World world, BlockPos pos, BlockState state, List<Direction> powerDirections, boolean previous, boolean now, Random random) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.powerDirections = powerDirections;
        this.previous = previous;
        this.now = now;
        this.random = random;
    }

    public World world() {
        return world;
    }

    public BlockPos pos() {
        return pos;
    }

    public BlockState state() {
        return state;
    }

    public boolean previous() {
        return previous;
    }

    public boolean now() {
        return now;
    }

    public Random random() {
        return random;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PowerState) obj;
        return Objects.equals(this.world, that.world) &&
                Objects.equals(this.pos, that.pos) &&
                Objects.equals(this.state, that.state) &&
                this.previous == that.previous &&
                this.now == that.now &&
                Objects.equals(this.random, that.random);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, pos, state, previous, now, random);
    }

    @Override
    public String toString() {
        return "PowerState[" +
                "world=" + world + ", " +
                "pos=" + pos + ", " +
                "state=" + state + ", " +
                "previous=" + previous + ", " +
                "now=" + now + ", " +
                "random=" + random + ']';
    }

    public boolean hasChanged() {
        return previous != now;
    }

    public boolean isPowered() {
        return now;
    }

    public List<Direction> powerDirections() {
        return powerDirections;
    }
}

package com.bwt.mechanical.impl;

import com.bwt.mechanical.api.digraph.Arc;
import com.bwt.mechanical.api.digraph.Node;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class Axle implements Arc {
    public static final IntProperty MECH_POWER = IntProperty.of("mech_power", 0, 4);


    IntProperty getMechPowered() {
        return MECH_POWER;
    }

    public static void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWER);
    }

    @Override
    public int getPowerLevel(BlockState state) {
        if (state.contains(MECH_POWER)) {
            return state.get(MECH_POWER);
        }
        return 0;
    }

    boolean isPowered(BlockState state) {
        return getPowerLevel(state) > 0;
    }

    BlockState withPower(BlockState state, int power) {
        return state.with(MECH_POWER, power);
    }
    public enum PowerState {
        UNPOWERED,
        POWERED,
        UNCHANGED,
        OVERPOWERED
    }

    public CalcPowerResult calculatePowerLevel(World world, BlockState state, BlockPos pos) {

        int currentPower = getPowerLevel(state);

        var axis = getAxis(world, pos, state);


        int maxPowerNeighbor = 0;
        int greaterPowerNeighbors = 0;
        for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
            Direction direction = Direction.from(axis, axisDirection);
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            int neighborPower = 0;

            Optional<Node> nodeOpt = MechTools.getNode(neighborState);
            if (nodeOpt.isPresent()) {
                var node = nodeOpt.get();
                if(node.isSendingOutput(world, neighborState, neighborPos, direction.getOpposite())) {
                    neighborPower = 4;
                }
            } else {
                Optional<Arc> arc = MechTools.getArc(neighborState);
                if (arc.isPresent()) {
                    neighborPower = arc.get().getPowerLevel(neighborState);
                }
            }


            if (neighborPower > maxPowerNeighbor) {
                maxPowerNeighbor = neighborPower;
            }

            if (neighborPower > currentPower) {
                greaterPowerNeighbors++;
            }

        }
        if (greaterPowerNeighbors >= 2) {
            // We're getting power from multiple directions at once
            return new CalcPowerResult(PowerState.OVERPOWERED, 0);
        }

        int newPower;

        if (maxPowerNeighbor > currentPower) {
            if (maxPowerNeighbor == 1) {
                //  Power has overextended
                return new CalcPowerResult(PowerState.OVERPOWERED, 0);
            }

            newPower = maxPowerNeighbor - 1;
        } else {
            newPower = 0;
        }

        if (newPower != currentPower) {
            return new CalcPowerResult(newPower == 0 ? PowerState.UNPOWERED : PowerState.POWERED, newPower);
        } else {
            return new CalcPowerResult(PowerState.UNCHANGED, newPower);
        }
    }

    @Override
    public boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {
        return direction.getAxis() == getAxis(world, blockPos, state) && isPowered(state);
    }

    public record CalcPowerResult(PowerState state, int power) { }
}

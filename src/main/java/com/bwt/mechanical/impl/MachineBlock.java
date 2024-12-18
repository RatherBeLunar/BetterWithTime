package com.bwt.mechanical.impl;

import com.bwt.mechanical.api.IMechPoweredBlock;
import com.bwt.mechanical.api.PowerState;
import com.bwt.mechanical.api.digraph.SinkNode;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface MachineBlock extends SinkNode, IMechPoweredBlock {

    static final int turnOnTickRate = 10;
    static final int turnOffTickRate = 9;

    default int getTurnOnTickRate() {
        return turnOnTickRate;
    }

    default int getTurnOffTickRate() {
        return turnOffTickRate;
    }


    @Override
    default boolean isReceivingInput(World world, BlockState state, BlockPos blockPos) {
        var inputFaces = getInputFaces(world, blockPos, state);
        return MechTools.isReceivingPowerFromArcOrSourceDirection(world, state, blockPos, inputFaces);
    }


    default PowerState getPowerState(World world, BlockState state, BlockPos pos, Random random) {
        var powerDirections = MechTools.getReceivingPowerFromArcOrSourceDirections(world, state, pos, getInputFaces(world, pos, state));
        var bReceivingPower = powerDirections.stream().findFirst().isPresent();
        var bOn = isStatePowered(state);
        return new PowerState(world, pos, state, powerDirections, bOn, bReceivingPower, random);
    }

    default void onUpdate(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var powerState = getPowerState(world, state, pos, random);
        if (powerState.hasChanged()) {
            powerState.world().setBlockState(powerState.pos(), asPoweredState(world, pos, state, powerState));
            onPowerChanged(powerState);
        } else if (powerState.isPowered()) {
            if(powerState.powerDirections().size() > 1) {
                overpower(powerState);
                return;
            }
            whilePowered(powerState);
        } else {
            whileUnpowered(powerState);
        }
    }

    default void onPowerChanged(PowerState powerState) {
    }

    default void whilePowered(PowerState powerState) {
    }

    default void whileUnpowered(PowerState powerState) {
    }


    default void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        var powerState = getPowerState(world, state, pos, world.getRandom());
        if (powerState.previous() != powerState.now()) {
            world.scheduleBlockTick(pos, state.getBlock(), getTurnOnTickRate());
        } else {
            world.scheduleBlockTick(pos, state.getBlock(), getTurnOffTickRate());
        }
    }


    default void overpower(PowerState powerState) {}

}

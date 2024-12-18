package com.bwt.mechanical.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMechPoweredBlock {
    BooleanProperty MECH_POWERED = BooleanProperty.of("mech_powered");

    public static void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED);
    }

    default boolean isStatePowered(BlockState state) {
        return state.contains(IMechPoweredBlock.MECH_POWERED) && state.get(IMechPoweredBlock.MECH_POWERED);
    }

    default BlockState asPoweredState(World world, BlockPos pos, BlockState state, PowerState powerState) {
        return state.with(IMechPoweredBlock.MECH_POWERED, powerState.now());
    }

}

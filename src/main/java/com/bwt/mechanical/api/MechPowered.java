package com.bwt.mechanical.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public interface MechPowered {
    BooleanProperty MECH_POWERED = BooleanProperty.of("mech_powered");

    default BooleanProperty getMechPowered() {
        return MECH_POWERED;
    }

    static void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED);
    }

    default boolean isPowered(BlockState state) {
        return state.contains(MECH_POWERED) && state.get(MECH_POWERED);
    }

    default BlockState asPowered(BlockState state, boolean powered) {
        return state.with(MECH_POWERED, powered);
    }

}

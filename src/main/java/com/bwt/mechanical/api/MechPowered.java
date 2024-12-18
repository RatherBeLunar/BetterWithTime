package com.bwt.mechanical.api;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;

public interface MechPowered {

    default BooleanProperty getMechPowered() {
        return IMechPoweredBlock.MECH_POWERED;
    }

    default boolean isPowered(BlockState state) {
        return state.contains(IMechPoweredBlock.MECH_POWERED) && state.get(IMechPoweredBlock.MECH_POWERED);
    }

    default BlockState asPowered(BlockState state, boolean powered) {
        return state.with(IMechPoweredBlock.MECH_POWERED, powered);
    }

}

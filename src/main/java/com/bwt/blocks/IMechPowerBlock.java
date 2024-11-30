package com.bwt.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;

public interface IMechPowerBlock {
    BooleanProperty MECH_POWERED = BooleanProperty.of("mech_powered");

    default boolean isMechPowered(BlockState blockState) {
        return blockState.get(MECH_POWERED);
    }

    default boolean canRepeatPower(BlockState blockState, Direction direction) {
        return false;
    }

    default boolean canTransferPower(BlockState blockState, Direction direction) {
        return false;
    }

    default int getMechPower(BlockState blockState) {
        return 0;
    }
}

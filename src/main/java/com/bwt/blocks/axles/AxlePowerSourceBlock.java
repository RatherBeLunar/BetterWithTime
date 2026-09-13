package com.bwt.blocks.axles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AxlePowerSourceBlock extends AxleBlock {
    public AxlePowerSourceBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(MECH_POWER, 3));
    }

    @Override
    public int getMechPowerForNeighbor(BlockState state, Direction.Axis axis) {
        return state.getValue(AXIS).equals(axis) ? 4 : 0;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {}
}

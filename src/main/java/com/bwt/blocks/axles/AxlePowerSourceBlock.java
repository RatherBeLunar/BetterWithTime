package com.bwt.blocks.axles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

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
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean notify) {}
}

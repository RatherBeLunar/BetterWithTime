package com.bwt.blocks.axles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AxlePowerSourceBlock extends AxleBlock {
    public AxlePowerSourceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(MECH_POWER, 3));
    }

    @Override
    public int getMechPowerForNeighbor(BlockState state, Direction.Axis axis) {
        return state.get(AXIS).equals(axis) ? 4 : 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {}
}

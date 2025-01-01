package com.bwt.blocks;

import com.bwt.mechanical.api.digraph.SourceNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AxlePowerSourceBlock extends AxleBlock implements SourceNode {

    public AxlePowerSourceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(MECH_POWER, 3));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

    }

    @Override
    public boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {
        return state.get(AXIS).equals(direction.getAxis());
    }
}

package com.bwt.blocks;

import com.bwt.entities.WindmillBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AxlePowerSourceBlock extends AxleBlock implements BlockEntityProvider {
    public AxlePowerSourceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(MECH_POWER, 3));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WindmillBlockEntity(pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

    }
}

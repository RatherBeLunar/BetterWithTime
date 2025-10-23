package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneClutchBlock extends GearBoxBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public RedstoneClutchBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return super.isMechPowered(blockState) && !blockState.get(POWERED);
    }

    @Override
    public BlockState getPowerStates(BlockState state, World world, BlockPos pos) {
        return super.getPowerStates(state, world, pos).with(POWERED, world.isReceivingRedstonePower(pos));
    }

    @Override
    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, world, pos);
        boolean isRedstonePowered = newState.get(POWERED);
        boolean wasRedstonePowered = state.get(POWERED);
        boolean isReceivingMechPower = super.isMechPowered(newState);
        boolean wasReceivingMechPower = super.isMechPowered(state);
        // If block just turned on
        if ((!isRedstonePowered && wasRedstonePowered) || (isReceivingMechPower && !wasReceivingMechPower)) {
            world.scheduleBlockTick(pos, this, turnOnTickRate);
        }
        // If block just turned off
        else if ((isRedstonePowered && !wasRedstonePowered) || (!isReceivingMechPower && wasReceivingMechPower)) {
            world.scheduleBlockTick(pos, this, turnOffTickRate);
        }
    }
}

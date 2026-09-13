package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneClutchBlock extends GearBoxBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RedstoneClutchBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return super.isMechPowered(blockState) && !blockState.getValue(POWERED);
    }

    @Override
    public BlockState getPowerStates(BlockState state, Level level, BlockPos pos) {
        return super.getPowerStates(state, level, pos).setValue(POWERED, level.hasNeighborSignal(pos));
    }

    @Override
    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, level, pos);
        boolean isRedstonePowered = newState.getValue(POWERED);
        boolean wasRedstonePowered = state.getValue(POWERED);
        boolean isReceivingMechPower = super.isMechPowered(newState);
        boolean wasReceivingMechPower = super.isMechPowered(state);
        // If block just turned on
        if ((!isRedstonePowered && wasRedstonePowered) || (isReceivingMechPower && !wasReceivingMechPower)) {
            level.scheduleTick(pos, this, turnOnTickRate);
        }
        // If block just turned off
        else if ((isRedstonePowered && !wasRedstonePowered) || (!isReceivingMechPower && wasReceivingMechPower)) {
            level.scheduleTick(pos, this, turnOffTickRate);
        }
    }
}

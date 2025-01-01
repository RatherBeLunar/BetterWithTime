package com.bwt.mechanical.impl;

import com.bwt.mechanical.api.MechPowered;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class MachineBlockWithEntity extends BlockWithEntity implements MachineBlock {
    private final int turnOnTickRate;
    private final int turnOffTickRate;

    public MachineBlockWithEntity(Settings settings) {
        this(settings, MachineBlock.turnOnTickRate, MachineBlock.turnOffTickRate);
    }

    public MachineBlockWithEntity(Settings settings, int tickRate) {
        this(settings, tickRate, tickRate);
    }

    public MachineBlockWithEntity(Settings settings, int turnOnTickRate, int turnOffTickRate) {
        super(settings);
        this.turnOnTickRate = turnOnTickRate;
        this.turnOffTickRate = turnOffTickRate;
        setDefaultState(getStateManager().getDefaultState().with(MechPowered.MECH_POWERED, false));
    }

    @Override
    public int getTurnOffTickRate() {
        return turnOffTickRate;
    }

    @Override
    public int getTurnOnTickRate() {
        return turnOnTickRate;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, getTurnOnTickRate());
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowered.appendProperties(builder);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        this.onUpdate(state, world, pos, random);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        schedulePowerUpdate(state, world, pos);
    }
}

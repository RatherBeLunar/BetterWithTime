package com.bwt.mechanical.impl;

import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.mechanical.api.IMechPoweredBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class MachineSimpleFacingBlock extends SimpleFacingBlock implements MachineBlock {

    private final int powerChangeTickRate;

    protected MachineSimpleFacingBlock(Settings settings, int powerChangeTickRate) {
        super(settings);
        this.powerChangeTickRate = powerChangeTickRate;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, powerChangeTickRate);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        IMechPoweredBlock.appendProperties(builder);
        builder.add(FACING);
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

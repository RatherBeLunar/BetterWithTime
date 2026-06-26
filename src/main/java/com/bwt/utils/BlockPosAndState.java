package com.bwt.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public record BlockPosAndState(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
    public static BlockPosAndState of(WorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return new BlockPosAndState(pos, state, state.hasBlockEntity() ? world.getBlockEntity(pos) : null);
    }

    public FluidState fluidState() {
        return state().getFluidState();
    }
}

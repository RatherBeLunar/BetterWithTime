package com.bwt.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public record BlockPosAndState(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
    public static BlockPosAndState of(LevelAccessor levelAccessor, BlockPos pos) {
        BlockState state = levelAccessor.getBlockState(pos);
        return new BlockPosAndState(pos, state, state.hasBlockEntity() ? levelAccessor.getBlockEntity(pos) : null);
    }

    public FluidState fluidState() {
        return state().getFluidState();
    }
}

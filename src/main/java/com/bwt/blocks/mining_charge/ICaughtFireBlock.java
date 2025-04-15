package com.bwt.blocks.mining_charge;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ICaughtFireBlock {
    default boolean onCaughtFire(BlockState state, World level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        return true;
    }
}

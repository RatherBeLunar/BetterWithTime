package com.bwt.utils;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public record FireData(int fireFactor, FireType fireType) {
    public static final int primaryFireFactor = 5;
    public static final int secondaryFireFactor = 1; // This was changed to 3 later

    public static FireData fromWorld(World world, BlockPos pos) {
        int fireCount = 0;
        int stokedFireCount = 0;
        FireType fireType = FireType.UNSTOKED;
        BlockPos below = pos.down();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockState state = world.getBlockState(below.offset(Direction.Axis.X, x).offset(Direction.Axis.Z, z));
                if (x == 0 && z == 0 && !state.isOf(Blocks.FIRE) && !state.isOf(BwtBlocks.stokedFireBlock)) {
                    return new FireData(0, FireType.UNSTOKED);
                }
                if (state.isOf(Blocks.FIRE)) {
                    fireCount += 1;
                }
                if (state.isOf(BwtBlocks.stokedFireBlock)) {
                    stokedFireCount += 1;
                    fireType = FireType.STOKED;
                }
            }
        }
        int fireTypeCount = fireType == FireType.STOKED ? stokedFireCount : fireCount;
        int fireFactor = primaryFireFactor + (fireTypeCount - 1) * secondaryFireFactor;
        return new FireData(fireFactor, fireType);
    }
}
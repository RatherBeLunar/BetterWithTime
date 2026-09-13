package com.bwt.utils.kiln_block_cook_overlay;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class KilnBlockCookOverlay {
    public static void setKilnBlockCookingInfo(ServerLevel serverLevel, BlockPos pos, int progress) {
        for (ServerPlayer player : PlayerLookup.tracking(serverLevel, pos)) {
            ServerPlayNetworking.send(player, new KilnBlockCookingProgressPayload(pos, progress));
        }
    }

    public static void setKilnBlockCookingInfo(Level level, BlockPos pos, int progress) {
        if (level instanceof ServerLevel serverLevel) {
            setKilnBlockCookingInfo(serverLevel, pos, progress);
        }
    }
}

package com.bwt.utils.kiln_block_cook_overlay;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class KilnBlockCookOverlay {
    public static void setKilnBlockCookingInfo(ServerLevel serverLevel, BlockPos pos, int progress) {
        for (ServerPlayer player : PlayerLookup.tracking(serverLevel, pos)) {
            ServerPlayNetworking.send(player, new KilnBlockCookingProgressPayload(pos, progress));
        }
    }
}

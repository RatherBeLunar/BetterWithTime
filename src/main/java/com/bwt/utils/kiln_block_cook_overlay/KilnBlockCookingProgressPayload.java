package com.bwt.utils.kiln_block_cook_overlay;

import com.bwt.utils.Id;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record KilnBlockCookingProgressPayload(BlockPos blockPos, int progress) implements CustomPacketPayload {
    public static final Identifier KILN_COOK_PACKET_ID = Id.of("kiln_cook_overlay");
    public static final CustomPacketPayload.Type<KilnBlockCookingProgressPayload> ID = new CustomPacketPayload.Type<>(KILN_COOK_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, KilnBlockCookingProgressPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, KilnBlockCookingProgressPayload::blockPos,
            ByteBufCodecs.INT, KilnBlockCookingProgressPayload::progress,
            KilnBlockCookingProgressPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

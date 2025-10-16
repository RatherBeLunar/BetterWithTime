package com.bwt.blocks.abstract_cooking_pot;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record AbstractCookingPotData(boolean isStoked) {
    public static final PacketCodec<RegistryByteBuf, AbstractCookingPotData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            AbstractCookingPotData::isStoked,
            AbstractCookingPotData::new
    );
}
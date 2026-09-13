package com.bwt.blocks.abstract_cooking_pot;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AbstractCookingPotData(boolean isStoked) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AbstractCookingPotData> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            AbstractCookingPotData::isStoked,
            AbstractCookingPotData::new
    );
}
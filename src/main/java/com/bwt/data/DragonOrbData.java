package com.bwt.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class DragonOrbData {
    public static Codec<DragonOrbData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.BOOL.fieldOf("validated").forGetter(DragonOrbData::isValid)).apply(instance, DragonOrbData::new)
    );

    public static PacketCodec<ByteBuf, DragonOrbData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    boolean validated;

    public DragonOrbData(boolean value) {
        this.validated = value;
    }

    public boolean isValid() {
        return validated;
    }

    public void setValid(boolean value) {
        validated = value;
    }

}

package com.bwt.utils;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WindmillBehavior implements StringRepresentable {
    NORMAL("normal"),
    NO_WIND("no wind"),
    TOO_MUCH_WIND("too much wind");

    public static final Codec<WindmillBehavior> CODEC = StringRepresentable.fromEnum(WindmillBehavior::values);
    private final String name;

    WindmillBehavior(final String string2) {
        this.name = string2;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}

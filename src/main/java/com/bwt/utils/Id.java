package com.bwt.utils;

import net.minecraft.util.Identifier;

public class Id {
    public static final String MOD_ID = "bwt";

    public static Identifier of(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public static Identifier of(String namespace, String id) {
        return Identifier.of(namespace, id);
    }

    public static Identifier mc(String id) {
        return Identifier.of(id);
    }
}

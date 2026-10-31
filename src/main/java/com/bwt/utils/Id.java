package com.bwt.utils;

import net.minecraft.resources.Identifier;

public class Id {
    public static final String MOD_ID = "bwt";

    public static final Identifier PROGRAMMER_ART_PACK_ID = Id.of("bwt_programmer_art");
    public static final Identifier LEGACY_SOUNDS_PACK_ID = Id.of("legacy_bwt_sounds");

    public static Identifier of(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }

    public static Identifier of(String namespace, String id) {
        return Identifier.fromNamespaceAndPath(namespace, id);
    }

    public static Identifier mc(String id) {
        return Identifier.parse(id);
    }
}

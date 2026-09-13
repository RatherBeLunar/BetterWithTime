package com.bwt.utils;

import net.minecraft.resources.ResourceLocation;

public class Id {
    public static final String MOD_ID = "bwt";

    public static final ResourceLocation PROGRAMMER_ART_PACK_ID = Id.of("bwt_programmer_art");

    public static ResourceLocation of(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    public static ResourceLocation of(String namespace, String id) {
        return ResourceLocation.fromNamespaceAndPath(namespace, id);
    }

    public static ResourceLocation mc(String id) {
        return ResourceLocation.parse(id);
    }
}

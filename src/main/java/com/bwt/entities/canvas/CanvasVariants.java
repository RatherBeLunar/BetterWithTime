package com.bwt.entities.canvas;

import com.bwt.utils.Id;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class CanvasVariants extends PaintingVariants {
    public static final RegistryKey<PaintingVariant> WANDERER = of("wanderer");

    private static RegistryKey<PaintingVariant> of(String id) {
        return RegistryKey.of(RegistryKeys.PAINTING_VARIANT, Id.of(id));
    }
}

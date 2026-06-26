package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class BwtPaintingVariantTags {
    public static final TagKey<PaintingVariant> CANVAS_PLACEABLE = of("canvas_placeable");

	private BwtPaintingVariantTags() {
	}

	private static TagKey<PaintingVariant> of(String id) {
		return TagKey.of(RegistryKeys.PAINTING_VARIANT, Id.of(id));
	}
}

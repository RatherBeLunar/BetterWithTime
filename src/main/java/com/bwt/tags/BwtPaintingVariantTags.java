package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class BwtPaintingVariantTags {
    public static final TagKey<PaintingVariant> CANVAS_PLACEABLE = of("canvas_placeable");

	private BwtPaintingVariantTags() {
	}

	private static TagKey<PaintingVariant> of(String id) {
		return TagKey.create(Registries.PAINTING_VARIANT, Id.of(id));
	}
}

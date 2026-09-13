package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CompatibilityTags {
    // Farmer's Delight
    public static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = TagKey.create(Registries.BLOCK, Id.of("farmersdelight", "unaffected_by_rich_soil"));
}

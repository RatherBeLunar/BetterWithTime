package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class CompatibilityTags {
    // Farmer's Delight
    public static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = TagKey.of(RegistryKeys.BLOCK, Id.of("farmersdelight", "unaffected_by_rich_soil"));
}

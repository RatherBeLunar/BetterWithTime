package com.bwt.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtBlockTags {
    public static final TagKey<Block> BLOCK_DISPENSER_INHALE_NOOP = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "block_dispenser_inhale_noop"));
    public static final TagKey<Block> DETECTABLE_SMALL_CROPS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "detectable_small_crops"));
    public static final TagKey<Block> SURVIVES_SAW_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "survives_saw_block"));
    public static final TagKey<Block> SIDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "siding_blocks"));
    public static final TagKey<Block> WOODEN_SIDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_siding_blocks"));
    public static final TagKey<Block> MOULDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "moulding_blocks"));
    public static final TagKey<Block> WOODEN_MOULDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_moulding_blocks"));
    public static final TagKey<Block> CORNER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "corner_blocks"));
    public static final TagKey<Block> WOODEN_CORNER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_corner_blocks"));
}

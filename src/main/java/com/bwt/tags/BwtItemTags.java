package com.bwt.tags;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtItemTags {
    public static final TagKey<Item> SIDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "siding_blocks"));
    public static final TagKey<Item> WOODEN_SIDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wooden_siding_blocks"));
    public static final TagKey<Item> MOULDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "moulding_blocks"));
    public static final TagKey<Item> WOODEN_MOULDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wooden_moulding_blocks"));
    public static final TagKey<Item> CORNER_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "siding_blocks"));
    public static final TagKey<Item> WOODEN_CORNER_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wooden_corner_blocks"));
    public static final TagKey<Item> PASSES_WICKER_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_wicker_filter"));
    public static final TagKey<Item> PASSES_GRATE_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_grate_filter"));
    public static final TagKey<Item> PASSES_SLATS_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_slats_filter"));
    public static final TagKey<Item> PASSES_TRAPDOOR_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_trapdoor_filter"));
    public static final TagKey<Item> PASSES_IRON_BARS_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_iron_bars_filter"));
    public static final TagKey<Item> PASSES_LADDER_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_ladder_filter"));
    public static final TagKey<Item> STOKED_EXPLOSIVES = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "stoked_explosives"));
}

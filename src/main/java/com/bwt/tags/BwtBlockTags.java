package com.bwt.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtBlockTags {
    public static final TagKey<Block> BLOCK_DISPENSER_INHALE_NOOP = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "block_dispenser_inhale_noop"));
    public static final TagKey<Block> BLOCK_DISPENSER_INHALE_VOID = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "block_dispenser_inhale_void"));
    public static final TagKey<Block> DETECTABLE_SMALL_CROPS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "detectable_small_crops"));
    public static final TagKey<Block> SURVIVES_SAW_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "survives_saw_block"));
    public static final TagKey<Block> SAW_BREAKS_NO_DROPS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "saw_breaks_no_drops"));
    public static final TagKey<Block> SAW_BREAKS_DROPS_LOOT = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "saw_breaks_drops_loot"));
    public static final TagKey<Block> SIDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "siding_blocks"));
    public static final TagKey<Block> WOODEN_SIDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_siding_blocks"));
    public static final TagKey<Block> MOULDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "moulding_blocks"));
    public static final TagKey<Block> WOODEN_MOULDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_moulding_blocks"));
    public static final TagKey<Block> CORNER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "corner_blocks"));
    public static final TagKey<Block> WOODEN_CORNER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_corner_blocks"));
    public static final TagKey<Block> COLUMN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "column_blocks"));
    public static final TagKey<Block> WOODEN_COLUMN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_column_blocks"));
    public static final TagKey<Block> PEDESTAL_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "pedestal_blocks"));
    public static final TagKey<Block> WOODEN_PEDESTAL_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_pedestal_blocks"));
    public static final TagKey<Block> TABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "table_blocks"));
    public static final TagKey<Block> WOODEN_TABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wooden_table_blocks"));
    public static final TagKey<Block> VASES = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "vases"));
    public static final TagKey<Block> WOOL_SLABS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "wool_slabs"));
    public static final TagKey<Block> DOES_NOT_TRIGGER_BUDDY = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "does_not_trigger_buddy"));
    public static final TagKey<Block> CROPS_CAN_PLANT_ON = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "farmland"));
    public static final TagKey<Block> SOUL_SAND_PLANTS_CAN_PLANT_ON = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "soul_sand_plants_can_plant_on"));
    public static final TagKey<Block> TRANSFERS_ROTATION_UPWARD_OVERRIDE = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "transfers_rotation_upward_override"));
    public static final TagKey<Block> MATTOCK_MINEABLE = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "mineable/mattock"));
    public static final TagKey<Block> BATTLEAXE_MINEABLE = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "mineable/battle_axe"));
}

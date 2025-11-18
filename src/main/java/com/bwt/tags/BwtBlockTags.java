package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtBlockTags {
    // When a BD attempts to inhale, the block will remain in the world and the BD won't gain any items
    public static final TagKey<Block> BLOCK_DISPENSER_INHALE_NOOP = TagKey.of(RegistryKeys.BLOCK, Id.of("block_dispenser_inhale_noop"));
    // When a BD attempts to inhale, the block will be removed and the BD won't gain any items
    public static final TagKey<Block> BLOCK_DISPENSER_INHALE_VOID = TagKey.of(RegistryKeys.BLOCK, Id.of("block_dispenser_inhale_void"));
    // Crops that, when fully grown one block in front of and below (diagonally down from) a Detector Block's face, will trigger the DB.
    public static final TagKey<Block> DETECTABLE_SMALL_CROPS = TagKey.of(RegistryKeys.BLOCK, Id.of("detectable_small_crops"));
    // When placed in front of a powered saw, neither the saw nor the place block will break
    public static final TagKey<Block> SURVIVES_SAW_BLOCK = TagKey.of(RegistryKeys.BLOCK, Id.of("survives_saw_block"));
    // When placed in front of a powered saw, the block will be removed with no drops
    public static final TagKey<Block> SAW_BREAKS_NO_DROPS = TagKey.of(RegistryKeys.BLOCK, Id.of("saw_breaks_no_drops"));
    // When placed in front of a powered saw, the block will be removed and drop its default loot
    public static final TagKey<Block> SAW_BREAKS_DROPS_LOOT = TagKey.of(RegistryKeys.BLOCK, Id.of("saw_breaks_drops_loot"));

    // Mini blocks
    // WOODEN_ blocks are mined with an axe and cut with a saw
    public static final TagKey<Block> SIDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("siding_blocks"));
    public static final TagKey<Block> WOODEN_SIDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("wooden_siding_blocks"));
    public static final TagKey<Block> MOULDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("moulding_blocks"));
    public static final TagKey<Block> WOODEN_MOULDING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("wooden_moulding_blocks"));
    public static final TagKey<Block> CORNER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("corner_blocks"));
    public static final TagKey<Block> WOODEN_CORNER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("wooden_corner_blocks"));
    public static final TagKey<Block> COLUMN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("column_blocks"));
    public static final TagKey<Block> WOODEN_COLUMN_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("wooden_column_blocks"));
    public static final TagKey<Block> PEDESTAL_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("pedestal_blocks"));
    public static final TagKey<Block> WOODEN_PEDESTAL_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("wooden_pedestal_blocks"));
    public static final TagKey<Block> TABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("table_blocks"));
    public static final TagKey<Block> WOODEN_TABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Id.of("wooden_table_blocks"));

    // All colors of vases
    public static final TagKey<Block> VASES = TagKey.of(RegistryKeys.BLOCK, Id.of("vases"));
    // All colors of wool slabs
    public static final TagKey<Block> WOOL_SLABS = TagKey.of(RegistryKeys.BLOCK, Id.of("wool_slabs"));

    // Blocks in this tag will not update the buddy block when placed, or when their state changes
    public static final TagKey<Block> DOES_NOT_TRIGGER_BUDDY = TagKey.of(RegistryKeys.BLOCK, Id.of("does_not_trigger_buddy"));
    // Overriding the vanilla hard-coding of allowing crops to be planted on farmland, so we can add the planter pot
    public static final TagKey<Block> CROPS_CAN_PLANT_ON = TagKey.of(RegistryKeys.BLOCK, Id.of("farmland"));
    // Overriding the vanilla hard-coding of allowing some plants to be placed on soul sand, so we can add the soul sand pot
    public static final TagKey<Block> SOUL_SAND_PLANTS_CAN_PLANT_ON = TagKey.of(RegistryKeys.BLOCK, Id.of("soul_sand_plants_can_plant_on"));
    // Turntables only transfer their rotation up 2 blocks if the first block has a certain solid shape. This tag includes things like stairs and walls to include more shapes
    public static final TagKey<Block> TRANSFERS_ROTATION_UPWARD_OVERRIDE = TagKey.of(RegistryKeys.BLOCK, Id.of("transfers_rotation_upward_override"));

    // Multitool mining, just includes the relevant sub-tool tags
    public static final TagKey<Block> MATTOCK_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Id.of("mineable/mattock"));
    public static final TagKey<Block> BATTLEAXE_MINEABLE = TagKey.of(RegistryKeys.BLOCK, Id.of("mineable/battle_axe"));

    // Blood wood basic handling
    public static final TagKey<Block> BLOOD_WOOD_LOGS = TagKey.of(RegistryKeys.BLOCK, Id.of("blood_wood_logs"));
    public static final TagKey<Block> BLOOD_WOOD_PLANTABLE_ON = TagKey.of(RegistryKeys.BLOCK, Id.of("blood_wood_plantable_on"));

    // Overriding the vanilla behavior of converting dirt to podzol - we don't want to use the dirt tag for this
    public static final TagKey<Block> CAN_CONVERT_TO_PODZOL = TagKey.of(RegistryKeys.BLOCK, Id.of("can_convert_to_podzol"));
    // Same thing but for our podzol slabs
    public static final TagKey<Block> CAN_CONVERT_TO_PODZOL_SLAB = TagKey.of(RegistryKeys.BLOCK, Id.of("can_convert_to_podzol_slab"));
    // Nether Groth will consume some small plants (fungi, weeping vines)
    public static final TagKey<Block> NETHER_GROTH_CAN_EAT = TagKey.of(RegistryKeys.BLOCK, Id.of("nether_groth_can_eat"));
}

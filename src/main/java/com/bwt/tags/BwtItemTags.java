package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Collectors;

public class BwtItemTags {
    public static final TagKey<Item> SIDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("siding_blocks"));
    public static final TagKey<Item> WOODEN_SIDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("wooden_siding_blocks"));
    public static final TagKey<Item> MOULDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("moulding_blocks"));
    public static final TagKey<Item> WOODEN_MOULDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("wooden_moulding_blocks"));
    public static final TagKey<Item> CORNER_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("corner_blocks"));
    public static final TagKey<Item> WOODEN_CORNER_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("wooden_corner_blocks"));
    public static final TagKey<Item> COLUMN_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("column_blocks"));
    public static final TagKey<Item> WOODEN_COLUMN_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("wooden_column_blocks"));
    public static final TagKey<Item> PEDESTAL_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("pedestal_blocks"));
    public static final TagKey<Item> WOODEN_PEDESTAL_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("wooden_pedestal_blocks"));
    public static final TagKey<Item> TABLE_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("table_blocks"));
    public static final TagKey<Item> WOODEN_TABLE_BLOCKS = TagKey.of(RegistryKeys.ITEM, Id.of("wooden_table_blocks"));
    public static final TagKey<Item> VASES = TagKey.of(RegistryKeys.ITEM, Id.of("vases"));
    public static final TagKey<Item> WOOL_SLABS = TagKey.of(RegistryKeys.ITEM, Id.of("wool_slabs"));
    public static final TagKey<Item> PASSES_WICKER_FILTER = TagKey.of(RegistryKeys.ITEM, Id.of("passes_wicker_filter"));
    public static final TagKey<Item> PASSES_GRATE_FILTER = TagKey.of(RegistryKeys.ITEM, Id.of("passes_grate_filter"));
    public static final TagKey<Item> PASSES_SLATS_FILTER = TagKey.of(RegistryKeys.ITEM, Id.of("passes_slats_filter"));
    public static final TagKey<Item> PASSES_TRAPDOOR_FILTER = TagKey.of(RegistryKeys.ITEM, Id.of("passes_trapdoor_filter"));
    public static final TagKey<Item> PASSES_IRON_BARS_FILTER = TagKey.of(RegistryKeys.ITEM, Id.of("passes_iron_bars_filter"));
    public static final TagKey<Item> PASSES_LADDER_FILTER = TagKey.of(RegistryKeys.ITEM, Id.of("passes_ladder_filter"));
    public static final TagKey<Item> STOKED_EXPLOSIVES = TagKey.of(RegistryKeys.ITEM, Id.of("stoked_explosives"));
    public static final TagKey<Item> SAW_DUSTS = TagKey.of(RegistryKeys.ITEM, Id.of("saw_dusts"));
    public static final TagKey<Item> MINING_CHARGE_IMMUNE = TagKey.of(RegistryKeys.ITEM, Id.of("mining_charge_immune"));
    public static final TagKey<Item> BLOOD_WOOD_LOGS = TagKey.of(RegistryKeys.ITEM, Id.of("blood_wood_logs"));
    public static final TagKey<Item> CAN_INFERNAL_ENCHANT = TagKey.of(RegistryKeys.ITEM, Id.of("can_infernal_enchant"));

    public static final TagKey<Item> INFERNAL_ENCHANTABLE_HELMETS = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/helmets"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_CHESTPLATES = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/chestplates"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_LEGGINGS = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/leggings"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_BOOTS = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/boots"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_ARMOR = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/armor"));

    public static final TagKey<Item> INFERNAL_ENCHANTABLE_PICKAXE = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/pickaxe"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_TOOL = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/tool"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_SWORD = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/sword"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_MELEE_WEAPON = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/melee_weapon"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_DURABLITY = TagKey.of(RegistryKeys.ITEM, Id.of( "infernal_enchantable/durability"));

    private static final Map<RegistryKey<Enchantment>, TagKey<Item>> CAN_APPLY_INFERNAL_ENCHANT_TO = Maps.newHashMap();

    public static final TagKey<Item> canApplyInfernal(RegistryKey<Enchantment> enchantment) {

        return CAN_APPLY_INFERNAL_ENCHANT_TO.computeIfAbsent(enchantment, (key) ->
                TagKey.of(RegistryKeys.ITEM, Id.of("bwt", "infernal_enchantments/" + key.getValue().getPath())));


    }

}

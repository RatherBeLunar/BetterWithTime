package com.bwt.tags;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Collectors;

public class BwtItemTags {
    public static final TagKey<Item> SIDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "siding_blocks"));
    public static final TagKey<Item> WOODEN_SIDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wooden_siding_blocks"));
    public static final TagKey<Item> MOULDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "moulding_blocks"));
    public static final TagKey<Item> WOODEN_MOULDING_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wooden_moulding_blocks"));
    public static final TagKey<Item> CORNER_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "corner_blocks"));
    public static final TagKey<Item> VASES = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "vases"));
    public static final TagKey<Item> WOOL_SLABS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wool_slabs"));
    public static final TagKey<Item> WOODEN_CORNER_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "wooden_corner_blocks"));
    public static final TagKey<Item> PASSES_WICKER_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_wicker_filter"));
    public static final TagKey<Item> PASSES_GRATE_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_grate_filter"));
    public static final TagKey<Item> PASSES_SLATS_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_slats_filter"));
    public static final TagKey<Item> PASSES_TRAPDOOR_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_trapdoor_filter"));
    public static final TagKey<Item> PASSES_IRON_BARS_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_iron_bars_filter"));
    public static final TagKey<Item> PASSES_LADDER_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_ladder_filter"));
    public static final TagKey<Item> STOKED_EXPLOSIVES = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "stoked_explosives"));
    public static final TagKey<Item> SAW_DUSTS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "saw_dusts"));
    public static final TagKey<Item> MINING_CHARGE_IMMUNE = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "mining_charge_immune"));
    public static final TagKey<Item> BLOOD_WOOD_LOGS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "blood_wood_logs"));
    public static final TagKey<Item> CAN_INFERNAL_ENCHANT = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "can_infernal_enchant"));

    public static final TagKey<Item> INFERNAL_ENCHANTABLE_HELMETS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/helmets"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_CHESTPLATES = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/chestplates"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_LEGGINGS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/leggings"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_BOOTS = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/boots"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_ARMOR = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/armor"));

    public static final TagKey<Item> INFERNAL_ENCHANTABLE_PICKAXE = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/pickaxe"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_TOOL = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/tool"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_SWORD = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/sword"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_MELEE_WEAPON = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/melee_weapon"));
    public static final TagKey<Item> INFERNAL_ENCHANTABLE_DURABLITY = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantable/durability"));

    public static final Map<Enchantment, TagKey<Item>> CAN_APPLY_INFERNAL_ENCHANT_TO = Registries.ENCHANTMENT.getKeys().stream().map( key -> Pair.of(Registries.ENCHANTMENT.get(key), TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "infernal_enchantments/" + key.getValue().getPath())))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
}

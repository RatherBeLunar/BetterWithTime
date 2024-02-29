package com.bwt.tags;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtTags implements ModInitializer {
    public static final TagKey<EntityType<?>> BLOCK_DISPENSER_INHALE_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier("bwt", "block_dispenser_inhale_entities"));
    public static final TagKey<Block> BLOCK_DISPENSER_INHALE_NOOP = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "block_dispenser_inhale_noop"));
    public static final TagKey<Block> DETECTABLE_SMALL_CROPS = TagKey.of(RegistryKeys.BLOCK, new Identifier("bwt", "detectable_small_crops"));
    public static final TagKey<Item> PASSES_LADDER_FILTER = TagKey.of(RegistryKeys.ITEM, new Identifier("bwt", "passes_ladder_filter"));

    @Override
    public void onInitialize() {

    }
}

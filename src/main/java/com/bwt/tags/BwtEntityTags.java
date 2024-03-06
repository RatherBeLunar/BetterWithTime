package com.bwt.tags;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtEntityTags{
    public static final TagKey<EntityType<?>> BLOCK_DISPENSER_INHALE_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier("bwt", "block_dispenser_inhale_entities"));
}

package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BwtEntityTags{
    public static final TagKey<EntityType<?>> BLOCK_DISPENSER_INHALE_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, Id.of("block_dispenser_inhale_entities"));
}

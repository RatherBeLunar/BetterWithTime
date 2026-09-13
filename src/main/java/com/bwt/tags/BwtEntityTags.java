package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class BwtEntityTags {
    public static final TagKey<EntityType<?>> BLOCK_DISPENSER_INHALE_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, Id.of("block_dispenser_inhale_entities"));
}

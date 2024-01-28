package com.bwt.utils.rectangular_entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class RectangularEntityType<T extends RectangularEntity> extends EntityType<T> {
    public RectangularEntityType(EntityFactory<T> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> canSpawnInside, EntityDimensions dimensions, int maxTrackDistance, int trackTickInterval, FeatureSet requiredFeatures) {
        super(factory, spawnGroup, saveable, summonable, fireImmune, spawnableFarFromPlayer, canSpawnInside, dimensions, maxTrackDistance, trackTickInterval, requiredFeatures);
    }

    @Override
    public EntityRectDimensions getDimensions() {
        return ((EntityRectDimensions) super.getDimensions());
    }
}

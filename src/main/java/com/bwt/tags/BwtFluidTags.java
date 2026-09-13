package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class BwtFluidTags {
    public static final TagKey<Fluid> AQUEDUCT_FLUIDS = TagKey.create(Registries.FLUID, Id.of("aqueduct_fluids"));
}

package com.bwt.tags;

import com.bwt.utils.Id;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class BwtFluidTags {
    public static final TagKey<Fluid> AQUEDUCT_FLUIDS = TagKey.of(RegistryKeys.FLUID, Id.of("aqueduct_fluids"));
}

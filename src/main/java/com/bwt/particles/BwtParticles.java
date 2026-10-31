package com.bwt.particles;

import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class BwtParticles implements ModInitializer {
    public static final SimpleParticleType BLOOD_WOOD_LEAVES = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Id.of("blood_wood_leaves"), BLOOD_WOOD_LEAVES);
    }
}

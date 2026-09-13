package com.bwt.damage_types;

import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class BwtDamageTypes implements ModInitializer {
    public static ResourceKey<DamageType> SAW_DAMAGE_TYPE;
    @Override
    public void onInitialize() {
         SAW_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, Id.of("saw"));
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }
}

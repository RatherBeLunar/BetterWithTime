package com.bwt.loot_tables;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;

@FunctionalInterface
public interface LootTableProvider {
    RegistryKey<LootTable> getLootTableRegistryKey(LivingEntity entity, DamageSource source);
    default LootTable getLootTable(LivingEntity entity, DamageSource source) {
        return entity.getWorld().getServer().getReloadableRegistries().getLootTable(getLootTableRegistryKey(entity, source));
    }
}

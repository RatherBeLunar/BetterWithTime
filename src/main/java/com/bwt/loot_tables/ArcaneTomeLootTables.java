package com.bwt.loot_tables;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class ArcaneTomeLootTables {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            BwtLootTables.dropLoot(entity, damageSource, (e, s) -> ArcaneTomeLootTables.getEntityArcaneTomeLootTable(e));
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            BwtLootTables.dropLoot(entity, damageSource, (e, s) -> {
              RegistryKey<DimensionType> dimensionType = e.getWorld().getDimensionEntry().getKey().orElse(null);
                return ArcaneTomeLootTables.getEntityArcaneTomeLootTable(e, dimensionType);
            });
        });

    }


    public static <T extends Entity> RegistryKey<LootTable> getEntityTypeArcaneTomeLootTable(EntityType<T> entityType) {
        var id = getEntityArcaneTomeLootTableId(entityType);
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
    }

    public static RegistryKey<LootTable> getEntityArcaneTomeLootTable(Entity entity) {
        return getEntityTypeArcaneTomeLootTable(entity.getType());
    }

    public static RegistryKey<LootTable> getEntityArcaneTomeLootTable(Entity entity, RegistryKey<DimensionType> dimensionType) {
        var id =  getEntityArcaneTomeLootTableId(entity.getType(), dimensionType);
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
    }

    public static <T extends Entity> Identifier getEntityArcaneTomeLootTableId(EntityType<T> entityType) {
        return new Identifier("bwt", "arcane_tome/" + entityType.getLootTableId().getValue().getPath());
    }

    public static <T extends Entity> Identifier getEntityArcaneTomeLootTableId(EntityType<T> entityType, RegistryKey<DimensionType> dimensionType) {
        var dimension = dimensionType.getValue().getPath();
        var entity = entityType.getLootTableId().getValue().getPath();
        var path = String.format("arcane_time/%s/%s", dimension, entity);
        return new Identifier("bwt", path);
    }
}

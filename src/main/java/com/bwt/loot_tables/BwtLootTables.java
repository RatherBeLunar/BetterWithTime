package com.bwt.loot_tables;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;

public class BwtLootTables implements ModInitializer {


    @Override
    public void onInitialize() {
        ArcaneTomeLootTables.register();
    }


    public static void dropLoot(LivingEntity entity, DamageSource damageSource, LootTableProvider provider) {

        LootTable lootTable = provider.getLootTable(entity, damageSource);
        if (lootTable == null) {
            return;
        }

        var causedByPlayer = damageSource.getSource() instanceof PlayerEntity;
        var attackingPlayer = causedByPlayer ? (PlayerEntity) damageSource.getSource() : null;
        LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld)entity.getWorld()))
                .add(LootContextParameters.THIS_ENTITY, entity)
                .add(LootContextParameters.ORIGIN, entity.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, damageSource)
                .addOptional(LootContextParameters.KILLER_ENTITY, damageSource.getAttacker())
                .addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damageSource.getSource());
        if (causedByPlayer && attackingPlayer != null) {
            builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, attackingPlayer).luck(attackingPlayer.getLuck());
        }

        LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);
        lootTable.generateLoot(lootContextParameterSet, entity.getLootTableSeed(), entity::dropStack);
    }


}

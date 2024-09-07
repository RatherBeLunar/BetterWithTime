package com.bwt.generation;

import com.bwt.items.BwtItems;
import com.bwt.items.components.ArcaneEnchantmentComponent;
import com.bwt.items.components.BwtDataComponents;
import com.bwt.loot_tables.ArcaneTomeLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public class EntityLootTableGenerator extends SimpleFabricLootTableProvider {
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public EntityLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this(dataOutput, registryLookup, LootContextTypes.ENTITY);
    }

    public EntityLootTableGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup, LootContextType lootContextType) {
        super(output, registryLookup, lootContextType);
        this.registryLookup = registryLookup;
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(
                EntityType.SKELETON.getLootTableId(),
                LootTable.builder()
                        .pool(LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0f))
                                .with(ItemEntry.builder(BwtItems.rottedArrowItem)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 2.0f)))
                                        .apply(EnchantedCountIncreaseLootFunction.builder(registryLookup.join(), UniformLootNumberProvider.create(0.0f, 1.0f)))
                                )
                        )
                        .pool(LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0f))
                                .with(ItemEntry.builder(Items.BONE)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0f, 2.0f)))
                                        .apply(EnchantedCountIncreaseLootFunction.builder(registryLookup.join(), UniformLootNumberProvider.create(0.0f, 1.0f))))
                        )
        );
        try {
            /// Loot Table identical to BTW
            registerEntityArcaneTomeLootTable(consumer, EntityType.WITCH, Enchantments.AQUA_AFFINITY, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.SPIDER, Enchantments.BANE_OF_ARTHROPODS, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.CREEPER, Enchantments.BLAST_PROTECTION, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.SILVERFISH, Enchantments.EFFICIENCY, DimensionTypes.THE_END, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.BAT, Enchantments.FEATHER_FALLING, 1 / 250f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.MAGMA_CUBE, Enchantments.FIRE_ASPECT, 1 / 250f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.ZOMBIFIED_PIGLIN, Enchantments.FIRE_PROTECTION, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.BLAZE, Enchantments.FLAME, 1 / 500f);

            registerEntityArcaneTomeLootTable(consumer, EntityType.WITHER, Enchantments.KNOCKBACK, 1f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.SKELETON, Enchantments.INFINITY, DimensionTypes.THE_NETHER, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.SKELETON, Enchantments.PROJECTILE_PROTECTION, DimensionTypes.OVERWORLD, 1 / 100f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.SLIME, Enchantments.PROTECTION, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.GHAST, Enchantments.PUNCH, 1 / 500f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.SQUID, Enchantments.RESPIRATION, 1 / 250f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.ENDERMAN, Enchantments.SILK_TOUCH, 1 / 1000f);
            registerEntityArcaneTomeLootTable(consumer, EntityType.ZOMBIE, Enchantments.SMITE, 1 / 1000f);
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    public <T extends Entity> void registerEntityArcaneTomeLootTable(
            BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer,
            EntityType<T> entityType,
            RegistryKey<Enchantment> enchantmentKey,
            float chance
    ) throws ExecutionException, InterruptedException {

        var enchantmentRegistry = this.registryLookup.get().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        var enchantment = enchantmentRegistry.getOrThrow(enchantmentKey);
        var component = new ArcaneEnchantmentComponent(enchantment, true);

        var identifier = ArcaneTomeLootTables.getEntityArcaneTomeLootTableId(entityType);

        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, identifier);

        consumer.accept(key,
                LootTable.builder().pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0f))
                                .with(ItemEntry.builder(BwtItems.arcaneTome)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f)))
                                        .apply(SetComponentsLootFunction.builder(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT, component))
                                        .conditionally(RandomChanceLootCondition.builder(chance))
                                )
                )
        );

    }

    public <T extends Entity> void registerEntityArcaneTomeLootTable(
            BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer,
            EntityType<T> entityType,
            RegistryKey<Enchantment> enchantmentKey,
            RegistryKey<DimensionType> dimensionType,
            float chance
    ) throws ExecutionException, InterruptedException {

        var enchantmentRegistry = this.registryLookup.get().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        var enchantment = enchantmentRegistry.getOrThrow(enchantmentKey);
        var component = new ArcaneEnchantmentComponent(enchantment, true);

        var identifier = ArcaneTomeLootTables.getEntityArcaneTomeLootTableId(entityType, dimensionType);

        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, identifier);
        consumer.accept(key,
                LootTable.builder().pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0f))
                                .with(ItemEntry.builder(BwtItems.arcaneTome)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f)))
                                        .apply(SetComponentsLootFunction.builder(BwtDataComponents.ARCANE_ENCHANTMENT_COMPONENT, component))
                                        .conditionally(RandomChanceLootCondition.builder(chance))
                                )
                )
        );

    }
}

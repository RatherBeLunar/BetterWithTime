package com.bwt.generation;

import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class EntityLootTableGenerator extends SimpleFabricLootTableProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public EntityLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this(dataOutput, registryLookup, LootContextParamSets.ENTITY);
    }

    public EntityLootTableGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup, LootContextParamSet lootContextType) {
        super(output, registryLookup, lootContextType);
        this.registryLookup = registryLookup;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        lootTableBiConsumer.accept(
                EntityType.SKELETON.getDefaultLootTable(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0f))
                                .add(LootItem.lootTableItem(BwtItems.rottedArrowItem)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0f, 2.0f)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registryLookup.join(), UniformGenerator.between(0.0f, 1.0f)))
                                )
                        )
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0f))
                                .add(LootItem.lootTableItem(Items.BONE)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0f, 2.0f)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registryLookup.join(), UniformGenerator.between(0.0f, 1.0f))))
                        )
        );
    }
}

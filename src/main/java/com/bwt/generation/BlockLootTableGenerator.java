package com.bwt.generation;

import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;

public class BlockLootTableGenerator extends FabricBlockLootTableProvider {
    public BlockLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(Blocks.SHORT_GRASS, net.minecraft.data.server.loottable.BlockLootTableGenerator.dropsWithShears(
                Blocks.SHORT_GRASS,
                this.applyExplosionDecay(
                        Blocks.SHORT_GRASS,
                        ItemEntry.builder(Items.WHEAT_SEEDS).conditionally(RandomChanceLootCondition.builder(0.125f))
                ).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                        .alternatively(ItemEntry.builder(BwtItems.hempSeedsItem).conditionally(RandomChanceLootCondition.builder(0.125f)).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2)))
        ));
        addDrop(Blocks.TALL_GRASS, net.minecraft.data.server.loottable.BlockLootTableGenerator.dropsWithShears(
                Blocks.TALL_GRASS,
                this.applyExplosionDecay(
                        Blocks.TALL_GRASS,
                        ItemEntry.builder(Items.WHEAT_SEEDS).conditionally(RandomChanceLootCondition.builder(0.125f))
                ).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                        .alternatively(ItemEntry.builder(BwtItems.hempSeedsItem).conditionally(RandomChanceLootCondition.builder(0.125f)).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2)))
        ));
    }

}

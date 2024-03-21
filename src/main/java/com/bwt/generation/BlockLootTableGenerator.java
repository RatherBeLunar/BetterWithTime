package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.predicate.StatePredicate;

public class BlockLootTableGenerator extends FabricBlockLootTableProvider {
    public BlockLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(BwtBlocks.anchorBlock);
//        addDrop(BwtBlocks.anvilBlock);
        addDrop(BwtBlocks.axleBlock);
        addDrop(BwtBlocks.axlePowerSourceBlock, BwtBlocks.axleBlock);
//        addDrop(BwtBlocks.barrelBlock);
        addDrop(BwtBlocks.bellowsBlock);
        addDrop(BwtBlocks.blockDispenserBlock);
//        addDrop(BwtBlocks.bloodWoodBlock);
//        addDrop(BwtBlocks.buddyBlockBlock);
        addDrop(BwtBlocks.cauldronBlock);
//        addDrop(BwtBlocks.canvasBlock);
//        addDrop(BwtBlocks.columnBlock);
        addDrop(BwtBlocks.companionCubeBlock);
        addDrop(BwtBlocks.companionSlabBlock);
        addDrop(BwtBlocks.crucibleBlock);
        addDrop(BwtBlocks.detectorBlock);
        addDrop(BwtBlocks.detectorLogicBlock);
        addDrop(BwtBlocks.gearBoxBlock);
        addDrop(BwtBlocks.grateBlock);
        addDrop(BwtBlocks.handCrankBlock);
        addHempDrop();
        addDrop(BwtBlocks.hibachiBlock);
        addDrop(BwtBlocks.hopperBlock);
//        addDrop(BwtBlocks.infernalEnchanterBlock);
        addDrop(BwtBlocks.kilnBlock, Blocks.BRICKS);
//        addDrop(BwtBlocks.lensBlock);
        addDrop(BwtBlocks.lightBlockBlock);
        addDrop(BwtBlocks.millStoneBlock);
//        addDrop(BwtBlocks.miningChargeBlock);
//        addDrop(BwtBlocks.netherGrothBlock);
        addDrop(BwtBlocks.obsidianDetectorRailBlock);
        addDrop(BwtBlocks.obsidianPressuePlateBlock);
        addDrop(BwtBlocks.obsidianDetectorRailBlock);
//        addDrop(BwtBlocks.pedestalBlock);
        addDrop(BwtBlocks.planterBlock);
        addDrop(BwtBlocks.soilPlanterBlock);
        addDrop(BwtBlocks.soulSandPlanterBlock);
        addDrop(BwtBlocks.grassPlanterBlock);
        addDrop(BwtBlocks.platformBlock);
        addDrop(BwtBlocks.pulleyBlock);
        addDrop(BwtBlocks.ropeBlock);
        addDrop(BwtBlocks.sawBlock);
//        addDrop(BwtBlocks.screwPumpBlock);
        addDrop(BwtBlocks.slatsBlock);
//        addDrop(BwtBlocks.stakeBlock);
        addDrop(BwtBlocks.stokedFireBlock);
        addDrop(BwtBlocks.stoneDetectorRailBlock);
//        addDrop(BwtBlocks.tableBlock);
        addDrop(BwtBlocks.turntableBlock);
        addDrop(BwtBlocks.unfiredCrucibleBlock);
        addDrop(BwtBlocks.unfiredPlanterBlock);
        addDrop(BwtBlocks.unfiredVaseBlock);
        addDrop(BwtBlocks.unfiredUrnBlock);
        addDrop(BwtBlocks.unfiredMouldBlock);
        addDrop(BwtBlocks.urnBlock);
//        addDrop(BwtBlocks.waterWheelBlock);
        addDrop(BwtBlocks.wickerBlock);
//        addDrop(BwtBlocks.woolSlabBlock);
        BwtBlocks.vaseBlocks.values().forEach(this::addDropWithSilkTouch);
        BwtBlocks.sidingBlocks.forEach(this::addDrop);
        BwtBlocks.mouldingBlocks.forEach(this::addDrop);
        BwtBlocks.cornerBlocks.forEach(this::addDrop);
    }

    private void addHempDrop() {
        addDrop(
                BwtBlocks.hempCropBlock,
                applyExplosionDecay(
                        BwtBlocks.hempCropBlock,
                        LootTable.builder()
                                .pool(LootPool.builder()
                                        // If fully grown, drop hemp item
                                        .conditionally(BlockStatePropertyLootCondition.builder(BwtBlocks.hempCropBlock)
                                                .properties(StatePredicate.Builder.create().exactMatch(HempCropBlock.AGE, HempCropBlock.MAX_AGE))
                                        ).with(ItemEntry.builder(BwtItems.hempItem))
                                ).pool(LootPool.builder()
                                        // Regardless of growth, drop some seeds
                                        .with(ItemEntry.builder(BwtItems.hempSeedsItem)
                                                .conditionally(RandomChanceLootCondition.builder(0.5f))
                                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5f, 0))
                                        )
                                )
                )
        );
    }

}

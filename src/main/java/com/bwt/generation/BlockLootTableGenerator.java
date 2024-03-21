package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
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
        addDrop(Blocks.SHORT_GRASS, BlockLootTableGenerator.dropsWithShears(
                Blocks.SHORT_GRASS,
                this.applyExplosionDecay(
                        Blocks.SHORT_GRASS,
                        ItemEntry.builder(Items.WHEAT_SEEDS).conditionally(RandomChanceLootCondition.builder(0.125f))
                ).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                        .alternatively(ItemEntry.builder(BwtItems.hempSeedsItem).conditionally(RandomChanceLootCondition.builder(0.125f)).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2)))
        ));
        addDrop(Blocks.TALL_GRASS, BlockLootTableGenerator.dropsWithShears(
                Blocks.TALL_GRASS,
                this.applyExplosionDecay(
                        Blocks.TALL_GRASS,
                        ItemEntry.builder(Items.WHEAT_SEEDS).conditionally(RandomChanceLootCondition.builder(0.125f))
                ).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
                        .alternatively(ItemEntry.builder(BwtItems.hempSeedsItem).conditionally(RandomChanceLootCondition.builder(0.125f)).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2)))
        ));
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
        addDrop(
                BwtBlocks.hempCropBlock,
                cropDrops(
                        BwtBlocks.hempCropBlock, BwtItems.hempItem, BwtItems.hempSeedsItem,
                        BlockStatePropertyLootCondition.builder(BwtBlocks.hempCropBlock).properties(
                                StatePredicate.Builder.create().exactMatch(HempCropBlock.AGE, HempCropBlock.MAX_AGE)
                        )
                )
        );
        addDrop(BwtBlocks.hibachiBlock);
        addDrop(BwtBlocks.hopperBlock);
//        addDrop(BwtBlocks.infernalEnchanterBlock);
        addDrop(BwtBlocks.kilnBlock);
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
    }

}

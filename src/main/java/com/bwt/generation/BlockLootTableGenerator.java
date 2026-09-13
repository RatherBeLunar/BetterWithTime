package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.HempCropBlock;
import com.bwt.items.BwtItems;
import com.bwt.utils.DyeUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.concurrent.CompletableFuture;

public class BlockLootTableGenerator extends FabricBlockLootTableProvider {
    public BlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(BwtBlocks.aqueductBlock);
        dropSelf(BwtBlocks.anchorBlock);
//        addDrop(BwtBlocks.anvilBlock);
        dropSelf(BwtBlocks.axleBlock);
        dropOther(BwtBlocks.axlePowerSourceBlock, BwtBlocks.axleBlock);
//        addDrop(BwtBlocks.barrelBlock);
        dropSelf(BwtBlocks.bellowsBlock);
        dropSelf(BwtBlocks.blockDispenserBlock);

        dropSelf(BwtBlocks.bloodWoodBlocks.logBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.strippedLogBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.woodBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.strippedWoodBlock);

        add(BwtBlocks.bloodWoodBlocks.leavesBlock, block -> createLeavesDrops(block, BwtBlocks.bloodWoodBlocks.saplingBlock, NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(BwtBlocks.bloodWoodBlocks.saplingBlock);
        dropPottedContents(BwtBlocks.bloodWoodBlocks.pottedSaplingBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.planksBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.buttonBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.fenceBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.fenceGateBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.pressurePlateBlock);
        add(BwtBlocks.bloodWoodBlocks.slabBlock, this::createSlabItemTable);
        dropSelf(BwtBlocks.bloodWoodBlocks.stairsBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.doorBlock);
        dropSelf(BwtBlocks.bloodWoodBlocks.trapdoorBlock);

        dropSelf(BwtBlocks.buddyBlock);
        dropSelf(BwtBlocks.cauldronBlock);
//        addDrop(BwtBlocks.canvasBlock);
        dropSelf(BwtBlocks.companionCubeBlock);
        dropSelf(BwtBlocks.companionSlabBlock);
        dropSelf(BwtBlocks.crucibleBlock);
        dropSelf(BwtBlocks.detectorBlock);
        dropSelf(BwtBlocks.gearBoxBlock);
        dropSelf(BwtBlocks.redstoneClutchBlock);
        dropSelf(BwtBlocks.grateBlock);
        dropSelf(BwtBlocks.handCrankBlock);
        addHempDrop();
        dropSelf(BwtBlocks.hibachiBlock);
        dropSelf(BwtBlocks.hopperBlock);
//        addDrop(BwtBlocks.infernalEnchanterBlock);
        dropOther(BwtBlocks.kilnBlock, Blocks.BRICKS);
        dropSelf(BwtBlocks.lensBlock);
        dropSelf(BwtBlocks.lightBlockBlock);
        dropSelf(BwtBlocks.millStoneBlock);
        dropSelf(BwtBlocks.miningChargeBlock);
//        addDrop(BwtBlocks.netherGrothBlock);
        dropSelf(BwtBlocks.obsidianDetectorRailBlock);
        dropSelf(BwtBlocks.obsidianPressurePlateBlock);
        dropSelf(BwtBlocks.obsidianDetectorRailBlock);
        dropSelf(BwtBlocks.planterBlock);
        dropSelf(BwtBlocks.soulForgeBlock);
        dropSelf(BwtBlocks.soilPlanterBlock);
        dropSelf(BwtBlocks.soulSandPlanterBlock);
        dropSelf(BwtBlocks.grassPlanterBlock);
        dropSelf(BwtBlocks.platformBlock);
        dropSelf(BwtBlocks.pulleyBlock);
        dropSelf(BwtBlocks.ropeBlock);
        dropSelf(BwtBlocks.ropeCoilBlock);
        dropSelf(BwtBlocks.sawBlock);
        dropSelf(BwtBlocks.screwPumpBlock);
        dropSelf(BwtBlocks.slatsBlock);
//        addDrop(BwtBlocks.stakeBlock);
        dropSelf(BwtBlocks.stokedFireBlock);
        dropSelf(BwtBlocks.stoneDetectorRailBlock);
        dropSelf(BwtBlocks.turntableBlock);
        dropSelf(BwtBlocks.unfiredCrucibleBlock);
        dropSelf(BwtBlocks.unfiredPlanterBlock);
        dropSelf(BwtBlocks.unfiredVaseBlock);
        dropSelf(BwtBlocks.unfiredUrnBlock);
        dropSelf(BwtBlocks.unfiredFlowerPotBlock);
        dropSelf(BwtBlocks.unfiredDecoratedPotBlock);
        add(BwtBlocks.unfiredDecoratedPotBlockWithSherds, this::unfiredDecoratedPotBlockWithSherdsDrops);
        dropSelf(BwtBlocks.urnBlock);
        dropSelf(BwtBlocks.wickerPaneBlock);
        dropSelf(BwtBlocks.wickerBlock);
        add(BwtBlocks.wickerSlabBlock, this::createSlabItemTable);
        dropSelf(BwtBlocks.vineTrapBlock);
        DyeUtils.streamColorItemsSorted(BwtBlocks.woolSlabBlocks).forEach(block -> add(block, this::createSlabItemTable));
        DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).forEach(this::dropWhenSilkTouch);
        BwtBlocks.sidingBlocks.forEach(this::dropSelf);
        BwtBlocks.mouldingBlocks.forEach(this::dropSelf);
        BwtBlocks.cornerBlocks.forEach(this::dropSelf);
        BwtBlocks.columnBlocks.forEach(this::dropSelf);
        BwtBlocks.pedestalBlocks.forEach(this::dropSelf);
        BwtBlocks.tableBlocks.forEach(this::dropSelf);
        dropSelf(BwtBlocks.dirtSlabBlock);
        dropOther(BwtBlocks.dirtPathSlabBlock, BwtBlocks.dirtSlabBlock);
        add(BwtBlocks.grassSlabBlock, createSingleItemTableWithSilkTouch(BwtBlocks.grassSlabBlock, BwtBlocks.dirtSlabBlock));
        add(BwtBlocks.myceliumSlabBlock, createSingleItemTableWithSilkTouch(BwtBlocks.myceliumSlabBlock, BwtBlocks.dirtSlabBlock));
        add(BwtBlocks.podzolSlabBlock, createSingleItemTableWithSilkTouch(BwtBlocks.podzolSlabBlock, BwtBlocks.dirtSlabBlock));
    }

    private void addHempDrop() {
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        add(
                BwtBlocks.hempCropBlock,
                applyExplosionDecay(
                        BwtBlocks.hempCropBlock,
                        LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        // If fully grown, drop hemp item
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BwtBlocks.hempCropBlock)
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HempCropBlock.AGE, HempCropBlock.MAX_AGE))
                                        ).add(LootItem.lootTableItem(BwtItems.hempItem))
                                ).withPool(LootPool.lootPool()
                                        // Regardless of growth, drop some seeds
                                        .add(LootItem.lootTableItem(BwtItems.hempSeedsItem)
                                                .when(LootItemRandomChanceCondition.randomChance(0.5f))
                                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(enchantmentRegistry.getOrThrow(Enchantments.FORTUNE), 0.5f, 0))
                                        )
                                )
                )
        );
    }

    private LootTable.Builder unfiredDecoratedPotBlockWithSherdsDrops(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(block)
                                                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                        .include(DataComponents.POT_DECORATIONS)
                                                )
                                )
                );
    }

}

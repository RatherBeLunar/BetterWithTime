package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.CompatibilityTags;
import com.bwt.utils.DyeUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.AIR).add(
                BwtBlocks.detectorLogicBlock,
                BwtBlocks.lensBeamBlock
        );
        getOrCreateTagBuilder(BlockTags.REPLACEABLE).add(
                BwtBlocks.detectorLogicBlock,
                BwtBlocks.lensBeamBlock
        );
        getOrCreateTagBuilder(BlockTags.CLIMBABLE).add(BwtBlocks.ropeBlock);
        getOrCreateTagBuilder(BlockTags.RAILS).add(BwtBlocks.stoneDetectorRailBlock, BwtBlocks.obsidianDetectorRailBlock);
        getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES).add(BwtBlocks.obsidianPressuePlateBlock);
        getOrCreateTagBuilder(BlockTags.SLABS).add(BwtBlocks.companionSlabBlock).add(BwtBlocks.wickerSlabBlock);

        // Make planters behave like their corresponding blocks
        getOrCreateTagBuilder(BlockTags.DIRT).add(BwtBlocks.soilPlanterBlock, BwtBlocks.grassPlanterBlock);
        getOrCreateTagBuilder(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(BwtBlocks.soulSandPlanterBlock);
        getOrCreateTagBuilder(BlockTags.SOUL_SPEED_BLOCKS).add(BwtBlocks.soulSandPlanterBlock);
        getOrCreateTagBuilder(BwtBlockTags.CROPS_CAN_PLANT_ON).add(Blocks.FARMLAND, BwtBlocks.soilPlanterBlock);
        getOrCreateTagBuilder(BwtBlockTags.SOUL_SAND_PLANTS_CAN_PLANT_ON).add(Blocks.SOUL_SAND, BwtBlocks.soulSandPlanterBlock);
        getOrCreateTagBuilder(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY).add(
                Blocks.REDSTONE_WIRE,
                Blocks.REDSTONE_TORCH,
                Blocks.REDSTONE_WALL_TORCH,
                Blocks.REPEATER,
                Blocks.COMPARATOR,
                BwtBlocks.buddyBlock,
                BwtBlocks.detectorLogicBlock
        );

        getOrCreateTagBuilder(BwtBlockTags.BLOCK_DISPENSER_INHALE_VOID)
                .add(Blocks.NETHER_PORTAL);
        getOrCreateTagBuilder(BwtBlockTags.BLOCK_DISPENSER_INHALE_NOOP)
                .add(Blocks.STRUCTURE_VOID)
                .add(Blocks.STRUCTURE_BLOCK)
                .add(Blocks.PISTON_HEAD)
                .add(Blocks.MOVING_PISTON)
                .add(Blocks.END_PORTAL)
                .forceAddTag(BlockTags.WITHER_IMMUNE)
                .forceAddTag(BlockTags.FIRE);
        getOrCreateTagBuilder(BwtBlockTags.DETECTABLE_SMALL_CROPS)
                .add(Blocks.WHEAT)
                .add(Blocks.CARROTS)
                .add(Blocks.POTATOES);
        getOrCreateTagBuilder(BwtBlockTags.TRANSFERS_ROTATION_UPWARD_OVERRIDE)
                .forceAddTag(BlockTags.STAIRS)
                .forceAddTag(BlockTags.WALLS)
                .forceAddTag(BlockTags.ANVIL)
                .add(Blocks.SOUL_SAND)
                .add(Blocks.MUD)
                .add(Blocks.HONEY_BLOCK);
        getOrCreateTagBuilder(BlockTags.INFINIBURN_OVERWORLD).add(BwtBlocks.hibachiBlock);
        getOrCreateTagBuilder(BlockTags.INFINIBURN_NETHER).add(BwtBlocks.hibachiBlock);
        getOrCreateTagBuilder(BlockTags.INFINIBURN_END).add(BwtBlocks.hibachiBlock);
        getOrCreateTagBuilder(BlockTags.FIRE).add(BwtBlocks.stokedFireBlock);
        getOrCreateTagBuilder(BlockTags.WOOL).add(BwtBlocks.companionCubeBlock);
        getOrCreateTagBuilder(BwtBlockTags.WOOL_SLABS).add(BwtBlocks.companionSlabBlock);

        addTools();
        addMaterialInheritedBlockTags();
        addVases();
        addWoolSlabs();
        addSawTags();
        addBloodWoodTags();
        addCompatibilityTags();
    }

    private void addBloodWoodTags() {
        getOrCreateTagBuilder(BwtBlockTags.BLOOD_WOOD_LOGS).add(
                BwtBlocks.bloodWoodBlocks.logBlock,
                BwtBlocks.bloodWoodBlocks.woodBlock,
                BwtBlocks.bloodWoodBlocks.strippedLogBlock,
                BwtBlocks.bloodWoodBlocks.strippedWoodBlock
        );
        getOrCreateTagBuilder(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON).add(
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL,
                BwtBlocks.soulSandPlanterBlock
        );
        getOrCreateTagBuilder(BlockTags.LOGS).forceAddTag(BwtBlockTags.BLOOD_WOOD_LOGS);
        getOrCreateTagBuilder(BlockTags.LEAVES).add(BwtBlocks.bloodWoodBlocks.leavesBlock);
        getOrCreateTagBuilder(BlockTags.SAPLINGS).add(BwtBlocks.bloodWoodBlocks.saplingBlock);
        getOrCreateTagBuilder(BlockTags.PLANKS).add(BwtBlocks.bloodWoodBlocks.planksBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(BwtBlocks.bloodWoodBlocks.buttonBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(BwtBlocks.bloodWoodBlocks.fenceBlock);
        getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(BwtBlocks.bloodWoodBlocks.fenceGateBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(BwtBlocks.bloodWoodBlocks.pressurePlateBlock);
        getOrCreateTagBuilder(BlockTags.STANDING_SIGNS).add(BwtBlocks.bloodWoodBlocks.signBlock);
        getOrCreateTagBuilder(BlockTags.WALL_SIGNS).add(BwtBlocks.bloodWoodBlocks.wallSignBlock);
        getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS).add(BwtBlocks.bloodWoodBlocks.hangingSignBlock);
        getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS).add(BwtBlocks.bloodWoodBlocks.wallHangingSignBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(BwtBlocks.bloodWoodBlocks.slabBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(BwtBlocks.bloodWoodBlocks.stairsBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(BwtBlocks.bloodWoodBlocks.doorBlock);
        getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(BwtBlocks.bloodWoodBlocks.trapdoorBlock);
    }

    private void addTools() {
        Stream.of(
                BwtBlocks.sidingBlocks.stream(),
                BwtBlocks.mouldingBlocks.stream(),
                BwtBlocks.cornerBlocks.stream(),
                BwtBlocks.columnBlocks.stream(),
                BwtBlocks.pedestalBlocks.stream(),
                BwtBlocks.tableBlocks.stream()
        )
                .reduce(Stream::concat).orElseGet(Stream::empty)
                .forEach(materialInheritedBlock -> getOrCreateTagBuilder(materialInheritedBlock.isWood() ? BlockTags.AXE_MINEABLE : BlockTags.PICKAXE_MINEABLE).add(materialInheritedBlock));

        getOrCreateTagBuilder(BwtBlockTags.MATTOCK_MINEABLE).forceAddTag(BlockTags.PICKAXE_MINEABLE).forceAddTag(BlockTags.SHOVEL_MINEABLE);
        getOrCreateTagBuilder(BwtBlockTags.BATTLEAXE_MINEABLE).forceAddTag(BlockTags.AXE_MINEABLE).forceAddTag(BlockTags.SWORD_EFFICIENT);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(BwtBlocks.anchorBlock)
                .add(BwtBlocks.blockDispenserBlock)
                .add(BwtBlocks.buddyBlock)
                .add(BwtBlocks.cauldronBlock)
                .add(BwtBlocks.concentratedHellfireBlock)
                .add(BwtBlocks.crucibleBlock)
                .add(BwtBlocks.detectorBlock)
                .add(BwtBlocks.handCrankBlock)
                .add(BwtBlocks.hibachiBlock)
//                .add(BwtBlocks.infernalEnchanterBlock)
                .add(BwtBlocks.kilnBlock)
                .add(BwtBlocks.lensBlock)
                .add(BwtBlocks.lightBlockBlock)
                .add(BwtBlocks.millStoneBlock)
                .add(BwtBlocks.obsidianDetectorRailBlock)
                .add(BwtBlocks.obsidianPressuePlateBlock)
//                .add(BwtBlocks.pedestalBlock)
                .add(BwtBlocks.planterBlock)
                .add(BwtBlocks.ropeBlock)
                .add(BwtBlocks.soapBlock)
                .add(BwtBlocks.soilPlanterBlock)
                .add(BwtBlocks.soulForgeBlock)
                .add(BwtBlocks.soulSandPlanterBlock)
                .add(BwtBlocks.grassPlanterBlock)
                .add(BwtBlocks.stoneDetectorRailBlock)
                .add(BwtBlocks.turntableBlock)
                .add(BwtBlocks.urnBlock);

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(BwtBlocks.dungBlock)
                .add(BwtBlocks.unfiredCrucibleBlock)
                .add(BwtBlocks.unfiredPlanterBlock)
                .add(BwtBlocks.unfiredVaseBlock)
                .add(BwtBlocks.unfiredUrnBlock)
                .add(BwtBlocks.unfiredMouldBlock)
                .add(BwtBlocks.dirtSlabBlock)
                .add(BwtBlocks.dirtPathSlabBlock)
                .add(BwtBlocks.grassSlabBlock)
                .add(BwtBlocks.myceliumSlabBlock)
                .add(BwtBlocks.podzolSlabBlock);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(BwtBlocks.axleBlock)
                .add(BwtBlocks.axlePowerSourceBlock)
                .add(BwtBlocks.bellowsBlock)
                .add(BwtBlocks.bloodWoodBlocks.logBlock)
                .add(BwtBlocks.bloodWoodBlocks.strippedLogBlock)
                .add(BwtBlocks.bloodWoodBlocks.woodBlock)
                .add(BwtBlocks.bloodWoodBlocks.strippedWoodBlock)
                .add(BwtBlocks.bloodWoodBlocks.planksBlock)
                .add(BwtBlocks.bloodWoodBlocks.buttonBlock)
                .add(BwtBlocks.bloodWoodBlocks.fenceBlock)
                .add(BwtBlocks.bloodWoodBlocks.fenceGateBlock)
                .add(BwtBlocks.bloodWoodBlocks.pressurePlateBlock)
                .add(BwtBlocks.bloodWoodBlocks.signBlock)
                .add(BwtBlocks.bloodWoodBlocks.wallSignBlock)
                .add(BwtBlocks.bloodWoodBlocks.hangingSignBlock)
                .add(BwtBlocks.bloodWoodBlocks.wallHangingSignBlock)
                .add(BwtBlocks.bloodWoodBlocks.slabBlock)
                .add(BwtBlocks.bloodWoodBlocks.stairsBlock)
                .add(BwtBlocks.bloodWoodBlocks.doorBlock)
                .add(BwtBlocks.bloodWoodBlocks.trapdoorBlock)
                .add(BwtBlocks.gearBoxBlock)
                .add(BwtBlocks.grateBlock)
                .add(BwtBlocks.hopperBlock)
                .add(BwtBlocks.platformBlock)
                .add(BwtBlocks.pulleyBlock)
                .add(BwtBlocks.ropeBlock)
                .add(BwtBlocks.ropeCoilBlock)
                .add(BwtBlocks.sawBlock)
                .add(BwtBlocks.slatsBlock)
                .add(BwtBlocks.soapBlock)
//                .add(BwtBlocks.screwPumpBlock)
//                .add(BwtBlocks.tableBlock)
                .add(BwtBlocks.wickerBlock)
                .add(BwtBlocks.wickerSlabBlock)
                .add(BwtBlocks.wickerPaneBlock);

        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .add(BwtBlocks.bloodWoodBlocks.leavesBlock)
                .add(BwtBlocks.paddingBlock);

        // Where do these go?
//        .add(BwtBlocks.miningChargeBlock)
//        .add(BwtBlocks.netherGrothBlock)
//        .add(BwtBlocks.stakeBlock)

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT).add(BwtBlocks.ropeBlock, BwtBlocks.hempCropBlock);
    }

    protected void addMaterialInheritedBlockTags() {
        FabricTagBuilder woodenSidingBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_SIDING_BLOCKS);
        FabricTagBuilder woodenMouldingBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_MOULDING_BLOCKS);
        FabricTagBuilder woodenCornerBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_CORNER_BLOCKS);
        FabricTagBuilder sidingBuilder = getOrCreateTagBuilder(BwtBlockTags.SIDING_BLOCKS);
        FabricTagBuilder mouldingBuilder = getOrCreateTagBuilder(BwtBlockTags.MOULDING_BLOCKS);
        FabricTagBuilder cornerBuilder = getOrCreateTagBuilder(BwtBlockTags.CORNER_BLOCKS);
        FabricTagBuilder woodenColumnBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_COLUMN_BLOCKS);
        FabricTagBuilder woodenPedestalBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_PEDESTAL_BLOCKS);
        FabricTagBuilder woodenTableBuilder = getOrCreateTagBuilder(BwtBlockTags.WOODEN_TABLE_BLOCKS);
        FabricTagBuilder columnBuilder = getOrCreateTagBuilder(BwtBlockTags.COLUMN_BLOCKS);
        FabricTagBuilder pedestalBuilder = getOrCreateTagBuilder(BwtBlockTags.PEDESTAL_BLOCKS);
        FabricTagBuilder tableBuilder = getOrCreateTagBuilder(BwtBlockTags.TABLE_BLOCKS);
        BwtBlocks.sidingBlocks.stream().filter(MaterialInheritedBlock::isWood).forEach(woodenSidingBuilder::add);
        BwtBlocks.mouldingBlocks.stream().filter(MaterialInheritedBlock::isWood).forEach(woodenMouldingBuilder::add);
        BwtBlocks.cornerBlocks.stream().filter(MaterialInheritedBlock::isWood).forEach(woodenCornerBuilder::add);
        BwtBlocks.columnBlocks.stream().filter(MaterialInheritedBlock::isWood).forEach(woodenColumnBuilder::add);
        BwtBlocks.pedestalBlocks.stream().filter(MaterialInheritedBlock::isWood).forEach(woodenPedestalBuilder::add);
        BwtBlocks.tableBlocks.stream().filter(MaterialInheritedBlock::isWood).forEach(woodenTableBuilder::add);
        sidingBuilder.addTag(BwtBlockTags.WOODEN_SIDING_BLOCKS);
        mouldingBuilder.addTag(BwtBlockTags.WOODEN_MOULDING_BLOCKS);
        cornerBuilder.addTag(BwtBlockTags.WOODEN_CORNER_BLOCKS);
        columnBuilder.addTag(BwtBlockTags.WOODEN_COLUMN_BLOCKS);
        pedestalBuilder.addTag(BwtBlockTags.WOODEN_PEDESTAL_BLOCKS);
        tableBuilder.addTag(BwtBlockTags.WOODEN_TABLE_BLOCKS);
        BwtBlocks.sidingBlocks.stream().filter(Predicate.not(MaterialInheritedBlock::isWood)).forEach(sidingBuilder::add);
        BwtBlocks.mouldingBlocks.stream().filter(Predicate.not(MaterialInheritedBlock::isWood)).forEach(mouldingBuilder::add);
        BwtBlocks.cornerBlocks.stream().filter(Predicate.not(MaterialInheritedBlock::isWood)).forEach(cornerBuilder::add);
        BwtBlocks.columnBlocks.stream().filter(Predicate.not(MaterialInheritedBlock::isWood)).forEach(columnBuilder::add);
        BwtBlocks.pedestalBlocks.stream().filter(Predicate.not(MaterialInheritedBlock::isWood)).forEach(pedestalBuilder::add);
        BwtBlocks.tableBlocks.stream().filter(Predicate.not(MaterialInheritedBlock::isWood)).forEach(tableBuilder::add);
    }

    private void addVases() {
        FabricTagBuilder vasesBuilder = getOrCreateTagBuilder(BwtBlockTags.VASES);
        DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).forEach(vasesBuilder::add);
    }

    private void addWoolSlabs() {
        FabricTagBuilder woolSlabsBuilder = getOrCreateTagBuilder(BwtBlockTags.WOOL_SLABS);
        DyeUtils.streamColorItemsSorted(BwtBlocks.woolSlabBlocks).forEach(woolSlabsBuilder::add);
        getOrCreateTagBuilder(BlockTags.SLABS).forceAddTag(BwtBlockTags.WOOL_SLABS);
    }

    protected void addSawTags() {
        getOrCreateTagBuilder(BwtBlockTags.SURVIVES_SAW_BLOCK)
                .add(BwtBlocks.companionSlabBlock)
                .add(Blocks.MELON_STEM)
                .add(Blocks.PUMPKIN_STEM)
                .add(Blocks.ATTACHED_MELON_STEM)
                .add(Blocks.ATTACHED_PUMPKIN_STEM)
                .add(Blocks.FROGSPAWN)
                .forceAddTag(BlockTags.FIRE)
                .forceAddTag(BlockTags.SAPLINGS);

        getOrCreateTagBuilder(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)
                .forceAddTag(BlockTags.FLOWERS)
                .forceAddTag(BlockTags.LEAVES)
                .forceAddTag(BlockTags.WOODEN_DOORS)
                .forceAddTag(BlockTags.WOODEN_TRAPDOORS)
                .forceAddTag(BlockTags.WOODEN_BUTTONS)
                .forceAddTag(BlockTags.WOODEN_PRESSURE_PLATES)
                .forceAddTag(BlockTags.ALL_SIGNS)
                .forceAddTag(BlockTags.BANNERS)
                .forceAddTag(BlockTags.CANDLE_CAKES)
                .forceAddTag(BlockTags.CORAL_PLANTS)
                .forceAddTag(BlockTags.CORAL_PLANTS)
                .add(Blocks.BAMBOO)
                .add(Blocks.BARREL)
                .add(Blocks.BEEHIVE)
                .add(Blocks.BEETROOTS)
                .add(Blocks.BIG_DRIPLEAF)
                .add(Blocks.BIG_DRIPLEAF_STEM)
                .add(Blocks.BOOKSHELF)
                .add(Blocks.CACTUS)
                .add(Blocks.CAKE)
                .add(Blocks.CAMPFIRE)
                .add(Blocks.CARROTS)
                .add(Blocks.CARVED_PUMPKIN)
                .add(Blocks.CHEST)
                .add(Blocks.CHISELED_BOOKSHELF)
                .add(Blocks.CHORUS_PLANT)
                .add(Blocks.COBWEB)
                .add(Blocks.COCOA)
                .add(Blocks.COMPOSTER)
                .add(Blocks.CRAFTING_TABLE)
                .add(Blocks.CRIMSON_FUNGUS)
                .add(Blocks.CAVE_VINES)
                .add(Blocks.CAVE_VINES_PLANT)
                .add(Blocks.FERN)
                .add(Blocks.JACK_O_LANTERN)
                .add(Blocks.JUKEBOX)
                .add(Blocks.KELP)
                .add(Blocks.KELP_PLANT)
                .add(Blocks.LARGE_FERN)
                .add(Blocks.LECTERN)
                .add(Blocks.LILY_PAD)
                .add(Blocks.MANGROVE_ROOTS)
                .add(Blocks.MELON)
                .add(Blocks.POTATOES)
                .add(Blocks.PITCHER_CROP)
                .add(Blocks.PITCHER_PLANT)
                .add(Blocks.PUMPKIN)
                .add(Blocks.COMPARATOR)
                .add(Blocks.REPEATER)
                .add(Blocks.REDSTONE_TORCH)
                .add(Blocks.REDSTONE_WALL_TORCH)
                .add(Blocks.REDSTONE_WIRE)
                .add(Blocks.SEA_PICKLE)
                .add(Blocks.SEAGRASS)
                .add(Blocks.SMALL_DRIPLEAF)
                .add(Blocks.SOUL_CAMPFIRE)
                .add(Blocks.SOUL_TORCH)
                .add(Blocks.SOUL_WALL_TORCH)
                .add(Blocks.SPORE_BLOSSOM)
                .add(Blocks.SUGAR_CANE)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.TALL_SEAGRASS)
                .add(Blocks.TORCH)
                .add(Blocks.TORCHFLOWER)
                .add(Blocks.TORCHFLOWER_CROP)
                .add(Blocks.TRAPPED_CHEST)
                .add(Blocks.TRIPWIRE)
                .add(Blocks.TURTLE_EGG)
                .add(Blocks.TWISTING_VINES)
                .add(Blocks.TWISTING_VINES_PLANT)
                .add(Blocks.WALL_TORCH)
                .add(Blocks.WARPED_FUNGUS)
                .add(Blocks.WEEPING_VINES)
                .add(Blocks.WEEPING_VINES_PLANT)
                .add(Blocks.WHEAT)
                .add(BwtBlocks.axleBlock)
                .add(BwtBlocks.gearBoxBlock)
                .add(BwtBlocks.sawBlock)
                .add(BwtBlocks.grateBlock)
                .add(BwtBlocks.slatsBlock)
                .add(BwtBlocks.wickerPaneBlock)
                .add(BwtBlocks.hempCropBlock)
                .add(BwtBlocks.companionSlabBlock);

        getOrCreateTagBuilder(BwtBlockTags.CAN_CONVERT_TO_PODZOL)
                .add(Blocks.DIRT)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.MYCELIUM)
                .add(Blocks.ROOTED_DIRT)
                .add(Blocks.MOSS_BLOCK)
                .add(Blocks.MUD)
                .add(Blocks.MUDDY_MANGROVE_ROOTS);
        getOrCreateTagBuilder(BwtBlockTags.CAN_CONVERT_TO_PODZOL_SLAB)
                .add(BwtBlocks.dirtSlabBlock)
                .add(BwtBlocks.grassSlabBlock)
                .add(BwtBlocks.myceliumSlabBlock);
    }

    protected void addCompatibilityTags() {
        getOrCreateTagBuilder(CompatibilityTags.UNAFFECTED_BY_RICH_SOIL).add(BwtBlocks.hempCropBlock);
    }
}

package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.tags.BwtBlockTags;
import com.bwt.tags.CompatibilityTags;
import com.bwt.utils.DyeUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    public TagBuilder getOrCreateRawBuilder(TagKey<Block> tag) {
        return super.getOrCreateRawBuilder(tag);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        valueLookupBuilder(BlockTags.AIR).add(
                BwtBlocks.detectorLogicBlock,
                BwtBlocks.lensBeamBlock
        );
        valueLookupBuilder(BlockTags.REPLACEABLE).add(
                BwtBlocks.detectorLogicBlock,
                BwtBlocks.lensBeamBlock
        );
        valueLookupBuilder(BlockTags.IMPERMEABLE).add(BwtBlocks.lensBeamGlassBlock);
        valueLookupBuilder(ConventionalBlockTags.GLASS_BLOCKS).add(BwtBlocks.lensBeamGlassBlock);
        valueLookupBuilder(BlockTags.CLIMBABLE).add(BwtBlocks.ropeBlock);
        valueLookupBuilder(BlockTags.RAILS).add(BwtBlocks.stoneDetectorRailBlock, BwtBlocks.obsidianDetectorRailBlock);
        valueLookupBuilder(BlockTags.PRESSURE_PLATES).add(BwtBlocks.obsidianPressurePlateBlock);
        valueLookupBuilder(BlockTags.SLABS).add(BwtBlocks.companionSlabBlock).add(BwtBlocks.wickerSlabBlock);

        // Make planters behave like their corresponding blocks
        valueLookupBuilder(BlockTags.DIRT).add(BwtBlocks.soilPlanterBlock, BwtBlocks.grassPlanterBlock);
        valueLookupBuilder(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(BwtBlocks.soulSandPlanterBlock);
        valueLookupBuilder(BlockTags.SOUL_SPEED_BLOCKS).add(BwtBlocks.soulSandPlanterBlock);
        valueLookupBuilder(BwtBlockTags.CROPS_CAN_PLANT_ON).add(Blocks.FARMLAND, BwtBlocks.soilPlanterBlock);
        valueLookupBuilder(BwtBlockTags.SOUL_SAND_PLANTS_CAN_PLANT_ON).add(Blocks.SOUL_SAND, BwtBlocks.soulSandPlanterBlock);
        valueLookupBuilder(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY).add(
                Blocks.REDSTONE_WIRE,
                Blocks.REDSTONE_TORCH,
                Blocks.REDSTONE_WALL_TORCH,
                Blocks.REPEATER,
                Blocks.COMPARATOR,
                BwtBlocks.buddyBlock,
                BwtBlocks.detectorLogicBlock
        );

        valueLookupBuilder(BwtBlockTags.BLOCK_DISPENSER_INHALE_VOID)
                .add(Blocks.NETHER_PORTAL);
        valueLookupBuilder(BwtBlockTags.BLOCK_DISPENSER_INHALE_NOOP)
                .add(Blocks.STRUCTURE_VOID)
                .add(Blocks.STRUCTURE_BLOCK)
                .add(Blocks.PISTON_HEAD)
                .add(Blocks.MOVING_PISTON)
                .add(Blocks.END_PORTAL)
                .forceAddTag(BlockTags.WITHER_IMMUNE)
                .forceAddTag(BlockTags.FIRE);
        valueLookupBuilder(BwtBlockTags.DETECTABLE_SMALL_CROPS)
                .add(Blocks.WHEAT)
                .add(Blocks.CARROTS)
                .add(Blocks.POTATOES);
        valueLookupBuilder(BwtBlockTags.TRANSFERS_ROTATION_UPWARD_OVERRIDE)
                .forceAddTag(BlockTags.STAIRS)
                .forceAddTag(BlockTags.WALLS)
                .forceAddTag(BlockTags.ANVIL)
                .add(Blocks.SOUL_SAND)
                .add(Blocks.MUD)
                .add(Blocks.HONEY_BLOCK);
        valueLookupBuilder(BwtBlockTags.NETHER_GROTH_CAN_EAT)
                .add(
                        Blocks.RED_MUSHROOM,
                        Blocks.BROWN_MUSHROOM,
                        Blocks.CRIMSON_FUNGUS,
                        Blocks.WARPED_FUNGUS,
                        Blocks.WEEPING_VINES,
                        Blocks.WEEPING_VINES_PLANT,
                        Blocks.TWISTING_VINES,
                        Blocks.TWISTING_VINES_PLANT,
                        Blocks.CRIMSON_ROOTS,
                        Blocks.WARPED_ROOTS,
                        Blocks.NETHER_SPROUTS
                );
        valueLookupBuilder(BlockTags.INFINIBURN_OVERWORLD).add(BwtBlocks.hibachiBlock);
        valueLookupBuilder(BlockTags.INFINIBURN_NETHER).add(BwtBlocks.hibachiBlock);
        valueLookupBuilder(BlockTags.INFINIBURN_END).add(BwtBlocks.hibachiBlock);
        valueLookupBuilder(BlockTags.FIRE).add(BwtBlocks.stokedFireBlock);
        valueLookupBuilder(BlockTags.WOOL).add(BwtBlocks.companionCubeBlock);
        valueLookupBuilder(BwtBlockTags.WOOL_SLABS).add(BwtBlocks.companionSlabBlock);

        valueLookupBuilder(BwtBlockTags.HEATS_COOKING_STATIONS).add(Blocks.FIRE);
        valueLookupBuilder(BwtBlockTags.STOKES_COOKING_STATIONS).add(BwtBlocks.stokedFireBlock);

        addTools();
        addMaterialInheritedBlockTags();
        addVases();
        addWoolSlabs();
        addSawTags();
        addDirtSlabCompatibilityTags();
        addBloodWoodTags();
        addModCompatibilityTags();
    }

    private void addBloodWoodTags() {
        valueLookupBuilder(BwtBlockTags.BLOOD_WOOD_LOGS).add(
                BwtBlocks.bloodWoodBlocks.logBlock,
                BwtBlocks.bloodWoodBlocks.woodBlock,
                BwtBlocks.bloodWoodBlocks.strippedLogBlock,
                BwtBlocks.bloodWoodBlocks.strippedWoodBlock
        );
        valueLookupBuilder(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON).add(
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL,
                BwtBlocks.soulSandPlanterBlock
        );
        valueLookupBuilder(BlockTags.LOGS).forceAddTag(BwtBlockTags.BLOOD_WOOD_LOGS);
        valueLookupBuilder(BlockTags.LEAVES).add(BwtBlocks.bloodWoodBlocks.leavesBlock);
        valueLookupBuilder(BlockTags.SAPLINGS).add(BwtBlocks.bloodWoodBlocks.saplingBlock);
        valueLookupBuilder(BlockTags.PLANKS).add(BwtBlocks.bloodWoodBlocks.planksBlock);
        valueLookupBuilder(BlockTags.WOODEN_BUTTONS).add(BwtBlocks.bloodWoodBlocks.buttonBlock);
        valueLookupBuilder(BlockTags.WOODEN_FENCES).add(BwtBlocks.bloodWoodBlocks.fenceBlock);
        valueLookupBuilder(BlockTags.FENCE_GATES).add(BwtBlocks.bloodWoodBlocks.fenceGateBlock);
        valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(BwtBlocks.bloodWoodBlocks.pressurePlateBlock);
        valueLookupBuilder(BlockTags.WOODEN_SLABS).add(BwtBlocks.bloodWoodBlocks.slabBlock);
        valueLookupBuilder(BlockTags.WOODEN_STAIRS).add(BwtBlocks.bloodWoodBlocks.stairsBlock);
        valueLookupBuilder(BlockTags.WOODEN_DOORS).add(BwtBlocks.bloodWoodBlocks.doorBlock);
        valueLookupBuilder(BlockTags.WOODEN_TRAPDOORS).add(BwtBlocks.bloodWoodBlocks.trapdoorBlock);
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
                .forEach(materialInheritedBlock -> valueLookupBuilder(materialInheritedBlock.isWood() ? BlockTags.MINEABLE_WITH_AXE : BlockTags.MINEABLE_WITH_PICKAXE).add(materialInheritedBlock));

        valueLookupBuilder(BwtBlockTags.MATTOCK_MINEABLE).forceAddTag(BlockTags.MINEABLE_WITH_PICKAXE).forceAddTag(BlockTags.MINEABLE_WITH_SHOVEL);
        valueLookupBuilder(BwtBlockTags.BATTLEAXE_MINEABLE).forceAddTag(BlockTags.MINEABLE_WITH_AXE).forceAddTag(BlockTags.SWORD_EFFICIENT);
        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
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
                .add(BwtBlocks.obsidianPressurePlateBlock)
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

        valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BwtBlocks.dungBlock)
                .add(BwtBlocks.unfiredDecoratedPotBlock)
                .add(BwtBlocks.unfiredDecoratedPotBlockWithSherds)
                .add(BwtBlocks.unfiredCrucibleBlock)
                .add(BwtBlocks.unfiredPlanterBlock)
                .add(BwtBlocks.unfiredVaseBlock)
                .add(BwtBlocks.unfiredUrnBlock)
                .add(BwtBlocks.unfiredFlowerPotBlock)
                .add(BwtBlocks.dirtSlabBlock)
                .add(BwtBlocks.dirtPathSlabBlock)
                .add(BwtBlocks.grassSlabBlock)
                .add(BwtBlocks.myceliumSlabBlock)
                .add(BwtBlocks.podzolSlabBlock);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
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
                .add(BwtBlocks.bloodWoodBlocks.slabBlock)
                .add(BwtBlocks.bloodWoodBlocks.stairsBlock)
                .add(BwtBlocks.bloodWoodBlocks.doorBlock)
                .add(BwtBlocks.bloodWoodBlocks.trapdoorBlock)
                .add(BwtBlocks.gearBoxBlock)
                .add(BwtBlocks.redstoneClutchBlock)
                .add(BwtBlocks.grateBlock)
                .add(BwtBlocks.hopperBlock)
                .add(BwtBlocks.platformBlock)
                .add(BwtBlocks.pulleyBlock)
                .add(BwtBlocks.ropeBlock)
                .add(BwtBlocks.ropeCoilBlock)
                .add(BwtBlocks.sawBlock)
                .add(BwtBlocks.slatsBlock)
                .add(BwtBlocks.soapBlock)
                .add(BwtBlocks.screwPumpBlock)
//                .add(BwtBlocks.tableBlock)
                .add(BwtBlocks.wickerBlock)
                .add(BwtBlocks.wickerSlabBlock)
                .add(BwtBlocks.wickerPaneBlock);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE)
                .add(BwtBlocks.bloodWoodBlocks.leavesBlock)
                .add(BwtBlocks.paddingBlock);

        // Where do these go?
//        .add(BwtBlocks.miningChargeBlock)
//        .add(BwtBlocks.netherGrothBlock)
//        .add(BwtBlocks.stakeBlock)

        valueLookupBuilder(BlockTags.SWORD_EFFICIENT).add(BwtBlocks.ropeBlock, BwtBlocks.hempCropBlock);
    }

    protected void addMaterialInheritedBlockTags() {
        TagAppender<Block, Block> woodenSidingBuilder = valueLookupBuilder(BwtBlockTags.WOODEN_SIDING_BLOCKS);
        TagAppender<Block, Block> woodenMouldingBuilder = valueLookupBuilder(BwtBlockTags.WOODEN_MOULDING_BLOCKS);
        TagAppender<Block, Block> woodenCornerBuilder = valueLookupBuilder(BwtBlockTags.WOODEN_CORNER_BLOCKS);
        TagAppender<Block, Block> sidingBuilder = valueLookupBuilder(BwtBlockTags.SIDING_BLOCKS);
        TagAppender<Block, Block> mouldingBuilder = valueLookupBuilder(BwtBlockTags.MOULDING_BLOCKS);
        TagAppender<Block, Block> cornerBuilder = valueLookupBuilder(BwtBlockTags.CORNER_BLOCKS);
        TagAppender<Block, Block> woodenColumnBuilder = valueLookupBuilder(BwtBlockTags.WOODEN_COLUMN_BLOCKS);
        TagAppender<Block, Block> woodenPedestalBuilder = valueLookupBuilder(BwtBlockTags.WOODEN_PEDESTAL_BLOCKS);
        TagAppender<Block, Block> woodenTableBuilder = valueLookupBuilder(BwtBlockTags.WOODEN_TABLE_BLOCKS);
        TagAppender<Block, Block> columnBuilder = valueLookupBuilder(BwtBlockTags.COLUMN_BLOCKS);
        TagAppender<Block, Block> pedestalBuilder = valueLookupBuilder(BwtBlockTags.PEDESTAL_BLOCKS);
        TagAppender<Block, Block> tableBuilder = valueLookupBuilder(BwtBlockTags.TABLE_BLOCKS);
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
        TagAppender<Block, Block> vasesBuilder = valueLookupBuilder(BwtBlockTags.VASES);
        DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).forEach(vasesBuilder::add);
    }

    private void addWoolSlabs() {
        TagAppender<Block, Block> woolSlabsBuilder = valueLookupBuilder(BwtBlockTags.WOOL_SLABS);
        DyeUtils.streamColorItemsSorted(BwtBlocks.woolSlabBlocks).forEach(woolSlabsBuilder::add);
        valueLookupBuilder(BlockTags.SLABS).forceAddTag(BwtBlockTags.WOOL_SLABS);
    }

    protected void addSawTags() {
        valueLookupBuilder(BwtBlockTags.SURVIVES_SAW_BLOCK)
                .add(BwtBlocks.companionSlabBlock)
                .add(Blocks.MELON_STEM)
                .add(Blocks.PUMPKIN_STEM)
                .add(Blocks.ATTACHED_MELON_STEM)
                .add(Blocks.ATTACHED_PUMPKIN_STEM)
                .add(Blocks.FROGSPAWN)
                .forceAddTag(BlockTags.FIRE)
                .forceAddTag(BlockTags.SAPLINGS);

        valueLookupBuilder(BwtBlockTags.SAW_BREAKS_DROPS_LOOT)
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
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.SHORT_DRY_GRASS)
                .add(Blocks.BUSH)
                .add(Blocks.DEAD_BUSH)
                .add(Blocks.FIREFLY_BUSH)
                .add(Blocks.SMALL_DRIPLEAF)
                .add(Blocks.SOUL_CAMPFIRE)
                .add(Blocks.SOUL_TORCH)
                .add(Blocks.SOUL_WALL_TORCH)
                .add(Blocks.SPORE_BLOSSOM)
                .add(Blocks.SUGAR_CANE)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.TALL_DRY_GRASS)
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
                .add(BwtBlocks.redstoneClutchBlock)
                .add(BwtBlocks.sawBlock)
                .add(BwtBlocks.grateBlock)
                .add(BwtBlocks.slatsBlock)
                .add(BwtBlocks.wickerPaneBlock)
                .add(BwtBlocks.hempCropBlock)
                .add(BwtBlocks.companionSlabBlock);
    }

    protected void addDirtSlabCompatibilityTags() {
        valueLookupBuilder(BwtBlockTags.CAN_CONVERT_TO_PODZOL)
                .add(Blocks.DIRT)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.MYCELIUM)
                .add(Blocks.ROOTED_DIRT)
                .add(Blocks.MOSS_BLOCK)
                .add(Blocks.PALE_MOSS_BLOCK)
                .add(Blocks.MUD)
                .add(Blocks.MUDDY_MANGROVE_ROOTS);
        valueLookupBuilder(BwtBlockTags.CAN_CONVERT_TO_PODZOL_SLAB)
                .add(BwtBlocks.dirtSlabBlock)
                .add(BwtBlocks.grassSlabBlock)
                .add(BwtBlocks.myceliumSlabBlock);
    }

    protected void addModCompatibilityTags() {
        valueLookupBuilder(CompatibilityTags.UNAFFECTED_BY_RICH_SOIL).add(BwtBlocks.hempCropBlock);
    }
}

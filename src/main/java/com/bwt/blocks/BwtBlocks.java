package com.bwt.blocks;

import com.bwt.blocks.axles.AxleBlock;
import com.bwt.blocks.axles.AxlePowerSourceBlock;
import com.bwt.blocks.axles.CreativePowerSourceBlock;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.blocks.blood_wood.BloodWoodBlocks;
import com.bwt.blocks.cauldron.CauldronBlock;
import com.bwt.blocks.crucible.CrucibleBlock;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.blocks.detector.DetectorLogicBlock;
import com.bwt.blocks.dirt_slab.DirtPathSlabBlock;
import com.bwt.blocks.dirt_slab.DirtSlabBlock;
import com.bwt.blocks.dirt_slab.GrassSlabBlock;
import com.bwt.blocks.lens.LensBeamBlock;
import com.bwt.blocks.lens.LensBeamGlassBlock;
import com.bwt.blocks.lens.LensBlock;
import com.bwt.blocks.dirt_slab.MyceliumSlabBlock;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.blocks.mining_charge.MiningChargeBlock;
import com.bwt.blocks.pulley.PulleyBlock;
import com.bwt.blocks.soul_forge.SoulForgeBlock;
import com.bwt.blocks.turntable.TurntableBlock;
import com.bwt.blocks.unfired_pottery.*;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.RegistrationBase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.ArrayList;
import java.util.HashMap;

public class BwtBlocks extends RegistrationBase implements ModInitializer {
    public static final AqueductBlock aqueductBlock = registerBlockWithItem(
            "aqueduct",
            AqueductBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
    );
	public static final AnchorBlock anchorBlock = registerBlockWithItem(
            "anchor",
            AnchorBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
    );
	public static final AxleBlock axleBlock = registerBlock(
            "axle",
            AxleBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .forceSolidOn()
                    .noOcclusion()
    );
    public static final AxlePowerSourceBlock axlePowerSourceBlock = registerBlock(
            "axle_power_source",
            AxlePowerSourceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(axleBlock)
    );
	public static final BellowsBlock bellowsBlock = registerBlockWithItem(
            "bellows",
            BellowsBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );
	public static final BlockDispenserBlock blockDispenserBlock = registerBlockWithItem(
            "block_dispenser",
            BlockDispenserBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)
                    .destroyTime(3.5f)
    );
    public static final BloodWoodBlocks bloodWoodBlocks = new BloodWoodBlocks().initialize();

	public static final BuddyBlock buddyBlock = registerBlockWithItem(
            "buddy_block",
            BuddyBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(3.5f)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .requiresCorrectToolForDrops()
    );
	public static final CauldronBlock cauldronBlock = registerBlockWithItem(
            "cauldron",
            CauldronBlock::new,
            BlockBehaviour.Properties.of()
                    .isRedstoneConductor(Blocks::never)
                    .noOcclusion()
                    .destroyTime(3.5f)
                    .explosionResistance(10f)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
    );
    public static final ArrayList<ColumnBlock> columnBlocks = new ArrayList<>();
	public static final Block concentratedHellfireBlock = registerBlockWithItem(
            "concentrated_hellfire_block",
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.FIRE)
                    .sound(SoundType.METAL)
    );
	public static final Block companionCubeBlock = registerBlockWithItem(
            "companion_cube",
            CompanionCubeBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .destroyTime(0.4f)
    );
	public static final Block companionSlabBlock = registerBlockWithItem(
            "companion_slab",
            CompanionSlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(companionCubeBlock)
    );
	public static final ArrayList<CornerBlock> cornerBlocks = new ArrayList<>();
    public static final CreativePowerSourceBlock creativePowerSouceBlock = registerBlockWithItem(
            "creative_power_source",
            CreativePowerSourceBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2F)
                    .sound(SoundType.WOOD)
                    .forceSolidOn()
                    .noOcclusion()
    );
	public static final Block crucibleBlock = registerBlockWithItem(
            "crucible",
            CrucibleBlock::new,
            BlockBehaviour.Properties.of()
                    .isRedstoneConductor(Blocks::never)
                    .noOcclusion()
                    .destroyTime(0.6f)
                    .explosionResistance(3f)
                    .sound(SoundType.GLASS)
                    .mapColor(MapColor.WOOL)
                    .requiresCorrectToolForDrops()
    );
	public static final Block detectorBlock = registerBlockWithItem(
            "detector_block",
            DetectorBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)
                    .destroyTime(3.5f)
    );
	public static final Block detectorLogicBlock = registerBlock(
            "detector_logic_block",
            DetectorLogicBlock::new,
            BlockBehaviour.Properties.of()
                    .replaceable()
                    .noCollission()
                    .noLootTable()
                    .pushReaction(PushReaction.DESTROY)
                    .air()
    );
    public static final Block dungBlock = registerBlockWithItem(
            "dung_block",
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .mapColor(MapColor.COLOR_BROWN)
                    .sound(SoundType.HONEY_BLOCK)
    );
    public static final Block gearBoxBlock = registerBlockWithItem(
            "gear_box",
            GearBoxBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .destroyTime(2F)
    );
	public static final Block grateBlock = registerBlockWithItem(
            "grate",
            IronBarsBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(0.5f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
    );
	public static final Block handCrankBlock = registerBlockWithItem(
            "hand_crank",
            HandCrankBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(0.5f)
                    .sound(SoundType.WOOD)
                    .forceSolidOn()
                    .noOcclusion()
                    .isValidSpawn(Blocks::never)
                    .isSuffocating(Blocks::never)
                    .isViewBlocking(Blocks::never)
                    .requiresCorrectToolForDrops()
    );
	public static final Block hempCropBlock = registerBlock(
            "hemp_crop_block",
            HempCropBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SUGAR_CANE)
    );
	public static final Block hibachiBlock = registerBlockWithItem(
            "hibachi",
            HibachiBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(3.5f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
    );
	public static final Block hopperBlock = registerBlockWithItem(
            "hopper",
            MechHopperBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .sound(SoundType.WOOD)
                    .forceSolidOn()
                    .noOcclusion()
    );
    //	public static final Block infernalEnchanterBlock = RegistrationUtils.registerBlockWithItem("infernal_enchanter", InfernalEnchanterBlock::new, AbstractBlock.Settings.create());
	public static final Block kilnBlock = registerBlock(
            "kiln",
            KilnBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
    );
	public static final LensBlock lensBlock = registerBlockWithItem(
            "lens",
            LensBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(3.5f)
                    .sound(SoundType.METAL)
                    .forceSolidOn()
                    .pushReaction(PushReaction.BLOCK)
    );
    public static final LensBeamBlock lensBeamBlock = registerBlock(
            "lens_beam",
            LensBeamBlock::new,
            BlockBehaviour.Properties.of()
                    .replaceable()
                    .noCollission()
                    .noLootTable()
                    .pushReaction(PushReaction.DESTROY)
                    .lightLevel(state -> state.getValue(LensBeamBlock.TERMINUS) ? 14 : 0)
                    .emissiveRendering(((state, level, pos) -> state.getValue(LensBeamBlock.TERMINUS)))
    );
    public static final LensBeamGlassBlock lensBeamGlassBlock = registerBlock(
            "lens_beam_glass",
            properties -> new LensBeamGlassBlock(Blocks.GLASS, properties),
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.GLASS)
                    .lightLevel(state -> state.getValue(LensBeamBlock.TERMINUS) ? 14 : 0)
                    .emissiveRendering(((state, level, pos) -> state.getValue(LensBeamBlock.TERMINUS)))
    );
	public static final Block lightBlockBlock = registerBlockWithItem(
            "light_block",
            LightBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.4f)
                    .lightLevel(Blocks.litBlockEmission(15))
    );
	public static final Block millStoneBlock = registerBlockWithItem(
            "mill_stone",
            MillStoneBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)
                    .destroyTime(3.5f)
    );
	public static final MiningChargeBlock miningChargeBlock = registerBlockWithItem(
            "mining_charge",
            MiningChargeBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .ignitedByLava()
                    .isRedstoneConductor(Blocks::never)
    );
	public static final ArrayList<MouldingBlock> mouldingBlocks = new ArrayList<>();
	public static final Block obsidianPressurePlateBlock = registerBlockWithItem(
            "obsidian_pressure_plate",
            ObsidianPressurePlateBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)
                    .strength(50.0f, 1200.0f)
    );
	public static final Block obsidianDetectorRailBlock = registerBlockWithItem(
            "obsidian_detector_rail",
            DetectorRailBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DETECTOR_RAIL)
                    .strength(25.0f, 1200.0f)
    );
    public static final ArrayList<PedestalBlock> pedestalBlocks = new ArrayList<>();
	public static final Block planterBlock = registerBlockWithItem(
            "planter",
            PlanterBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)
                    .noOcclusion()
                    .destroyTime(0.6f)
    );
	public static final Block soilPlanterBlock = registerBlockWithItem(
            "soil_planter",
            SoilPlanterBlock::new,
            BlockBehaviour.Properties.ofFullCopy(planterBlock)
    );
	public static final Block soulSandPlanterBlock = registerBlockWithItem(
            "soul_sand_planter",
            SoulSandPlanterBlock::new,
            BlockBehaviour.Properties.ofFullCopy(planterBlock)
    );
	public static final Block grassPlanterBlock = registerBlockWithItem(
            "grass_planter",
            GrassPlanterBlock::new,
            BlockBehaviour.Properties.ofFullCopy(planterBlock));
    public static final Block paddingBlock = registerBlockWithItem(
            "padding_block",
            PaddingBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .mapColor(MapColor.QUARTZ)
                    .sound(SoundType.WOOL)
    );
	public static final Block platformBlock = registerBlockWithItem(
            "platform",
            PlatformBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .noOcclusion()
                    .isValidSpawn(Blocks::never)
                    .isRedstoneConductor(Blocks::never)
                    .isSuffocating(Blocks::never)
                    .isViewBlocking(Blocks::never)
                    .destroyTime(2f)
                    .ignitedByLava()
    );
	public static final Block pulleyBlock = registerBlockWithItem(
            "pulley",
            PulleyBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .destroyTime(2f)
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .pushReaction(PushReaction.IGNORE)
    );
    public static final Block redstoneClutchBlock = registerBlockWithItem(
            "redstone_clutch",
            RedstoneClutchBlock::new,
            BlockBehaviour.Properties.ofFullCopy(gearBoxBlock)
    );
    public static final Block ropeCoilBlock = registerBlockWithItem(
            "rope_coil_block",
            BlockBehaviour.Properties.of()
                    .destroyTime(1f)
                    .mapColor(MapColor.COLOR_BROWN)
                    .sound(SoundType.GRASS)
    );
	public static final RopeBlock ropeBlock = registerBlockWithItem(
            "rope",
            RopeBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(0.5f)
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
    );
	public static final Block sawBlock = registerBlockWithItem(
            "saw",
            SawBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .ignitedByLava()
                    .sound(SoundType.WOOD)
                    .noOcclusion()
    );
	public static final ScrewPumpBlock screwPumpBlock = registerBlockWithItem(
            "screw_pump",
            ScrewPumpBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .destroyTime(2f)
                    .explosionResistance(5f)
    );
    public static final ArrayList<SidingBlock> sidingBlocks = new ArrayList<>();
	public static final Block slatsBlock = registerBlockWithItem(
            "slats",
            IronBarsBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .noOcclusion()
    );
    public static final Block soapBlock = registerBlockWithItem(
            "soap_block",
            SimpleFacingBlock::new,
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .mapColor(MapColor.COLOR_PINK)
                    .sound(SoundType.SLIME_BLOCK)
    );
    //	public static final Block stakeBlock = RegistrationUtils.registerBlockWithItem("stake", StakeBlock::new, BlockBehaviour.Properties.of());
    public static final StokedFireBlock stokedFireBlock = registerBlock(
            "stoked_fire",
            StokedFireBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.FIRE)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 15)
                    .sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final Block stoneDetectorRailBlock = registerBlockWithItem(
            "stone_detector_rail",
            DetectorRailBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DETECTOR_RAIL)
    );
	public static final Block soulForgeBlock = registerBlockWithItem(
            "soul_forge",
            SoulForgeBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)
    );
    public static final ArrayList<TableBlock> tableBlocks = new ArrayList<>();
	public static final Block turntableBlock = registerBlockWithItem(
            "turntable",
            TurntableBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2f)
                    .sound(SoundType.STONE)
                    .mapColor(Blocks.PISTON_HEAD.defaultMapColor())
    );
    public static final UnfiredPotteryBlock unfiredDecoratedPotBlock = registerBlockWithItem(
            "unfired_decorated_pot",
            UnfiredDecoratedPotBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
    );
    public static final UnfiredDecoratedPotBlockWithSherds unfiredDecoratedPotBlockWithSherds = registerBlockWithItem(
            "unfired_decorated_pot_with_sherds",
            UnfiredDecoratedPotBlockWithSherds::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never),
            new Item.Properties().component(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY)
    );
	public static final UnfiredPotteryBlock unfiredCrucibleBlock = registerBlockWithItem(
            "unfired_crucible",
            UnfiredCrucibleBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredPlanterBlock = registerBlockWithItem(
            "unfired_planter",
            UnfiredPlanterBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredVaseBlock = registerBlockWithItem(
            "unfired_vase",
            UnfiredVaseBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredUrnBlock = registerBlockWithItem(
            "unfired_urn",
            UnfiredUrnBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
    );
    public static final UnfiredPotteryBlock unfiredFlowerPotBlock = registerBlockWithItem(
            "unfired_flower_pot",
            UnfiredFlowerPotBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
    );
	public static final Block urnBlock = registerBlockWithItem(
            "urn",
            UrnBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)
                    .noOcclusion()
                    .isRedstoneConductor(Blocks::never)
                    .isValidSpawn(Blocks::never)
                    .destroyTime(2f)
    );
	public static final HashMap<DyeColor, VaseBlock> vaseBlocks = new HashMap<>();
	public static final Block wickerPaneBlock = registerBlockWithItem(
            "wicker",
            IronBarsBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0.5f)
                    .sound(SoundType.GRASS)
                    .ignitedByLava()
                    .noOcclusion()
    );
    public static final Block wickerBlock = registerBlockWithItem(
            "wicker_block",
            BlockBehaviour.Properties.of()
                    .destroyTime(2f)
                    .ignitedByLava()
                    .mapColor(MapColor.PODZOL)
                    .sound(SoundType.GRASS)
    );
    public static final Block wickerSlabBlock = registerBlockWithItem(
            "wicker_slab",
            SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(wickerBlock)
    );
    public static final HashMap<DyeColor, SlabBlock> woolSlabBlocks = new HashMap<>();
    public static final Block vineTrapBlock = registerBlockWithItem(
            "vine_trap",
            VineTrapBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .isValidSpawn(Blocks::never)
                    .noCollission()
    );
    public static final Block dirtSlabBlock = registerBlockWithItem(
            "dirt_slab",
            properties -> new DirtSlabBlock(properties, Blocks.DIRT),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
    );
    public static final Block dirtPathSlabBlock = registerBlockWithItem(
            "dirt_path_slab",
            properties -> new DirtPathSlabBlock(properties, Blocks.DIRT_PATH),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT_PATH)
    );
    public static final Block grassSlabBlock = registerBlockWithItem(
            "grass_slab",
            properties -> new GrassSlabBlock(properties, Blocks.GRASS_BLOCK),
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
    );
    public static final Block myceliumSlabBlock = registerBlockWithItem(
            "mycelium_slab",
            properties -> new MyceliumSlabBlock(properties, Blocks.MYCELIUM),
            BlockBehaviour.Properties.ofFullCopy(Blocks.MYCELIUM)
    );
    public static final Block podzolSlabBlock = registerBlockWithItem(
            "podzol_slab",
            properties -> new MyceliumSlabBlock(properties, Blocks.PODZOL),
            BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL)
    );

    public static final Block netherGroth = registerBlockWithItem(
            "nether_groth",
            NetherGrothBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .isRedstoneConductor(Blocks::never)
                    .randomTicks()
                    .strength(0.2f)
                    .speedFactor(0.4F)
                    .sound(SoundType.FUNGUS)
                    .pushReaction(PushReaction.DESTROY)
                    .speedFactor(0.8f)
    );

    public static final Block grothedNetherrackBlock = registerBlock(
            "grothed_netherrack",
            GrothedNetherrackBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(0.4F)
                    .sound(SoundType.NETHERRACK)
    );

    @Override
    public void onInitialize() {
        // Mini blocks
        MaterialInheritedBlock.registerMaterialBlocks(
                sidingBlocks, mouldingBlocks, cornerBlocks,
                columnBlocks, pedestalBlocks, tableBlocks
        );
        // Keep track of the blood wood mini blocks for easy access later
        bloodWoodBlocks.initializeMiniBlocks(
                sidingBlocks, mouldingBlocks, cornerBlocks,
                columnBlocks, pedestalBlocks, tableBlocks
        );
        // Vases
        VaseBlock.registerColors(vaseBlocks);
        // Wool slabs
        DyeUtils.WOOL_COLORS.forEach((dyeColor, woolBlock) -> woolSlabBlocks.put(
                dyeColor,
                registerBlockWithItem(
                        dyeColor.getName() + "_wool_slab",
                        SlabBlock::new,
                        BlockBehaviour.Properties.ofFullCopy(woolBlock)
                )
        ));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(content -> {
            content.addAfter(Items.NETHER_WART, BwtBlocks.netherGroth);
            content.addAfter(Items.CHERRY_LOG, BwtBlocks.bloodWoodBlocks.logBlock);
            content.addAfter(Items.CHERRY_LEAVES, BwtBlocks.bloodWoodBlocks.leavesBlock);
            content.addAfter(Items.CHERRY_SAPLING, BwtBlocks.bloodWoodBlocks.saplingBlock);
            content.addAfter(Blocks.DIRT, dirtSlabBlock);
            content.addAfter(Blocks.DIRT_PATH, dirtPathSlabBlock);
            content.addAfter(Blocks.GRASS_BLOCK, grassSlabBlock);
            content.addAfter(Blocks.MYCELIUM, myceliumSlabBlock);
            content.addAfter(Blocks.PODZOL, podzolSlabBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(content -> {
            content.acceptAll(DyeUtils.streamColorItemsSorted(vaseBlocks).map(vaseBlock -> vaseBlock.asItem().getDefaultInstance()).toList());
            content.acceptAll(DyeUtils.streamColorItemsSorted(woolSlabBlocks).map(woolSlabBlock -> woolSlabBlock.asItem().getDefaultInstance()).toList());
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(axleBlock);
            content.accept(creativePowerSouceBlock);
            content.accept(gearBoxBlock);
            content.accept(redstoneClutchBlock);
            content.accept(hibachiBlock);
            content.accept(lightBlockBlock);
            content.accept(blockDispenserBlock);
            content.accept(obsidianPressurePlateBlock);
            content.accept(detectorBlock);
            content.accept(buddyBlock);
            content.accept(millStoneBlock);
            content.accept(handCrankBlock);
            content.accept(stoneDetectorRailBlock);
            content.accept(obsidianDetectorRailBlock);
            content.accept(sawBlock);
            content.accept(hopperBlock);
            content.accept(pulleyBlock);
            content.accept(anchorBlock);
            content.accept(platformBlock);
            content.accept(turntableBlock);
            content.accept(bellowsBlock);
            content.accept(cauldronBlock);
            content.accept(crucibleBlock);
            content.accept(soulForgeBlock);
            content.accept(lensBlock);
//            content.add(BwtBlocks.aqueductBlock);
//            content.add(BwtBlocks.screwPumpBlock);
            content.addAfter(Items.TNT, miningChargeBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.accept(cauldronBlock);
            content.accept(crucibleBlock);
            content.accept(planterBlock);
            content.accept(soilPlanterBlock);
            content.accept(soulSandPlanterBlock);
            content.accept(grassPlanterBlock);
            content.accept(urnBlock);
            content.accept(unfiredDecoratedPotBlock);
            content.accept(unfiredCrucibleBlock);
            content.accept(unfiredPlanterBlock);
            content.accept(unfiredVaseBlock);
            content.accept(unfiredUrnBlock);
            content.accept(unfiredFlowerPotBlock);
            content.addAfter(Items.CRAFTING_TABLE, soulForgeBlock);
            content.addAfter(Items.SCAFFOLDING, BwtBlocks.vineTrapBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            content.addAfter(Items.CHERRY_BUTTON,
                    BwtBlocks.bloodWoodBlocks.logBlock,
                    BwtBlocks.bloodWoodBlocks.woodBlock,
                    BwtBlocks.bloodWoodBlocks.strippedLogBlock,
                    BwtBlocks.bloodWoodBlocks.strippedWoodBlock,
                    BwtBlocks.bloodWoodBlocks.planksBlock,
                    BwtBlocks.bloodWoodBlocks.stairsBlock,
                    BwtBlocks.bloodWoodBlocks.slabBlock,
                    BwtBlocks.bloodWoodBlocks.fenceBlock,
                    BwtBlocks.bloodWoodBlocks.fenceGateBlock,
                    BwtBlocks.bloodWoodBlocks.doorBlock,
                    BwtBlocks.bloodWoodBlocks.trapdoorBlock,
                    BwtBlocks.bloodWoodBlocks.pressurePlateBlock,
                    BwtBlocks.bloodWoodBlocks.buttonBlock
            );
            for (int i = 0; i < sidingBlocks.size(); i++) {
                SidingBlock sidingBlock = sidingBlocks.get(i);
                MouldingBlock mouldingBlock = mouldingBlocks.get(i);
                CornerBlock cornerBlock = cornerBlocks.get(i);
                ColumnBlock columnBlock = columnBlocks.get(i);
                PedestalBlock pedestalBlock = pedestalBlocks.get(i);
                TableBlock tableBlock = tableBlocks.get(i);
                if (content.getDisplayStacks().stream().anyMatch(itemStack -> itemStack.is(sidingBlock.fullBlock.asItem()))) {
                    content.addAfter(sidingBlock.fullBlock, sidingBlock, mouldingBlock, cornerBlock, columnBlock, pedestalBlock, tableBlock);
                }
            }
            content.accept(companionCubeBlock);
            content.accept(companionSlabBlock);
            content.accept(grateBlock);
            content.accept(slatsBlock);
            content.accept(wickerPaneBlock);
            content.accept(wickerBlock);
            content.accept(wickerSlabBlock);
            content.accept(platformBlock);
            content.accept(soapBlock);
            content.accept(dungBlock);
            content.accept(paddingBlock);
            content.accept(ropeCoilBlock);
            content.accept(concentratedHellfireBlock);
            content.accept(dirtSlabBlock);
            content.accept(dirtPathSlabBlock);
            content.accept(grassSlabBlock);
            content.accept(myceliumSlabBlock);
            content.accept(podzolSlabBlock);
        });

        FlattenableBlockRegistry.register(BwtBlocks.grassSlabBlock, BwtBlocks.dirtPathSlabBlock.defaultBlockState());
        FlattenableBlockRegistry.register(BwtBlocks.dirtSlabBlock, BwtBlocks.dirtPathSlabBlock.defaultBlockState());
        FlattenableBlockRegistry.register(BwtBlocks.myceliumSlabBlock, BwtBlocks.dirtPathSlabBlock.defaultBlockState());
        FlattenableBlockRegistry.register(BwtBlocks.podzolSlabBlock, BwtBlocks.dirtPathSlabBlock.defaultBlockState());
    }
}

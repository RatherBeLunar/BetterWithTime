package com.bwt.blocks;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.blocks.cauldron.CauldronBlock;
import com.bwt.blocks.crucible.CrucibleBlock;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.blocks.detector.DetectorLogicBlock;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.blocks.pulley.PulleyBlock;
import com.bwt.blocks.soul_forge.SoulForgeBlock;
import com.bwt.blocks.turntable.TurntableBlock;
import com.bwt.utils.DyeUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class BwtBlocks implements ModInitializer {

	public static final Block anchorBlock = new AnchorBlock(FabricBlockSettings.create()
            .hardness(2f)
            .sounds(BlockSoundGroup.STONE)
            .nonOpaque()
            .solid()
            .requiresTool()
    );
	public static final Block axleBlock = new AxleBlock(FabricBlockSettings.create()
            .hardness(2F)
            .sounds(BlockSoundGroup.WOOD)
            .burnable()
            .solid()
            .nonOpaque()
    );
    public static final Block axlePowerSourceBlock = new AxlePowerSourceBlock(FabricBlockSettings.copyOf(axleBlock.getSettings()));
//	public static final Block barrelBlock = new BarrelBlock(FabricBlockSettings.create());
	public static final Block bellowsBlock = new BellowsBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
	public static final BlockDispenserBlock blockDispenserBlock = new BlockDispenserBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)
            .hardness(3.5f)
    );
//	public static final Block bloodWoodBlock = new BloodWoodBlock(FabricBlockSettings.create());
//	Blood Wood Sapling
	public static final Block buddyBlock = new BuddyBlock(FabricBlockSettings.create()
            .hardness(3.5f)
            .sounds(BlockSoundGroup.STONE)
            .mapColor(MapColor.LIGHT_GRAY)
            .requiresTool()
    );
	public static final Block cauldronBlock = new CauldronBlock(FabricBlockSettings.create()
            .solidBlock(Blocks::never)
            .notSolid()
            .nonOpaque()
            .hardness(3.5f)
            .resistance(10f)
            .sounds(BlockSoundGroup.METAL)
            .mapColor(MapColor.BLACK)
            .requiresTool()
    );
//	public static final Block canvasBlock = new CanvasBlock(FabricBlockSettings.create());
    public static final ArrayList<ColumnBlock> columnBlocks = new ArrayList<>();
	public static final Block concentratedHellfireBlock = new Block(FabricBlockSettings.create().hardness(2f).requiresTool().mapColor(MapColor.BRIGHT_RED).sounds(BlockSoundGroup.METAL));
	public static final Block companionCubeBlock = new CompanionCubeBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL)
            .hardness(0.4f)
    );
	public static final Block companionSlabBlock = new CompanionSlabBlock(FabricBlockSettings.copyOf(companionCubeBlock));
	public static final ArrayList<CornerBlock> cornerBlocks = new ArrayList<>();
	public static final Block crucibleBlock = new CrucibleBlock(FabricBlockSettings.create()
            .sounds(BlockSoundGroup.GLASS)
            .hardness(0.6f)
            .resistance(3f)
    );
	public static final Block detectorBlock = new DetectorBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)
            .hardness(3.5f)
    );
	public static final Block detectorLogicBlock = new DetectorLogicBlock(FabricBlockSettings.copyOf(Blocks.AIR));
    public static final Block dungBlock = new Block(FabricBlockSettings.create().hardness(2f).mapColor(MapColor.BROWN).sounds(BlockSoundGroup.HONEY));
    public static final Block gearBoxBlock = new GearBoxBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
            .hardness(2F)
    );
	public static final Block grateBlock = new PaneBlock(FabricBlockSettings.create()
            .hardness(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
    );
	public static final Block handCrankBlock = new HandCrankBlock(FabricBlockSettings.create()
            .hardness(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .solid()
            .nonOpaque()
            .allowsSpawning(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .requiresTool()
    );
	public static final Block hempCropBlock = new HempCropBlock(FabricBlockSettings.copyOf(Blocks.SUGAR_CANE));
	public static final Block hibachiBlock = new HibachiBlock(FabricBlockSettings.create()
            .hardness(3.5f)
            .sounds(BlockSoundGroup.STONE)
            .solidBlock(Blocks::never)
            .requiresTool()
    );
	public static final Block hopperBlock = new MechHopperBlock(FabricBlockSettings.create()
            .hardness(2f)
            .sounds(BlockSoundGroup.WOOD)
            .solid()
            .nonOpaque()
    );
//	public static final Block infernalEnchanterBlock = new InfernalEnchanterBlock(FabricBlockSettings.create());
	public static final Block kilnBlock = new KilnBlock(FabricBlockSettings.copyOf(Blocks.BRICKS));
//	public static final Block lensBlock = new LensBlock(FabricBlockSettings.create());
	public static final Block lightBlockBlock = new LightBlock(FabricBlockSettings.copyOf(Blocks.GLASS)
            .strength(0.4f)
            .luminance(Blocks.createLightLevelFromLitBlockState(15))
    );
	public static final Block millStoneBlock = new MillStoneBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)
            .hardness(3.5f)
    );
//	public static final Block miningChargeBlock = new MiningChargeBlock(FabricBlockSettings.create());
	public static final ArrayList<MouldingBlock> mouldingBlocks = new ArrayList<>();
//	public static final Block netherGrothBlock = new NetherGrothBlock(FabricBlockSettings.create());
//	public static final Block obsidianDetectorRailBlock = new ObsidianDetectorRailBlock(FabricBlockSettings.create());
	public static final Block obsidianPressuePlateBlock = new ObsidianPressurePlateBlock(FabricBlockSettings.copyOf(Blocks.STONE_PRESSURE_PLATE)
            .strength(50.0f, 1200.0f)
    );
	public static final Block obsidianDetectorRailBlock = new DetectorRailBlock(FabricBlockSettings.copyOf(Blocks.DETECTOR_RAIL)
            .strength(25.0f, 1200.0f)
    );
    public static final ArrayList<PedestalBlock> pedestalBlocks = new ArrayList<>();
	public static final Block planterBlock = new PlanterBlock(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)
            .nonOpaque()
            .hardness(0.6f)
    );
	public static final Block soilPlanterBlock = new SoilPlanterBlock(FabricBlockSettings.copyOf(planterBlock));
	public static final Block soulSandPlanterBlock = new SoulSandPlanterBlock(FabricBlockSettings.copyOf(planterBlock));
	public static final Block grassPlanterBlock = new GrassPlanterBlock(FabricBlockSettings.copyOf(planterBlock));
    public static final Block paddingBlock = new PaddingBlock(FabricBlockSettings.create().hardness(2f).mapColor(MapColor.OFF_WHITE).sounds(BlockSoundGroup.WOOL));
	public static final Block platformBlock = new PlatformBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
            .nonOpaque()
            .allowsSpawning(Blocks::never)
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .hardness(2f)
            .burnable()
    );
	public static final Block pulleyBlock = new PulleyBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
            .hardness(2f)
            .mapColor(MapColor.TERRACOTTA_BROWN)
            .pistonBehavior(PistonBehavior.IGNORE)
    );
    public static final Block ropeCoilBlock = new RopeBlock(FabricBlockSettings.create().hardness(1f).mapColor(MapColor.BROWN).sounds(BlockSoundGroup.GRASS));
	public static final Block ropeBlock = new RopeBlock(FabricBlockSettings.create()
            .hardness(0.5f)
            .sounds(BlockSoundGroup.GRASS)
    );
	public static final Block sawBlock = new SawBlock(FabricBlockSettings.create()
            .hardness(2f)
            .burnable()
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
    );
//	public static final Block screwPumpBlock = new ScrewPumpBlock(FabricBlockSettings.create());
    public static final ArrayList<SidingBlock> sidingBlocks = new ArrayList<>();
	public static final Block slatsBlock = new PaneBlock(FabricBlockSettings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .burnable()
            .nonOpaque()
    );
    public static final Block soapBlock = new SimpleFacingBlock(FabricBlockSettings.create().hardness(2f).mapColor(MapColor.PINK).sounds(BlockSoundGroup.SLIME));
//	public static final Block stakeBlock = new StakeBlock(FabricBlockSettings.create());
    public static final Block stokedFireBlock = new StokedFireBlock(FabricBlockSettings.copyOf(Blocks.SOUL_FIRE));
    public static final Block stoneDetectorRailBlock = new DetectorRailBlock(FabricBlockSettings.copyOf(Blocks.DETECTOR_RAIL));
	public static final Block soulForgeBlock = new SoulForgeBlock(FabricBlockSettings.copyOf(Blocks.ANVIL));
    public static final ArrayList<TableBlock> tableBlocks = new ArrayList<>();
	public static final Block turntableBlock = new TurntableBlock(FabricBlockSettings.create()
            .strength(2f)
            .sounds(BlockSoundGroup.STONE)
            .mapColor(Blocks.PISTON_HEAD.getDefaultMapColor())
    );
	public static final UnfiredPotteryBlock unfiredCrucibleBlock = new UnfiredCrucibleBlock(FabricBlockSettings.copyOf(Blocks.CLAY)
            .nonOpaque()
            .notSolid()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredPlanterBlock = new UnfiredPlanterBlock(FabricBlockSettings.copyOf(Blocks.CLAY)
            .nonOpaque()
            .notSolid()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredVaseBlock = new UnfiredVaseBlock(FabricBlockSettings.copyOf(Blocks.CLAY)
            .nonOpaque()
            .notSolid()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredUrnBlock = new UnfiredUrnBlock(FabricBlockSettings.copyOf(Blocks.CLAY)
            .nonOpaque()
            .notSolid()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredMouldBlock = new UnfiredMouldBlock(FabricBlockSettings.copyOf(Blocks.CLAY)
            .nonOpaque()
            .notSolid()
            .solidBlock(Blocks::never)
    );
	public static final Block urnBlock = new UrnBlock(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)
            .nonOpaque()
            .solidBlock(Blocks::never)
            .allowsSpawning(Blocks::never)
            .hardness(2f)
    );
	public static final HashMap<DyeColor, VaseBlock> vaseBlocks = new HashMap<>();
	public static final Block wickerPaneBlock = new PaneBlock(FabricBlockSettings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .nonOpaque()
    );
    public static final Block wickerBlock = new Block(FabricBlockSettings.create().hardness(2f).burnable().mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.GRASS));
    public static final Block wickerSlabBlock = new SlabBlock(FabricBlockSettings.copyOf(wickerBlock));
    public static final HashMap<DyeColor, SlabBlock> woolSlabBlocks = new HashMap<>();


    @Override
    public void onInitialize() {
        // Axle
        Registry.register(Registries.BLOCK, new Identifier("bwt", "axle"), axleBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "axle"), new BlockItem(axleBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "axle_power_source"), axlePowerSourceBlock);
        // Gearbox
        Registry.register(Registries.BLOCK, new Identifier("bwt", "gear_box"), gearBoxBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "gear_box"), new BlockItem(gearBoxBlock, new FabricItemSettings()));
        // Hibachi
        Registry.register(Registries.BLOCK, new Identifier("bwt", "hibachi"), hibachiBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "hibachi"), new BlockItem(hibachiBlock, new FabricItemSettings()));
        // Light Block
        Registry.register(Registries.BLOCK, new Identifier("bwt", "light_block"), lightBlockBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "light_block"), new BlockItem(lightBlockBlock, new FabricItemSettings()));
        // Block Dispenser
        Registry.register(Registries.BLOCK, new Identifier("bwt", "block_dispenser"), blockDispenserBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "block_dispenser"), new BlockItem(blockDispenserBlock, new FabricItemSettings()));
        // Cauldron / Stewing Pot
        Registry.register(Registries.BLOCK, new Identifier("bwt", "cauldron"), cauldronBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "cauldron"), new BlockItem(cauldronBlock, new FabricItemSettings()));
        // Obsidian pressure plate
        Registry.register(Registries.BLOCK, new Identifier("bwt", "obsidian_pressure_plate"), obsidianPressuePlateBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "obsidian_pressure_plate"), new BlockItem(obsidianPressuePlateBlock, new FabricItemSettings()));
        // Hemp crop
        Registry.register(Registries.BLOCK, new Identifier("bwt", "hemp_crop_block"), hempCropBlock);
        // Detector Block
        Registry.register(Registries.BLOCK, new Identifier("bwt", "detector_block"), detectorBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "detector_block"), new BlockItem(detectorBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "detector_logic_block"), detectorLogicBlock);
        // Mill Stone
        Registry.register(Registries.BLOCK, new Identifier("bwt", "mill_stone"), millStoneBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "mill_stone"), new BlockItem(millStoneBlock, new FabricItemSettings()));
        // Companion Cube
        Registry.register(Registries.BLOCK, new Identifier("bwt", "companion_cube"), companionCubeBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "companion_cube"), new BlockItem(companionCubeBlock, new FabricItemSettings()));
        // Companion Slab
        Registry.register(Registries.BLOCK, new Identifier("bwt", "companion_slab"), companionSlabBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "companion_slab"), new BlockItem(companionSlabBlock, new FabricItemSettings()));
        // Hand Crank
        Registry.register(Registries.BLOCK, new Identifier("bwt", "hand_crank"), handCrankBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "hand_crank"), new BlockItem(handCrankBlock, new FabricItemSettings()));
        // Anchor
        Registry.register(Registries.BLOCK, new Identifier("bwt", "anchor"), anchorBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "anchor"), new BlockItem(anchorBlock, new FabricItemSettings()));
        // Rope
        Registry.register(Registries.BLOCK, new Identifier("bwt", "rope"), ropeBlock);
        // Stone Detector Rail
        Registry.register(Registries.BLOCK, new Identifier("bwt", "stone_detector_rail"), stoneDetectorRailBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "stone_detector_rail"), new BlockItem(stoneDetectorRailBlock, new FabricItemSettings()));
        // Obsidian Detector Rail
        Registry.register(Registries.BLOCK, new Identifier("bwt", "obsidian_detector_rail"), obsidianDetectorRailBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "obsidian_detector_rail"), new BlockItem(obsidianDetectorRailBlock, new FabricItemSettings()));
        // Bwt Hopper
        Registry.register(Registries.BLOCK, new Identifier("bwt", "hopper"), hopperBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "hopper"), new BlockItem(hopperBlock, new FabricItemSettings()));
        // Grate
        Registry.register(Registries.BLOCK, new Identifier("bwt", "grate"), grateBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "grate"), new BlockItem(grateBlock, new FabricItemSettings()));
        // Slats
        Registry.register(Registries.BLOCK, new Identifier("bwt", "slats"), slatsBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "slats"), new BlockItem(slatsBlock, new FabricItemSettings()));
        // Wicker
        Registry.register(Registries.BLOCK, new Identifier("bwt", "wicker"), wickerPaneBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "wicker"), new BlockItem(wickerPaneBlock, new FabricItemSettings()));
        // Saw
        Registry.register(Registries.BLOCK, new Identifier("bwt", "saw"), sawBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "saw"), new BlockItem(sawBlock, new FabricItemSettings()));
        // Pulley
        Registry.register(Registries.BLOCK, new Identifier("bwt", "pulley"), pulleyBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "pulley"), new BlockItem(pulleyBlock, new FabricItemSettings()));
        // Platform
        Registry.register(Registries.BLOCK, new Identifier("bwt", "platform"), platformBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "platform"), new BlockItem(platformBlock, new FabricItemSettings()));
        // Turntable
        Registry.register(Registries.BLOCK, new Identifier("bwt", "turntable"), turntableBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "turntable"), new BlockItem(turntableBlock, new FabricItemSettings()));
        // Stoked Fire
        Registry.register(Registries.BLOCK, new Identifier("bwt", "stoked_fire"), stokedFireBlock);
        // Bellows
        Registry.register(Registries.BLOCK, new Identifier("bwt", "bellows"), bellowsBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "bellows"), new BlockItem(bellowsBlock, new FabricItemSettings()));
        // Unfired Pottery
        Registry.register(Registries.BLOCK, new Identifier("bwt", "unfired_crucible"), unfiredCrucibleBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "unfired_crucible"), new BlockItem(unfiredCrucibleBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "unfired_planter"), unfiredPlanterBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "unfired_planter"), new BlockItem(unfiredPlanterBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "unfired_vase"), unfiredVaseBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "unfired_vase"), new BlockItem(unfiredVaseBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "unfired_urn"), unfiredUrnBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "unfired_urn"), new BlockItem(unfiredUrnBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "unfired_mould"), unfiredMouldBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "unfired_mould"), new BlockItem(unfiredMouldBlock, new FabricItemSettings()));
        // Kiln
        Registry.register(Registries.BLOCK, new Identifier("bwt", "kiln"), kilnBlock);
        // Mini blocks
        MaterialInheritedBlock.registerMaterialBlocks(
                sidingBlocks, mouldingBlocks, cornerBlocks,
                columnBlocks, pedestalBlocks, tableBlocks
        );
        // Crucible
        Registry.register(Registries.BLOCK, new Identifier("bwt", "crucible"), crucibleBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "crucible"), new BlockItem(crucibleBlock, new FabricItemSettings()));
        // Planters
        Registry.register(Registries.BLOCK, new Identifier("bwt", "planter"), planterBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "planter"), new BlockItem(planterBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "soil_planter"), soilPlanterBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "soil_planter"), new BlockItem(soilPlanterBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "soul_sand_planter"), soulSandPlanterBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "soul_sand_planter"), new BlockItem(soulSandPlanterBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "grass_planter"), grassPlanterBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "grass_planter"), new BlockItem(grassPlanterBlock, new FabricItemSettings()));
        // Vases
        VaseBlock.registerColors(vaseBlocks);
        // Urn
        Registry.register(Registries.BLOCK, new Identifier("bwt", "urn"), urnBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "urn"), new BlockItem(urnBlock, new FabricItemSettings()));
        // SoulForge
        Registry.register(Registries.BLOCK, new Identifier("bwt", "soul_forge"), soulForgeBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "soul_forge"), new BlockItem(soulForgeBlock, new FabricItemSettings()));
        // Wool slabs
        DyeUtils.WOOL_COLORS.forEach((dyeColor, woolBlock) -> {
            SlabBlock woolSlabBlock = new SlabBlock(FabricBlockSettings.copyOf(woolBlock));
            woolSlabBlocks.put(dyeColor, woolSlabBlock);
            Registry.register(Registries.BLOCK, new Identifier("bwt", dyeColor.getName() + "_wool_slab"), woolSlabBlock);
            Registry.register(Registries.ITEM, new Identifier("bwt", dyeColor.getName() + "_wool_slab"), new BlockItem(woolSlabBlock, new FabricItemSettings()));
        });
        // Buddy Block
        Registry.register(Registries.BLOCK, new Identifier("bwt", "buddy_block"), buddyBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "buddy_block"), new BlockItem(buddyBlock, new FabricItemSettings()));
        // Aesthetic compacting blocks
        Registry.register(Registries.BLOCK, new Identifier("bwt", "soap_block"), soapBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "soap_block"), new BlockItem(soapBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "wicker_block"), wickerBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "wicker_block"), new BlockItem(wickerBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "dung_block"), dungBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "dung_block"), new BlockItem(dungBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "padding_block"), paddingBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "padding_block"), new BlockItem(paddingBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "rope_coil_block"), ropeCoilBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "rope_coil_block"), new BlockItem(ropeCoilBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "concentrated_hellfire_block"), concentratedHellfireBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "concentrated_hellfire_block"), new BlockItem(concentratedHellfireBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "wicker_slab"), wickerSlabBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "wicker_slab"), new BlockItem(wickerSlabBlock, new FabricItemSettings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(content -> {
            content.addAll(DyeUtils.streamColorItemsSorted(vaseBlocks).map(vaseBlock -> vaseBlock.asItem().getDefaultStack()).toList());
            content.addAll(DyeUtils.streamColorItemsSorted(woolSlabBlocks).map(woolSlabBlock -> woolSlabBlock.asItem().getDefaultStack()).toList());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(axleBlock);
            content.add(gearBoxBlock);
            content.add(hibachiBlock);
            content.add(lightBlockBlock);
            content.add(blockDispenserBlock);
            content.add(obsidianPressuePlateBlock);
            content.add(detectorBlock);
            content.add(buddyBlock);
            content.add(millStoneBlock);
            content.add(handCrankBlock);
            content.add(stoneDetectorRailBlock);
            content.add(obsidianDetectorRailBlock);
            content.add(sawBlock);
            content.add(hopperBlock);
            content.add(pulleyBlock);
            content.add(anchorBlock);
            content.add(platformBlock);
            content.add(turntableBlock);
            content.add(bellowsBlock);
            content.add(cauldronBlock);
            content.add(crucibleBlock);
            content.add(soulForgeBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(cauldronBlock);
            content.add(crucibleBlock);
            content.add(planterBlock);
            content.add(soilPlanterBlock);
            content.add(soulSandPlanterBlock);
            content.add(grassPlanterBlock);
            content.add(urnBlock);
            content.add(unfiredCrucibleBlock);
            content.add(unfiredPlanterBlock);
            content.add(unfiredVaseBlock);
            content.add(unfiredUrnBlock);
            content.add(unfiredMouldBlock);
            content.addAfter(Items.CRAFTING_TABLE, soulForgeBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            for (int i = 0; i < sidingBlocks.size(); i++) {
                SidingBlock sidingBlock = sidingBlocks.get(i);
                MouldingBlock mouldingBlock = mouldingBlocks.get(i);
                CornerBlock cornerBlock = cornerBlocks.get(i);
                ColumnBlock columnBlock = columnBlocks.get(i);
                PedestalBlock pedestalBlock = pedestalBlocks.get(i);
                TableBlock tableBlock = tableBlocks.get(i);
                if (content.getDisplayStacks().stream().anyMatch(itemStack -> itemStack.isOf(sidingBlock.fullBlock.asItem()))) {
                    content.addAfter(sidingBlock.fullBlock, sidingBlock, mouldingBlock, cornerBlock, columnBlock, pedestalBlock, tableBlock);
                }
            }
            content.add(companionCubeBlock);
            content.add(companionSlabBlock);
            content.add(grateBlock);
            content.add(slatsBlock);
            content.add(wickerPaneBlock);
            content.add(wickerBlock);
            content.add(wickerSlabBlock);
            content.add(platformBlock);
            content.add(soapBlock);
            content.add(dungBlock);
            content.add(paddingBlock);
            content.add(ropeCoilBlock);
            content.add(concentratedHellfireBlock);
        });
    }
}

package com.bwt.blocks;

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
import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.Sherds;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.HashMap;

public class BwtBlocks implements ModInitializer {

    public static final AqueductBlock aqueductBlock = new AqueductBlock(AbstractBlock.Settings.copy(Blocks.BRICKS));
	public static final Block anchorBlock = new AnchorBlock(AbstractBlock.Settings.create()
            .hardness(2f)
            .sounds(BlockSoundGroup.STONE)
            .nonOpaque()
            .solid()
            .requiresTool()
    );
	public static final Block axleBlock = new AxleBlock(AbstractBlock.Settings.create()
            .hardness(2F)
            .sounds(BlockSoundGroup.WOOD)
            .burnable()
            .solid()
            .nonOpaque()
    );
    public static final Block axlePowerSourceBlock = new AxlePowerSourceBlock(AbstractBlock.Settings.copy(axleBlock));
	public static final Block bellowsBlock = new BellowsBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS));
	public static final BlockDispenserBlock blockDispenserBlock = new BlockDispenserBlock(AbstractBlock.Settings.copy(Blocks.DISPENSER)
            .hardness(3.5f)
    );
    public static final BloodWoodBlocks bloodWoodBlocks = new BloodWoodBlocks().initialize();

	public static final Block buddyBlock = new BuddyBlock(AbstractBlock.Settings.create()
            .hardness(3.5f)
            .sounds(BlockSoundGroup.STONE)
            .mapColor(MapColor.LIGHT_GRAY)
            .requiresTool()
    );
	public static final Block cauldronBlock = new CauldronBlock(AbstractBlock.Settings.create()
            .solidBlock(Blocks::never)
            .nonOpaque()
            .hardness(3.5f)
            .resistance(10f)
            .sounds(BlockSoundGroup.METAL)
            .mapColor(MapColor.BLACK)
            .requiresTool()
    );
//	public static final Block canvasBlock = new CanvasBlock(AbstractBlock.Settings.create());
    public static final ArrayList<ColumnBlock> columnBlocks = new ArrayList<>();
	public static final Block concentratedHellfireBlock = new Block(AbstractBlock.Settings.create().hardness(2f).requiresTool().mapColor(MapColor.BRIGHT_RED).sounds(BlockSoundGroup.METAL));
	public static final Block companionCubeBlock = new CompanionCubeBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)
            .hardness(0.4f)
    );
	public static final Block companionSlabBlock = new CompanionSlabBlock(AbstractBlock.Settings.copy(companionCubeBlock));
	public static final ArrayList<CornerBlock> cornerBlocks = new ArrayList<>();
	public static final Block crucibleBlock = new CrucibleBlock(AbstractBlock.Settings.create()
            .solidBlock(Blocks::never)
            .nonOpaque()
            .hardness(0.6f)
            .resistance(3f)
            .sounds(BlockSoundGroup.GLASS)
            .mapColor(MapColor.WHITE_GRAY)
            .requiresTool()
    );
	public static final Block detectorBlock = new DetectorBlock(AbstractBlock.Settings.copy(Blocks.DISPENSER)
            .hardness(3.5f)
    );
	public static final Block detectorLogicBlock = new DetectorLogicBlock(AbstractBlock.Settings.create()
            .replaceable()
            .noCollision()
            .dropsNothing()
            .pistonBehavior(PistonBehavior.DESTROY)
    );
    public static final Block dungBlock = new Block(AbstractBlock.Settings.create().hardness(2f).mapColor(MapColor.BROWN).sounds(BlockSoundGroup.HONEY));
    public static final Block gearBoxBlock = new GearBoxBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .hardness(2F)
    );
	public static final Block grateBlock = new PaneBlock(AbstractBlock.Settings.create()
            .hardness(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
    );
	public static final Block handCrankBlock = new HandCrankBlock(AbstractBlock.Settings.create()
            .hardness(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .solid()
            .nonOpaque()
            .allowsSpawning(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .requiresTool()
    );
	public static final Block hempCropBlock = new HempCropBlock(AbstractBlock.Settings.copy(Blocks.SUGAR_CANE));
	public static final Block hibachiBlock = new HibachiBlock(AbstractBlock.Settings.create()
            .hardness(3.5f)
            .sounds(BlockSoundGroup.STONE)
            .requiresTool()
    );
	public static final Block hopperBlock = new MechHopperBlock(AbstractBlock.Settings.create()
            .hardness(2f)
            .sounds(BlockSoundGroup.WOOD)
            .solid()
            .nonOpaque()
    );
//	public static final Block infernalEnchanterBlock = new InfernalEnchanterBlock(AbstractBlock.Settings.create());
	public static final Block kilnBlock = new KilnBlock(AbstractBlock.Settings.copy(Blocks.BRICKS));
	public static final LensBlock lensBlock = new LensBlock(AbstractBlock.Settings.create()
            .hardness(3.5f)
            .sounds(BlockSoundGroup.METAL)
            .solid()
            .pistonBehavior(PistonBehavior.BLOCK)
    );
    public static final LensBeamBlock lensBeamBlock = new LensBeamBlock(AbstractBlock.Settings.create()
            .replaceable()
            .noCollision()
            .dropsNothing()
            .pistonBehavior(PistonBehavior.DESTROY)
            .luminance(state -> state.get(LensBeamBlock.TERMINUS) ? 14 : 0)
            .emissiveLighting(((state, world, pos) -> state.get(LensBeamBlock.TERMINUS)))
    );
    public static final LensBeamGlassBlock lensBeamGlassBlock = new LensBeamGlassBlock(
            Blocks.GLASS,
            AbstractBlock.Settings
                    .copy(Blocks.GLASS)
                    .luminance(state -> state.get(LensBeamBlock.TERMINUS) ? 14 : 0)
                    .emissiveLighting(((state, world, pos) -> state.get(LensBeamBlock.TERMINUS)))
    );
	public static final Block lightBlockBlock = new LightBlock(AbstractBlock.Settings.copy(Blocks.GLASS)
            .strength(0.4f)
            .luminance(Blocks.createLightLevelFromLitBlockState(15))
    );
	public static final Block millStoneBlock = new MillStoneBlock(AbstractBlock.Settings.copy(Blocks.DISPENSER)
            .hardness(3.5f)
    );
	public static final MiningChargeBlock miningChargeBlock = new MiningChargeBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.BROWN)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never)
    );
	public static final ArrayList<MouldingBlock> mouldingBlocks = new ArrayList<>();
	public static final Block obsidianPressurePlateBlock = new ObsidianPressurePlateBlock(AbstractBlock.Settings.copy(Blocks.STONE_PRESSURE_PLATE)
            .strength(50.0f, 1200.0f)
    );
	public static final Block obsidianDetectorRailBlock = new DetectorRailBlock(AbstractBlock.Settings.copy(Blocks.DETECTOR_RAIL)
            .strength(25.0f, 1200.0f)
    );
    public static final ArrayList<PedestalBlock> pedestalBlocks = new ArrayList<>();
	public static final Block planterBlock = new PlanterBlock(AbstractBlock.Settings.copy(Blocks.TERRACOTTA)
            .nonOpaque()
            .hardness(0.6f)
    );
	public static final Block soilPlanterBlock = new SoilPlanterBlock(AbstractBlock.Settings.copy(planterBlock));
	public static final Block soulSandPlanterBlock = new SoulSandPlanterBlock(AbstractBlock.Settings.copy(planterBlock));
	public static final Block grassPlanterBlock = new GrassPlanterBlock(AbstractBlock.Settings.copy(planterBlock));
    public static final Block paddingBlock = new PaddingBlock(AbstractBlock.Settings.create().hardness(2f).mapColor(MapColor.OFF_WHITE).sounds(BlockSoundGroup.WOOL));
	public static final Block platformBlock = new PlatformBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .nonOpaque()
            .allowsSpawning(Blocks::never)
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never)
            .hardness(2f)
            .burnable()
    );
	public static final Block pulleyBlock = new PulleyBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .hardness(2f)
            .mapColor(MapColor.TERRACOTTA_BROWN)
            .pistonBehavior(PistonBehavior.IGNORE)
    );
    public static final Block redstoneClutchBlock = new RedstoneClutchBlock(AbstractBlock.Settings.copy(gearBoxBlock));
    public static final Block ropeCoilBlock = new Block(AbstractBlock.Settings.create().hardness(1f).mapColor(MapColor.BROWN).sounds(BlockSoundGroup.GRASS));
	public static final RopeBlock ropeBlock = new RopeBlock(AbstractBlock.Settings.create()
            .hardness(0.5f)
            .sounds(BlockSoundGroup.GRASS)
            .pistonBehavior(PistonBehavior.DESTROY)
    );
	public static final Block sawBlock = new SawBlock(AbstractBlock.Settings.create()
            .hardness(2f)
            .burnable()
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
    );
	public static final ScrewPumpBlock screwPumpBlock = new ScrewPumpBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)
            .hardness(2f)
            .resistance(5f)
    );
    public static final ArrayList<SidingBlock> sidingBlocks = new ArrayList<>();
	public static final Block slatsBlock = new PaneBlock(AbstractBlock.Settings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .burnable()
            .nonOpaque()
    );
    public static final Block soapBlock = new SimpleFacingBlock(AbstractBlock.Settings.create().hardness(2f).mapColor(MapColor.PINK).sounds(BlockSoundGroup.SLIME));
//	public static final Block stakeBlock = new StakeBlock(AbstractBlock.Settings.create());
    public static final StokedFireBlock stokedFireBlock = new StokedFireBlock(AbstractBlock.Settings.copy(Blocks.SOUL_FIRE));
    public static final Block stoneDetectorRailBlock = new DetectorRailBlock(AbstractBlock.Settings.copy(Blocks.DETECTOR_RAIL));
	public static final Block soulForgeBlock = new SoulForgeBlock(AbstractBlock.Settings.copy(Blocks.ANVIL));
    public static final ArrayList<TableBlock> tableBlocks = new ArrayList<>();
	public static final Block turntableBlock = new TurntableBlock(AbstractBlock.Settings.create()
            .strength(2f)
            .sounds(BlockSoundGroup.STONE)
            .mapColor(Blocks.PISTON_HEAD.getDefaultMapColor())
    );
    public static final UnfiredPotteryBlock unfiredDecoratedPotBlock = new UnfiredDecoratedPotBlock(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
    public static final UnfiredPotteryBlock unfiredDecoratedPotBlockWithSherds = new UnfiredDecoratedPotBlockWithSherds(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredCrucibleBlock = new UnfiredCrucibleBlock(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredPlanterBlock = new UnfiredPlanterBlock(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredVaseBlock = new UnfiredVaseBlock(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredUrnBlock = new UnfiredUrnBlock(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
    public static final UnfiredPotteryBlock unfiredFlowerPotBlock = new UnfiredFlowerPotBlock(AbstractBlock.Settings.copy(Blocks.CLAY)
            .nonOpaque()
            .solidBlock(Blocks::never)
    );
	public static final Block urnBlock = new UrnBlock(AbstractBlock.Settings.copy(Blocks.TERRACOTTA)
            .nonOpaque()
            .solidBlock(Blocks::never)
            .allowsSpawning(Blocks::never)
            .hardness(2f)
    );
	public static final HashMap<DyeColor, VaseBlock> vaseBlocks = new HashMap<>();
	public static final Block wickerPaneBlock = new PaneBlock(AbstractBlock.Settings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .nonOpaque()
    );
    public static final Block wickerBlock = new Block(AbstractBlock.Settings.create().hardness(2f).burnable().mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.GRASS));
    public static final Block wickerSlabBlock = new SlabBlock(AbstractBlock.Settings.copy(wickerBlock));
    public static final HashMap<DyeColor, SlabBlock> woolSlabBlocks = new HashMap<>();
    public static final Block vineTrapBlock = new VineTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)
            .allowsSpawning(Blocks::never)
            .noCollision()
    );
    public static final Block dirtSlabBlock = new DirtSlabBlock(AbstractBlock.Settings.copy(Blocks.DIRT), Blocks.DIRT);
    public static final Block dirtPathSlabBlock = new DirtPathSlabBlock(AbstractBlock.Settings.copy(Blocks.DIRT_PATH), Blocks.DIRT_PATH);
    public static final Block grassSlabBlock = new GrassSlabBlock(AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK), Blocks.GRASS_BLOCK);
    public static final Block myceliumSlabBlock = new MyceliumSlabBlock(AbstractBlock.Settings.copy(Blocks.MYCELIUM), Blocks.MYCELIUM);
    public static final Block podzolSlabBlock = new MyceliumSlabBlock(AbstractBlock.Settings.copy(Blocks.PODZOL), Blocks.PODZOL);

    public static final Block netherGroth = new NetherGrothBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_RED)
                    .solidBlock(Blocks::never)
                    .ticksRandomly()
                    .strength(0.2f)
                    .velocityMultiplier(0.4F)
                    .sounds(BlockSoundGroup.FUNGUS)
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .velocityMultiplier(0.8f)
    );

    public static final Block grothedNetherrackBlock = new GrothedNetherrackBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_RED)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(0.4F)
                    .sounds(BlockSoundGroup.NETHERRACK)
    );

    @Override
    public void onInitialize() {
        // Axle
        Registry.register(Registries.BLOCK, Id.of("axle"), axleBlock);
        Registry.register(Registries.ITEM, Id.of("axle"), new BlockItem(axleBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("axle_power_source"), axlePowerSourceBlock);
        // Gearbox
        Registry.register(Registries.BLOCK, Id.of("gear_box"), gearBoxBlock);
        Registry.register(Registries.ITEM, Id.of("gear_box"), new BlockItem(gearBoxBlock, new Item.Settings()));
        // Redstone Clutch
        Registry.register(Registries.BLOCK, Id.of("redstone_clutch"), redstoneClutchBlock);
        Registry.register(Registries.ITEM, Id.of("redstone_clutch"), new BlockItem(redstoneClutchBlock, new Item.Settings()));
        // Hibachi
        Registry.register(Registries.BLOCK, Id.of("hibachi"), hibachiBlock);
        Registry.register(Registries.ITEM, Id.of("hibachi"), new BlockItem(hibachiBlock, new Item.Settings()));
        // Light Block
        Registry.register(Registries.BLOCK, Id.of("light_block"), lightBlockBlock);
        Registry.register(Registries.ITEM, Id.of("light_block"), new BlockItem(lightBlockBlock, new Item.Settings()));
        // Block Dispenser
        Registry.register(Registries.BLOCK, Id.of("block_dispenser"), blockDispenserBlock);
        Registry.register(Registries.ITEM, Id.of("block_dispenser"), new BlockItem(blockDispenserBlock, new Item.Settings()));
        // Cauldron / Stewing Pot
        Registry.register(Registries.BLOCK, Id.of("cauldron"), cauldronBlock);
        Registry.register(Registries.ITEM, Id.of("cauldron"), new BlockItem(cauldronBlock, new Item.Settings()));
        // Obsidian pressure plate
        Registry.register(Registries.BLOCK, Id.of("obsidian_pressure_plate"), obsidianPressurePlateBlock);
        Registry.register(Registries.ITEM, Id.of("obsidian_pressure_plate"), new BlockItem(obsidianPressurePlateBlock, new Item.Settings()));
        // Hemp crop
        Registry.register(Registries.BLOCK, Id.of("hemp_crop_block"), hempCropBlock);
        // Detector Block
        Registry.register(Registries.BLOCK, Id.of("detector_block"), detectorBlock);
        Registry.register(Registries.ITEM, Id.of("detector_block"), new BlockItem(detectorBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("detector_logic_block"), detectorLogicBlock);
        // Mill Stone
        Registry.register(Registries.BLOCK, Id.of("mill_stone"), millStoneBlock);
        Registry.register(Registries.ITEM, Id.of("mill_stone"), new BlockItem(millStoneBlock, new Item.Settings()));
        // Companion Cube
        Registry.register(Registries.BLOCK, Id.of("companion_cube"), companionCubeBlock);
        Registry.register(Registries.ITEM, Id.of("companion_cube"), new BlockItem(companionCubeBlock, new Item.Settings()));
        // Companion Slab
        Registry.register(Registries.BLOCK, Id.of("companion_slab"), companionSlabBlock);
        Registry.register(Registries.ITEM, Id.of("companion_slab"), new BlockItem(companionSlabBlock, new Item.Settings()));
        // Hand Crank
        Registry.register(Registries.BLOCK, Id.of("hand_crank"), handCrankBlock);
        Registry.register(Registries.ITEM, Id.of("hand_crank"), new BlockItem(handCrankBlock, new Item.Settings()));
        // Anchor
        Registry.register(Registries.BLOCK, Id.of("anchor"), anchorBlock);
        Registry.register(Registries.ITEM, Id.of("anchor"), new BlockItem(anchorBlock, new Item.Settings()));
        // Rope
        Registry.register(Registries.BLOCK, Id.of("rope"), ropeBlock);
        // Stone Detector Rail
        Registry.register(Registries.BLOCK, Id.of("stone_detector_rail"), stoneDetectorRailBlock);
        Registry.register(Registries.ITEM, Id.of("stone_detector_rail"), new BlockItem(stoneDetectorRailBlock, new Item.Settings()));
        // Obsidian Detector Rail
        Registry.register(Registries.BLOCK, Id.of("obsidian_detector_rail"), obsidianDetectorRailBlock);
        Registry.register(Registries.ITEM, Id.of("obsidian_detector_rail"), new BlockItem(obsidianDetectorRailBlock, new Item.Settings()));
        // Bwt Hopper
        Registry.register(Registries.BLOCK, Id.of("hopper"), hopperBlock);
        Registry.register(Registries.ITEM, Id.of("hopper"), new BlockItem(hopperBlock, new Item.Settings()));
        // Grate
        Registry.register(Registries.BLOCK, Id.of("grate"), grateBlock);
        Registry.register(Registries.ITEM, Id.of("grate"), new BlockItem(grateBlock, new Item.Settings()));
        // Slats
        Registry.register(Registries.BLOCK, Id.of("slats"), slatsBlock);
        Registry.register(Registries.ITEM, Id.of("slats"), new BlockItem(slatsBlock, new Item.Settings()));
        // Wicker
        Registry.register(Registries.BLOCK, Id.of("wicker"), wickerPaneBlock);
        Registry.register(Registries.ITEM, Id.of("wicker"), new BlockItem(wickerPaneBlock, new Item.Settings()));
        // Saw
        Registry.register(Registries.BLOCK, Id.of("saw"), sawBlock);
        Registry.register(Registries.ITEM, Id.of("saw"), new BlockItem(sawBlock, new Item.Settings()));
        // Pulley
        Registry.register(Registries.BLOCK, Id.of("pulley"), pulleyBlock);
        Registry.register(Registries.ITEM, Id.of("pulley"), new BlockItem(pulleyBlock, new Item.Settings()));
        // Platform
        Registry.register(Registries.BLOCK, Id.of("platform"), platformBlock);
        Registry.register(Registries.ITEM, Id.of("platform"), new BlockItem(platformBlock, new Item.Settings()));
        // Turntable
        Registry.register(Registries.BLOCK, Id.of("turntable"), turntableBlock);
        Registry.register(Registries.ITEM, Id.of("turntable"), new BlockItem(turntableBlock, new Item.Settings()));
        // Stoked Fire
        Registry.register(Registries.BLOCK, Id.of("stoked_fire"), stokedFireBlock);
        // Bellows
        Registry.register(Registries.BLOCK, Id.of("bellows"), bellowsBlock);
        Registry.register(Registries.ITEM, Id.of("bellows"), new BlockItem(bellowsBlock, new Item.Settings()));
        // Unfired Pottery
        Registry.register(Registries.BLOCK, Id.of("unfired_decorated_pot_with_sherds"), unfiredDecoratedPotBlockWithSherds);
        Registry.register(Registries.ITEM, Id.of("unfired_decorated_pot_with_sherds"), new BlockItem(unfiredDecoratedPotBlockWithSherds, new Item.Settings().component(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT)));
        Registry.register(Registries.BLOCK, Id.of("unfired_decorated_pot"), unfiredDecoratedPotBlock);
        Registry.register(Registries.ITEM, Id.of("unfired_decorated_pot"), new BlockItem(unfiredDecoratedPotBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("unfired_crucible"), unfiredCrucibleBlock);
        Registry.register(Registries.ITEM, Id.of("unfired_crucible"), new BlockItem(unfiredCrucibleBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("unfired_planter"), unfiredPlanterBlock);
        Registry.register(Registries.ITEM, Id.of("unfired_planter"), new BlockItem(unfiredPlanterBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("unfired_vase"), unfiredVaseBlock);
        Registry.register(Registries.ITEM, Id.of("unfired_vase"), new BlockItem(unfiredVaseBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("unfired_urn"), unfiredUrnBlock);
        Registry.register(Registries.ITEM, Id.of("unfired_urn"), new BlockItem(unfiredUrnBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("unfired_flower_pot"), unfiredFlowerPotBlock);
        Registry.register(Registries.ITEM, Id.of("unfired_flower_pot"), new BlockItem(unfiredFlowerPotBlock, new Item.Settings()));
        // Kiln
        Registry.register(Registries.BLOCK, Id.of("kiln"), kilnBlock);
        // Blood Wood
        bloodWoodBlocks.register();
        // Mini blocks
        MaterialInheritedBlock.registerMaterialBlocks(
                sidingBlocks, mouldingBlocks, cornerBlocks,
                columnBlocks, pedestalBlocks, tableBlocks
        );
        // Crucible
        Registry.register(Registries.BLOCK, Id.of("crucible"), crucibleBlock);
        Registry.register(Registries.ITEM, Id.of("crucible"), new BlockItem(crucibleBlock, new Item.Settings()));
        // Planters
        Registry.register(Registries.BLOCK, Id.of("planter"), planterBlock);
        Registry.register(Registries.ITEM, Id.of("planter"), new BlockItem(planterBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("soil_planter"), soilPlanterBlock);
        Registry.register(Registries.ITEM, Id.of("soil_planter"), new BlockItem(soilPlanterBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("soul_sand_planter"), soulSandPlanterBlock);
        Registry.register(Registries.ITEM, Id.of("soul_sand_planter"), new BlockItem(soulSandPlanterBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("grass_planter"), grassPlanterBlock);
        Registry.register(Registries.ITEM, Id.of("grass_planter"), new BlockItem(grassPlanterBlock, new Item.Settings()));
        // Vases
        VaseBlock.registerColors(vaseBlocks);
        // Urn
        Registry.register(Registries.BLOCK, Id.of("urn"), urnBlock);
        Registry.register(Registries.ITEM, Id.of("urn"), new BlockItem(urnBlock, new Item.Settings()));
        // SoulForge
        Registry.register(Registries.BLOCK, Id.of("soul_forge"), soulForgeBlock);
        Registry.register(Registries.ITEM, Id.of("soul_forge"), new BlockItem(soulForgeBlock, new Item.Settings()));
        // Wool slabs
        DyeUtils.WOOL_COLORS.forEach((dyeColor, woolBlock) -> {
            SlabBlock woolSlabBlock = new SlabBlock(AbstractBlock.Settings.copy(woolBlock));
            woolSlabBlocks.put(dyeColor, woolSlabBlock);
            Registry.register(Registries.BLOCK, Id.of(dyeColor.getName() + "_wool_slab"), woolSlabBlock);
            Registry.register(Registries.ITEM, Id.of(dyeColor.getName() + "_wool_slab"), new BlockItem(woolSlabBlock, new Item.Settings()));
        });
        // Buddy Block
        Registry.register(Registries.BLOCK, Id.of("buddy_block"), buddyBlock);
        Registry.register(Registries.ITEM, Id.of("buddy_block"), new BlockItem(buddyBlock, new Item.Settings()));
        // Aesthetic compacting blocks
        Registry.register(Registries.BLOCK, Id.of("soap_block"), soapBlock);
        Registry.register(Registries.ITEM, Id.of("soap_block"), new BlockItem(soapBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("wicker_block"), wickerBlock);
        Registry.register(Registries.ITEM, Id.of("wicker_block"), new BlockItem(wickerBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("dung_block"), dungBlock);
        Registry.register(Registries.ITEM, Id.of("dung_block"), new BlockItem(dungBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("padding_block"), paddingBlock);
        Registry.register(Registries.ITEM, Id.of("padding_block"), new BlockItem(paddingBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("rope_coil_block"), ropeCoilBlock);
        Registry.register(Registries.ITEM, Id.of("rope_coil_block"), new BlockItem(ropeCoilBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("concentrated_hellfire_block"), concentratedHellfireBlock);
        Registry.register(Registries.ITEM, Id.of("concentrated_hellfire_block"), new BlockItem(concentratedHellfireBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("wicker_slab"), wickerSlabBlock);
        Registry.register(Registries.ITEM, Id.of("wicker_slab"), new BlockItem(wickerSlabBlock, new Item.Settings()));
        // Mining charge
        Registry.register(Registries.BLOCK, Id.of("mining_charge"), miningChargeBlock);
        Registry.register(Registries.ITEM, Id.of("mining_charge"), new BlockItem(miningChargeBlock, new Item.Settings()));
        // Vine trap
        Registry.register(Registries.BLOCK, Id.of("vine_trap"), vineTrapBlock);
        Registry.register(Registries.ITEM, Id.of("vine_trap"), new BlockItem(vineTrapBlock, new Item.Settings()));
        // Lens
        Registry.register(Registries.BLOCK, Id.of("lens"), lensBlock);
        Registry.register(Registries.ITEM, Id.of("lens"), new BlockItem(lensBlock, new Item.Settings()));
        Registry.register(Registries.BLOCK, Id.of("lens_beam"), lensBeamBlock);
        Registry.register(Registries.BLOCK, Id.of("lens_beam_glass"), lensBeamGlassBlock);
        // Dirt Slab
        Registry.register(Registries.BLOCK, Id.of("dirt_slab"), dirtSlabBlock);
        Registry.register(Registries.ITEM, Id.of("dirt_slab"), new BlockItem(dirtSlabBlock, new Item.Settings()));
        // Dirt Path Slab
        Registry.register(Registries.BLOCK, Id.of("dirt_path_slab"), dirtPathSlabBlock);
        Registry.register(Registries.ITEM, Id.of("dirt_path_slab"), new BlockItem(dirtPathSlabBlock, new Item.Settings()));
        // Grass Slab
        Registry.register(Registries.BLOCK, Id.of("grass_slab"), grassSlabBlock);
        Registry.register(Registries.ITEM, Id.of("grass_slab"), new BlockItem(grassSlabBlock, new Item.Settings()));
        // Mycelium Slab
        Registry.register(Registries.BLOCK, Id.of("mycelium_slab"), myceliumSlabBlock);
        Registry.register(Registries.ITEM, Id.of("mycelium_slab"), new BlockItem(myceliumSlabBlock, new Item.Settings()));
        // Podzol Slab
        Registry.register(Registries.BLOCK, Id.of("podzol_slab"), podzolSlabBlock);
        Registry.register(Registries.ITEM, Id.of("podzol_slab"), new BlockItem(podzolSlabBlock, new Item.Settings()));
        // Nether Groth
        Registry.register(Registries.BLOCK, Id.of("nether_groth"), netherGroth);
        Registry.register(Registries.ITEM, Id.of("nether_groth"), new BlockItem(netherGroth, new Item.Settings()));
        // Grothed Netherrack
        Registry.register(Registries.BLOCK, Id.of("grothed_netherrack"), grothedNetherrackBlock);
        // Aqueduct
        Registry.register(Registries.BLOCK, Id.of("aqueduct"), aqueductBlock);
        Registry.register(Registries.ITEM, Id.of("aqueduct"), new BlockItem(aqueductBlock, new Item.Settings()));
        // Screw pump
        Registry.register(Registries.BLOCK, Id.of("screw_pump"), screwPumpBlock);
        Registry.register(Registries.ITEM, Id.of("screw_pump"), new BlockItem(screwPumpBlock, new Item.Settings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
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

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(content -> {
            content.addAll(DyeUtils.streamColorItemsSorted(vaseBlocks).map(vaseBlock -> vaseBlock.asItem().getDefaultStack()).toList());
            content.addAll(DyeUtils.streamColorItemsSorted(woolSlabBlocks).map(woolSlabBlock -> woolSlabBlock.asItem().getDefaultStack()).toList());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(axleBlock);
            content.add(gearBoxBlock);
            content.add(redstoneClutchBlock);
            content.add(hibachiBlock);
            content.add(lightBlockBlock);
            content.add(blockDispenserBlock);
            content.add(obsidianPressurePlateBlock);
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
            content.add(lensBlock);
            content.add(BwtBlocks.aqueductBlock);
            content.add(BwtBlocks.screwPumpBlock);
            content.addAfter(Items.TNT, miningChargeBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(cauldronBlock);
            content.add(crucibleBlock);
            content.add(planterBlock);
            content.add(soilPlanterBlock);
            content.add(soulSandPlanterBlock);
            content.add(grassPlanterBlock);
            content.add(urnBlock);
            content.add(unfiredDecoratedPotBlock);
            content.add(unfiredCrucibleBlock);
            content.add(unfiredPlanterBlock);
            content.add(unfiredVaseBlock);
            content.add(unfiredUrnBlock);
            content.add(unfiredFlowerPotBlock);
            content.addAfter(Items.CRAFTING_TABLE, soulForgeBlock);
            content.addAfter(Items.SCAFFOLDING, BwtBlocks.vineTrapBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
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
            content.add(dirtSlabBlock);
            content.add(dirtPathSlabBlock);
            content.add(grassSlabBlock);
            content.add(myceliumSlabBlock);
            content.add(podzolSlabBlock);
        });

        FlattenableBlockRegistry.register(BwtBlocks.grassSlabBlock, BwtBlocks.dirtPathSlabBlock.getDefaultState());
        FlattenableBlockRegistry.register(BwtBlocks.dirtSlabBlock, BwtBlocks.dirtPathSlabBlock.getDefaultState());
        FlattenableBlockRegistry.register(BwtBlocks.myceliumSlabBlock, BwtBlocks.dirtPathSlabBlock.getDefaultState());
        FlattenableBlockRegistry.register(BwtBlocks.podzolSlabBlock, BwtBlocks.dirtPathSlabBlock.getDefaultState());
    }
}

package com.bwt.blocks;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.blocks.cauldron.CauldronBlock;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.blocks.detector.DetectorLogicBlock;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mill_stone.MillStoneBlock;
import com.bwt.blocks.pulley.PulleyBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class BwtBlocks implements ModInitializer {

	public static final Block anchorBlock = new AnchorBlock(FabricBlockSettings.create()
        .hardness(2f)
        .sounds(BlockSoundGroup.STONE)
        .nonOpaque()
        .solid()
        .requiresTool()
    );
//	public static final Block anvilBlock = new AnvilBlock(FabricBlockSettings.create());
	public static final Block axleBlock = new AxleBlock(FabricBlockSettings.create()
        .hardness(2F)
        .sounds(BlockSoundGroup.WOOD)
        .burnable()
        .solid()
        .nonOpaque()
    );
    public static final Block axlePowerSourceBlock = new AxlePowerSourceBlock(FabricBlockSettings.copyOf(axleBlock.getSettings()));
//	public static final Block barrelBlock = new BarrelBlock(FabricBlockSettings.create());
//	public static final Block bellowsBlock = new BellowsBlock(FabricBlockSettings.create());
	public static final BlockDispenserBlock blockDispenserBlock = new BlockDispenserBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)
        .hardness(3.5f)
    );
//	public static final Block bloodWoodBlock = new BloodWoodBlock(FabricBlockSettings.create());
//	Blood Wood Sapling
//	public static final Block buddyBlockBlock = new BuddyBlockBlock(FabricBlockSettings.create());
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
//	public static final Block columnBlock = new ColumnBlock(FabricBlockSettings.create());
	public static final Block companionCubeBlock = new CompanionCubeBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL));
	public static final Block companionSlabBlock = new CompanionSlabBlock(FabricBlockSettings.create());
	public static final ArrayList<CornerBlock> cornerBlocks = new ArrayList<>();
//	public static final Block crucibleBlock = new CrucibleBlock(FabricBlockSettings.create());
	public static final Block detectorBlock = new DetectorBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER).hardness(3.5f));
	public static final Block detectorLogicBlock = new DetectorLogicBlock(FabricBlockSettings.copyOf(Blocks.AIR));
    public static final Block gearBoxBlock = new GearBoxBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
        .hardness(2F)
    );
	public static final Block grateBlock = new PaneBlock(FabricBlockSettings.create().hardness(0.5f).sounds(BlockSoundGroup.WOOD).nonOpaque());
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
//	public static final Block kilnBlock = new KilnBlock(FabricBlockSettings.create());
//	public static final Block lensBlock = new LensBlock(FabricBlockSettings.create());
	public static final Block lightBlockBlock = new LightBlock(FabricBlockSettings.copyOf(Blocks.GLASS)
        .strength(0.4f)
        .luminance(Blocks.createLightLevelFromLitBlockState(15))
    );
	public static final Block millStoneBlock = new MillStoneBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER).hardness(3.5f));
//	public static final Block miningChargeBlock = new MiningChargeBlock(FabricBlockSettings.create());
	public static final ArrayList<MouldingBlock> mouldingBlocks = new ArrayList<>();
//	public static final Block netherGrothBlock = new NetherGrothBlock(FabricBlockSettings.create());
//	public static final Block obsidianDetectorRailBlock = new ObsidianDetectorRailBlock(FabricBlockSettings.create());
	public static final Block obsidianPressuePlateBlock = new ObsidianPressurePlateBlock(FabricBlockSettings.copyOf(Blocks.STONE_PRESSURE_PLATE).strength(50.0f, 1200.0f)
    );
	public static final Block obsidianDetectorRailBlock = new DetectorRailBlock(FabricBlockSettings.copyOf(Blocks.DETECTOR_RAIL)
            .strength(50.0f, 1200.0f)
    );
//	public static final Block pedestalBlock = new PedestalBlock(FabricBlockSettings.create());
//	public static final Block planterBlock = new PlanterBlock(FabricBlockSettings.create());
	public static final Block platformBlock = new PlatformBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
        .nonOpaque()
        .allowsSpawning(Blocks::never)
        .solidBlock(Blocks::never)
        .suffocates(Blocks::never)
        .blockVision(Blocks::never)
    );
	public static final Block pulleyBlock = new PulleyBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(2f).pistonBehavior(PistonBehavior.IGNORE));
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
	public static final Block slatsBlock = new PaneBlock(FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.WOOD).nonOpaque());
//	public static final Block stakeBlock = new StakeBlock(FabricBlockSettings.create());
    public static final Block stoneDetectorRailBlock = new DetectorRailBlock(FabricBlockSettings.copyOf(Blocks.DETECTOR_RAIL));
//	public static final Block tableBlock = new TableBlock(FabricBlockSettings.create());
//	public static final Block turntableBlock = new TurntableBlock(FabricBlockSettings.create());
//	public static final Block unfiredPotteryBlock = new UnfiredPotteryBlock(FabricBlockSettings.create());
//	public static final Block urnBlock = new UrnBlock(FabricBlockSettings.create());
//	public static final Block vaseBlock = new VaseBlock(FabricBlockSettings.create());
//	public static final Block waterWheelBlock = new WaterWheelBlock(FabricBlockSettings.create());
	public static final Block wickerBlock = new PaneBlock(FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.GRASS).nonOpaque());
//	public static final Block woolSlabBlock = new WoolSlabBlock(FabricBlockSettings.create());


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
        Registry.register(Registries.BLOCK, new Identifier("bwt", "wicker"), wickerBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "wicker"), new BlockItem(wickerBlock, new FabricItemSettings()));
        // Saw
        Registry.register(Registries.BLOCK, new Identifier("bwt", "saw"), sawBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "saw"), new BlockItem(sawBlock, new FabricItemSettings()));
        // Pulley
        Registry.register(Registries.BLOCK, new Identifier("bwt", "pulley"), pulleyBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "pulley"), new BlockItem(pulleyBlock, new FabricItemSettings()));
        // Platform
        Registry.register(Registries.BLOCK, new Identifier("bwt", "platform"), platformBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "platform"), new BlockItem(platformBlock, new FabricItemSettings()));
        // Mini blocks
        MiniBlock.registerMiniBlocks(sidingBlocks, mouldingBlocks, cornerBlocks);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(axleBlock);
            content.add(gearBoxBlock);
            content.add(hibachiBlock);
            content.add(lightBlockBlock);
            content.add(blockDispenserBlock);
            content.add(obsidianPressuePlateBlock);
            content.add(detectorBlock);
            content.add(millStoneBlock);
            content.add(handCrankBlock);
            content.add(stoneDetectorRailBlock);
            content.add(obsidianDetectorRailBlock);
            content.add(sawBlock);
            content.add(pulleyBlock);
            content.add(platformBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(cauldronBlock);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.add(companionCubeBlock);
            content.add(grateBlock);
            content.add(slatsBlock);
            content.add(wickerBlock);
            content.add(platformBlock);
        });
    }
}

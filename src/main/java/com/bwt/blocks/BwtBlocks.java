package com.bwt.blocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BwtBlocks implements ModInitializer {

//	public static final Block anchorBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "anchor"), new AnchorBlock(FabricBlockSettings.create()));
//	public static final Block anvilBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "anvil"), new AnvilBlock(FabricBlockSettings.create()));
	public static final Block axleBlock = new AxleBlock(
            FabricBlockSettings.create()
                    .hardness(2F)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable()
                    .solid()
                    .nonOpaque()
    );
//	public static final Block barrelBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "barrel"), new BarrelBlock(FabricBlockSettings.create()));
//	public static final Block bellowsBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "bellows"), new BellowsBlock(FabricBlockSettings.create()));
//	public static final Block blockDispenserBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "block_dispenser"), new BlockDispenserBlock(FabricBlockSettings.create()));
//	public static final Block bloodWoodBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "blood_wood"), new BloodWoodBlock(FabricBlockSettings.create()));
//	Blood Wood Sapling
//	public static final Block buddyBlockBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "buddy_block"), new BuddyBlockBlock(FabricBlockSettings.create()));
//	public static final Block canvasBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "canvas"), new CanvasBlock(FabricBlockSettings.create()));
//	public static final Block columnBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "column"), new ColumnBlock(FabricBlockSettings.create()));
//	public static final Block companionCubeBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "companion_cube"), new CompanionCubeBlock(FabricBlockSettings.create()));
//	public static final Block companionSlabBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "companion_slab"), new CompanionSlabBlock(FabricBlockSettings.create()));
//	public static final Block cornerBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "corner"), new CornerBlock(FabricBlockSettings.create()));
//	public static final Block crucibleBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "crucible"), new CrucibleBlock(FabricBlockSettings.create()));
//	public static final Block detectorBlockBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "detector_block"), new DetectorBlockBlock(FabricBlockSettings.create()));
    public static final Block gearBoxBlock = new GearBoxBlock(
            FabricBlockSettings.create()
                    .hardness(2F)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable()
                    .solid()
    );
//	public static final Block grateBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "grate"), new GrateBlock(FabricBlockSettings.create()));
//	public static final Block handCrankBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "hand_crank"), new HandCrankBlock(FabricBlockSettings.create()));
//	public static final Block hempBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "hemp"), new HempBlock(FabricBlockSettings.create()));
//	public static final Block hibachiBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "hibachi"), new HibachiBlock(FabricBlockSettings.create()));
//	public static final Block hopperBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "hopper"), new HopperBlock(FabricBlockSettings.create()));
//	public static final Block infernalEnchanterBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "infernal_enchanter"), new InfernalEnchanterBlock(FabricBlockSettings.create()));
//	public static final Block kilnBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "kiln"), new KilnBlock(FabricBlockSettings.create()));
//	public static final Block lensBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "lens"), new LensBlock(FabricBlockSettings.create()));
//	public static final Block lightBlockBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "light_block"), new LightBlockBlock(FabricBlockSettings.create()));
//	public static final Block millStoneBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "mill_stone"), new MillStoneBlock(FabricBlockSettings.create()));
//	public static final Block miningChargeBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "mining_charge"), new MiningChargeBlock(FabricBlockSettings.create()));
//	public static final Block mouldingBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "moulding"), new MouldingBlock(FabricBlockSettings.create()));
//	public static final Block netherGrothBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "nether_groth"), new NetherGrothBlock(FabricBlockSettings.create()));
//	public static final Block obsidianDetectorRailBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "obsidian_detector_rail"), new ObsidianDetectorRailBlock(FabricBlockSettings.create()));
//	public static final Block obsidianPressuePlateBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "obsidian_pressure_plate"), new ObsidianPressurePlateBlock(FabricBlockSettings.create()));
//	public static final Block pedestalBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "pedestal"), new PedestalBlock(FabricBlockSettings.create()));
//	public static final Block planterBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "planter"), new PlanterBlock(FabricBlockSettings.create()));
//	public static final Block platformBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "platform"), new PlatformBlock(FabricBlockSettings.create()));
//	public static final Block pulleyBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "pulley"), new PulleyBlock(FabricBlockSettings.create()));
//	public static final Block ropeBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "rope"), new RopeBlock(FabricBlockSettings.create()));
//	public static final Block sawBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "saw"), new SawBlock(FabricBlockSettings.create()));
//	public static final Block screwPumpBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "screw_pump"), new ScrewPumpBlock(FabricBlockSettings.create()));
//	public static final Block slatsBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "slats"), new SlatsBlock(FabricBlockSettings.create()));
//	public static final Block stakeBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "stake"), new StakeBlock(FabricBlockSettings.create()));
//	public static final Block stewingPotBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "stewing_pot"), new StewingPotBlock(FabricBlockSettings.create()));
//	public static final Block tableBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "table"), new TableBlock(FabricBlockSettings.create()));
//	public static final Block turntableBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "turntable"), new TurntableBlock(FabricBlockSettings.create()));
//	public static final Block unfiredPotteryBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "unfired_pottery"), new UnfiredPotteryBlock(FabricBlockSettings.create()));
//	public static final Block urnBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "urn"), new UrnBlock(FabricBlockSettings.create()));
//	public static final Block vaseBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "vase"), new VaseBlock(FabricBlockSettings.create()));
//	public static final Block waterWheelBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "water_wheel"), new WaterWheelBlock(FabricBlockSettings.create()));
//	public static final Block wickerBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "wicker"), new WickerBlock(FabricBlockSettings.create()));
	public static final Block axlePowerSourceBlock = new AxlePowerSourceBlock(FabricBlockSettings.copyOf(axleBlock.getSettings()));
//	public static final Block windmillBlock = new AxlePowerSourceBlock(FabricBlockSettings.copyOf(axlePowerSourceBlock.getSettings()));
//	public static final Block woodenDetectorRailBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "wooden_detector_rail"), new WoodenDetectorRailBlock(FabricBlockSettings.create()));
//	public static final Block woodenSidingBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "wooden_siding"), new WoodenSidingBlock(FabricBlockSettings.create()));
//	public static final Block woolSlabBlock = Registry.register(Registries.BLOCK, new Identifier("bwt", "wool_slab"), new WoolSlabBlock(FabricBlockSettings.create()));





    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, new Identifier("bwt", "axle"), axleBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "axle"), new BlockItem(axleBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "gear_box"), gearBoxBlock);
        Registry.register(Registries.ITEM, new Identifier("bwt", "gear_box"), new BlockItem(gearBoxBlock, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("bwt", "axle_power_source"), axlePowerSourceBlock);
//        Registry.register(Registries.BLOCK, new Identifier("bwt", "windmill"), windmillBlock);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.add(gearBoxBlock));
    }
}

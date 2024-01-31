package com.bwt.blocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BwtBlocks implements ModInitializer {

//	public static final Block anchorBlock = new AnchorBlock(FabricBlockSettings.create());
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
//	public static final Block blockDispenserBlock = new BlockDispenserBlock(FabricBlockSettings.create());
//	public static final Block bloodWoodBlock = new BloodWoodBlock(FabricBlockSettings.create());
//	Blood Wood Sapling
//	public static final Block buddyBlockBlock = new BuddyBlockBlock(FabricBlockSettings.create());
//	public static final Block canvasBlock = new CanvasBlock(FabricBlockSettings.create());
//	public static final Block columnBlock = new ColumnBlock(FabricBlockSettings.create());
//	public static final Block companionCubeBlock = new CompanionCubeBlock(FabricBlockSettings.create());
//	public static final Block companionSlabBlock = new CompanionSlabBlock(FabricBlockSettings.create());
//	public static final Block cornerBlock = new CornerBlock(FabricBlockSettings.create());
//	public static final Block crucibleBlock = new CrucibleBlock(FabricBlockSettings.create());
//	public static final Block detectorBlockBlock = new DetectorBlockBlock(FabricBlockSettings.create());
    public static final Block gearBoxBlock = new GearBoxBlock(FabricBlockSettings.create()
        .hardness(2F)
        .sounds(BlockSoundGroup.WOOD)
        .burnable()
        .solid()
    );
//	public static final Block grateBlock = new GrateBlock(FabricBlockSettings.create());
//	public static final Block handCrankBlock = new HandCrankBlock(FabricBlockSettings.create());
//	public static final Block hempBlock = new HempBlock(FabricBlockSettings.create());
	public static final Block hibachiBlock = new HibachiBlock(FabricBlockSettings.create()
        .hardness(3.5f)
        .sounds(BlockSoundGroup.STONE)
        .solidBlock(Blocks::never)
    );
//	public static final Block hopperBlock = new HopperBlock(FabricBlockSettings.create());
//	public static final Block infernalEnchanterBlock = new InfernalEnchanterBlock(FabricBlockSettings.create());
//	public static final Block kilnBlock = new KilnBlock(FabricBlockSettings.create());
//	public static final Block lensBlock = new LensBlock(FabricBlockSettings.create());
//	public static final Block lightBlockBlock = new LightBlockBlock(FabricBlockSettings.create());
//	public static final Block millStoneBlock = new MillStoneBlock(FabricBlockSettings.create());
//	public static final Block miningChargeBlock = new MiningChargeBlock(FabricBlockSettings.create());
//	public static final Block mouldingBlock = new MouldingBlock(FabricBlockSettings.create());
//	public static final Block netherGrothBlock = new NetherGrothBlock(FabricBlockSettings.create());
//	public static final Block obsidianDetectorRailBlock = new ObsidianDetectorRailBlock(FabricBlockSettings.create());
//	public static final Block obsidianPressuePlateBlock = new ObsidianPressurePlateBlock(FabricBlockSettings.create());
//	public static final Block pedestalBlock = new PedestalBlock(FabricBlockSettings.create());
//	public static final Block planterBlock = new PlanterBlock(FabricBlockSettings.create());
//	public static final Block platformBlock = new PlatformBlock(FabricBlockSettings.create());
//	public static final Block pulleyBlock = new PulleyBlock(FabricBlockSettings.create());
//	public static final Block ropeBlock = new RopeBlock(FabricBlockSettings.create());
//	public static final Block sawBlock = new SawBlock(FabricBlockSettings.create());
//	public static final Block screwPumpBlock = new ScrewPumpBlock(FabricBlockSettings.create());
//	public static final Block slatsBlock = new SlatsBlock(FabricBlockSettings.create());
//	public static final Block stakeBlock = new StakeBlock(FabricBlockSettings.create());
//	public static final Block stewingPotBlock = new StewingPotBlock(FabricBlockSettings.create());
//	public static final Block tableBlock = new TableBlock(FabricBlockSettings.create());
//	public static final Block turntableBlock = new TurntableBlock(FabricBlockSettings.create());
//	public static final Block unfiredPotteryBlock = new UnfiredPotteryBlock(FabricBlockSettings.create());
//	public static final Block urnBlock = new UrnBlock(FabricBlockSettings.create());
//	public static final Block vaseBlock = new VaseBlock(FabricBlockSettings.create());
//	public static final Block waterWheelBlock = new WaterWheelBlock(FabricBlockSettings.create());
//	public static final Block wickerBlock = new WickerBlock(FabricBlockSettings.create());

//	public static final Block windmillBlock = new AxlePowerSourceBlock(FabricBlockSettings.copyOf(axlePowerSourceBlock.getSettings()));
//	public static final Block woodenDetectorRailBlock = new WoodenDetectorRailBlock(FabricBlockSettings.create());
//	public static final Block woodenSidingBlock = new WoodenSidingBlock(FabricBlockSettings.create());
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
        
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.add(gearBoxBlock);
            content.add(axleBlock);
            content.add(hibachiBlock);
        });


    }
}

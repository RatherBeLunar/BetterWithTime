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
import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
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

public class BwtBlocks implements ModInitializer {

    public static final AqueductBlock aqueductBlock = new AqueductBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS));
	public static final Block anchorBlock = new AnchorBlock(BlockBehaviour.Properties.of()
            .destroyTime(2f)
            .sound(SoundType.STONE)
            .noOcclusion()
            .forceSolidOn()
            .requiresCorrectToolForDrops()
    );
	public static final Block axleBlock = new AxleBlock(BlockBehaviour.Properties.of()
            .destroyTime(2F)
            .sound(SoundType.WOOD)
            .ignitedByLava()
            .forceSolidOn()
            .noOcclusion()
    );
    public static final Block axlePowerSourceBlock = new AxlePowerSourceBlock(BlockBehaviour.Properties.ofFullCopy(axleBlock));
	public static final Block bellowsBlock = new BellowsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
	public static final BlockDispenserBlock blockDispenserBlock = new BlockDispenserBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)
            .destroyTime(3.5f)
    );
    public static final BloodWoodBlocks bloodWoodBlocks = new BloodWoodBlocks().initialize();

	public static final Block buddyBlock = new BuddyBlock(BlockBehaviour.Properties.of()
            .destroyTime(3.5f)
            .sound(SoundType.STONE)
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .requiresCorrectToolForDrops()
    );
	public static final Block cauldronBlock = new CauldronBlock(BlockBehaviour.Properties.of()
            .isRedstoneConductor(Blocks::never)
            .noOcclusion()
            .destroyTime(3.5f)
            .explosionResistance(10f)
            .sound(SoundType.METAL)
            .mapColor(MapColor.COLOR_BLACK)
            .requiresCorrectToolForDrops()
    );
    public static final ArrayList<ColumnBlock> columnBlocks = new ArrayList<>();
	public static final Block concentratedHellfireBlock = new Block(BlockBehaviour.Properties.of().destroyTime(2f).requiresCorrectToolForDrops().mapColor(MapColor.FIRE).sound(SoundType.METAL));
	public static final Block companionCubeBlock = new CompanionCubeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
            .destroyTime(0.4f)
    );
	public static final Block companionSlabBlock = new CompanionSlabBlock(BlockBehaviour.Properties.ofFullCopy(companionCubeBlock));
	public static final ArrayList<CornerBlock> cornerBlocks = new ArrayList<>();
    public static final CreativePowerSourceBlock creativePowerSouceBlock = new CreativePowerSourceBlock(BlockBehaviour.Properties.of()
            .destroyTime(2F)
            .sound(SoundType.WOOD)
            .forceSolidOn()
            .noOcclusion()
    );
	public static final Block crucibleBlock = new CrucibleBlock(BlockBehaviour.Properties.of()
            .isRedstoneConductor(Blocks::never)
            .noOcclusion()
            .destroyTime(0.6f)
            .explosionResistance(3f)
            .sound(SoundType.GLASS)
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
    );
	public static final Block detectorBlock = new DetectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)
            .destroyTime(3.5f)
    );
	public static final Block detectorLogicBlock = new DetectorLogicBlock(BlockBehaviour.Properties.of()
            .replaceable()
            .noCollission()
            .noLootTable()
            .pushReaction(PushReaction.DESTROY)
            .air()
    );
    public static final Block dungBlock = new Block(BlockBehaviour.Properties.of().destroyTime(2f).mapColor(MapColor.COLOR_BROWN).sound(SoundType.HONEY_BLOCK));
    public static final Block gearBoxBlock = new GearBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .destroyTime(2F)
    );
	public static final Block grateBlock = new IronBarsBlock(BlockBehaviour.Properties.of()
            .destroyTime(0.5f)
            .sound(SoundType.WOOD)
            .noOcclusion()
    );
	public static final Block handCrankBlock = new HandCrankBlock(BlockBehaviour.Properties.of()
            .destroyTime(0.5f)
            .sound(SoundType.WOOD)
            .forceSolidOn()
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isSuffocating(Blocks::never)
            .isViewBlocking(Blocks::never)
            .requiresCorrectToolForDrops()
    );
	public static final Block hempCropBlock = new HempCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SUGAR_CANE));
	public static final Block hibachiBlock = new HibachiBlock(BlockBehaviour.Properties.of()
            .destroyTime(3.5f)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
    );
	public static final Block hopperBlock = new MechHopperBlock(BlockBehaviour.Properties.of()
            .destroyTime(2f)
            .sound(SoundType.WOOD)
            .forceSolidOn()
            .noOcclusion()
    );
//	public static final Block infernalEnchanterBlock = new InfernalEnchanterBlock(AbstractBlock.Settings.create());
	public static final Block kilnBlock = new KilnBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS));
	public static final LensBlock lensBlock = new LensBlock(BlockBehaviour.Properties.of()
            .destroyTime(3.5f)
            .sound(SoundType.METAL)
            .forceSolidOn()
            .pushReaction(PushReaction.BLOCK)
    );
    public static final LensBeamBlock lensBeamBlock = new LensBeamBlock(BlockBehaviour.Properties.of()
            .replaceable()
            .noCollission()
            .noLootTable()
            .pushReaction(PushReaction.DESTROY)
            .lightLevel(state -> state.getValue(LensBeamBlock.TERMINUS) ? 14 : 0)
            .emissiveRendering(((state, level, pos) -> state.getValue(LensBeamBlock.TERMINUS)))
    );
    public static final LensBeamGlassBlock lensBeamGlassBlock = new LensBeamGlassBlock(
            Blocks.GLASS,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.GLASS)
                    .lightLevel(state -> state.getValue(LensBeamBlock.TERMINUS) ? 14 : 0)
                    .emissiveRendering(((state, level, pos) -> state.getValue(LensBeamBlock.TERMINUS)))
    );
	public static final Block lightBlockBlock = new LightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
            .strength(0.4f)
            .lightLevel(Blocks.litBlockEmission(15))
    );
	public static final Block millStoneBlock = new MillStoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER)
            .destroyTime(3.5f)
    );
	public static final MiningChargeBlock miningChargeBlock = new MiningChargeBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BROWN)
            .instabreak()
            .sound(SoundType.GRASS)
            .ignitedByLava()
            .isRedstoneConductor(Blocks::never)
    );
	public static final ArrayList<MouldingBlock> mouldingBlocks = new ArrayList<>();
	public static final Block obsidianPressurePlateBlock = new ObsidianPressurePlateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)
            .strength(50.0f, 1200.0f)
    );
	public static final Block obsidianDetectorRailBlock = new DetectorRailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DETECTOR_RAIL)
            .strength(25.0f, 1200.0f)
    );
    public static final ArrayList<PedestalBlock> pedestalBlocks = new ArrayList<>();
	public static final Block planterBlock = new PlanterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)
            .noOcclusion()
            .destroyTime(0.6f)
    );
	public static final Block soilPlanterBlock = new SoilPlanterBlock(BlockBehaviour.Properties.ofFullCopy(planterBlock));
	public static final Block soulSandPlanterBlock = new SoulSandPlanterBlock(BlockBehaviour.Properties.ofFullCopy(planterBlock));
	public static final Block grassPlanterBlock = new GrassPlanterBlock(BlockBehaviour.Properties.ofFullCopy(planterBlock));
    public static final Block paddingBlock = new PaddingBlock(BlockBehaviour.Properties.of().destroyTime(2f).mapColor(MapColor.QUARTZ).sound(SoundType.WOOL));
	public static final Block platformBlock = new PlatformBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor(Blocks::never)
            .isSuffocating(Blocks::never)
            .isViewBlocking(Blocks::never)
            .destroyTime(2f)
            .ignitedByLava()
    );
	public static final Block pulleyBlock = new PulleyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .destroyTime(2f)
            .mapColor(MapColor.TERRACOTTA_BROWN)
            .pushReaction(PushReaction.IGNORE)
    );
    public static final Block redstoneClutchBlock = new RedstoneClutchBlock(BlockBehaviour.Properties.ofFullCopy(gearBoxBlock));
    public static final Block ropeCoilBlock = new Block(BlockBehaviour.Properties.of().destroyTime(1f).mapColor(MapColor.COLOR_BROWN).sound(SoundType.GRASS));
	public static final RopeBlock ropeBlock = new RopeBlock(BlockBehaviour.Properties.of()
            .destroyTime(0.5f)
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY)
    );
	public static final Block sawBlock = new SawBlock(BlockBehaviour.Properties.of()
            .destroyTime(2f)
            .ignitedByLava()
            .sound(SoundType.WOOD)
            .noOcclusion()
    );
	public static final ScrewPumpBlock screwPumpBlock = new ScrewPumpBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .destroyTime(2f)
            .explosionResistance(5f)
    );
    public static final ArrayList<SidingBlock> sidingBlocks = new ArrayList<>();
	public static final Block slatsBlock = new IronBarsBlock(BlockBehaviour.Properties.of()
            .strength(0.5f)
            .sound(SoundType.WOOD)
            .ignitedByLava()
            .noOcclusion()
    );
    public static final Block soapBlock = new SimpleFacingBlock(BlockBehaviour.Properties.of().destroyTime(2f).mapColor(MapColor.COLOR_PINK).sound(SoundType.SLIME_BLOCK));
//	public static final Block stakeBlock = new StakeBlock(AbstractBlock.Settings.create());
    public static final StokedFireBlock stokedFireBlock = new StokedFireBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.FIRE)
            .replaceable()
            .noCollission()
            .instabreak()
            .lightLevel(state -> 15)
            .sound(SoundType.WOOL)
            .pushReaction(PushReaction.DESTROY)
    );
    public static final Block stoneDetectorRailBlock = new DetectorRailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DETECTOR_RAIL));
	public static final Block soulForgeBlock = new SoulForgeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL));
    public static final ArrayList<TableBlock> tableBlocks = new ArrayList<>();
	public static final Block turntableBlock = new TurntableBlock(BlockBehaviour.Properties.of()
            .strength(2f)
            .sound(SoundType.STONE)
            .mapColor(Blocks.PISTON_HEAD.defaultMapColor())
    );
    public static final UnfiredPotteryBlock unfiredDecoratedPotBlock = new UnfiredDecoratedPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
    public static final UnfiredPotteryBlock unfiredDecoratedPotBlockWithSherds = new UnfiredDecoratedPotBlockWithSherds(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredCrucibleBlock = new UnfiredCrucibleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredPlanterBlock = new UnfiredPlanterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredVaseBlock = new UnfiredVaseBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
	public static final UnfiredPotteryBlock unfiredUrnBlock = new UnfiredUrnBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
    public static final UnfiredPotteryBlock unfiredFlowerPotBlock = new UnfiredFlowerPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CLAY)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
    );
	public static final Block urnBlock = new UrnBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)
            .noOcclusion()
            .isRedstoneConductor(Blocks::never)
            .isValidSpawn(Blocks::never)
            .destroyTime(2f)
    );
	public static final HashMap<DyeColor, VaseBlock> vaseBlocks = new HashMap<>();
	public static final Block wickerPaneBlock = new IronBarsBlock(BlockBehaviour.Properties.of()
            .strength(0.5f)
            .sound(SoundType.GRASS)
            .ignitedByLava()
            .noOcclusion()
    );
    public static final Block wickerBlock = new Block(BlockBehaviour.Properties.of().destroyTime(2f).ignitedByLava().mapColor(MapColor.PODZOL).sound(SoundType.GRASS));
    public static final Block wickerSlabBlock = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(wickerBlock));
    public static final HashMap<DyeColor, SlabBlock> woolSlabBlocks = new HashMap<>();
    public static final Block vineTrapBlock = new VineTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
            .isValidSpawn(Blocks::never)
            .noCollission()
    );
    public static final Block dirtSlabBlock = new DirtSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT), Blocks.DIRT);
    public static final Block dirtPathSlabBlock = new DirtPathSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT_PATH), Blocks.DIRT_PATH);
    public static final Block grassSlabBlock = new GrassSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK), Blocks.GRASS_BLOCK);
    public static final Block myceliumSlabBlock = new MyceliumSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MYCELIUM), Blocks.MYCELIUM);
    public static final Block podzolSlabBlock = new MyceliumSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL), Blocks.PODZOL);

    public static final Block netherGroth = new NetherGrothBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .isRedstoneConductor(Blocks::never)
                    .randomTicks()
                    .strength(0.2f)
                    .speedFactor(0.4F)
                    .sound(SoundType.FUNGUS)
                    .pushReaction(PushReaction.DESTROY)
                    .speedFactor(0.8f)
    );

    public static final Block grothedNetherrackBlock = new GrothedNetherrackBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(0.4F)
                    .sound(SoundType.NETHERRACK)
    );

    @Override
    public void onInitialize() {
        // Axles
        Registry.register(BuiltInRegistries.BLOCK, Id.of("axle"), axleBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("axle"), new BlockItem(axleBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("axle_power_source"), axlePowerSourceBlock);
        Registry.register(BuiltInRegistries.BLOCK, Id.of("creative_power_source"), creativePowerSouceBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("creative_power_source"), new BlockItem(creativePowerSouceBlock, new Item.Properties()));
        // Gearbox
        Registry.register(BuiltInRegistries.BLOCK, Id.of("gear_box"), gearBoxBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("gear_box"), new BlockItem(gearBoxBlock, new Item.Properties()));
        // Redstone Clutch
        Registry.register(BuiltInRegistries.BLOCK, Id.of("redstone_clutch"), redstoneClutchBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("redstone_clutch"), new BlockItem(redstoneClutchBlock, new Item.Properties()));
        // Hibachi
        Registry.register(BuiltInRegistries.BLOCK, Id.of("hibachi"), hibachiBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("hibachi"), new BlockItem(hibachiBlock, new Item.Properties()));
        // Light Block
        Registry.register(BuiltInRegistries.BLOCK, Id.of("light_block"), lightBlockBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("light_block"), new BlockItem(lightBlockBlock, new Item.Properties()));
        // Block Dispenser
        Registry.register(BuiltInRegistries.BLOCK, Id.of("block_dispenser"), blockDispenserBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("block_dispenser"), new BlockItem(blockDispenserBlock, new Item.Properties()));
        // Cauldron / Stewing Pot
        Registry.register(BuiltInRegistries.BLOCK, Id.of("cauldron"), cauldronBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("cauldron"), new BlockItem(cauldronBlock, new Item.Properties()));
        // Obsidian pressure plate
        Registry.register(BuiltInRegistries.BLOCK, Id.of("obsidian_pressure_plate"), obsidianPressurePlateBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("obsidian_pressure_plate"), new BlockItem(obsidianPressurePlateBlock, new Item.Properties()));
        // Hemp crop
        Registry.register(BuiltInRegistries.BLOCK, Id.of("hemp_crop_block"), hempCropBlock);
        // Detector Block
        Registry.register(BuiltInRegistries.BLOCK, Id.of("detector_block"), detectorBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("detector_block"), new BlockItem(detectorBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("detector_logic_block"), detectorLogicBlock);
        // Mill Stone
        Registry.register(BuiltInRegistries.BLOCK, Id.of("mill_stone"), millStoneBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("mill_stone"), new BlockItem(millStoneBlock, new Item.Properties()));
        // Companion Cube
        Registry.register(BuiltInRegistries.BLOCK, Id.of("companion_cube"), companionCubeBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("companion_cube"), new BlockItem(companionCubeBlock, new Item.Properties()));
        // Companion Slab
        Registry.register(BuiltInRegistries.BLOCK, Id.of("companion_slab"), companionSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("companion_slab"), new BlockItem(companionSlabBlock, new Item.Properties()));
        // Hand Crank
        Registry.register(BuiltInRegistries.BLOCK, Id.of("hand_crank"), handCrankBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("hand_crank"), new BlockItem(handCrankBlock, new Item.Properties()));
        // Anchor
        Registry.register(BuiltInRegistries.BLOCK, Id.of("anchor"), anchorBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("anchor"), new BlockItem(anchorBlock, new Item.Properties()));
        // Rope
        Registry.register(BuiltInRegistries.BLOCK, Id.of("rope"), ropeBlock);
        // Stone Detector Rail
        Registry.register(BuiltInRegistries.BLOCK, Id.of("stone_detector_rail"), stoneDetectorRailBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("stone_detector_rail"), new BlockItem(stoneDetectorRailBlock, new Item.Properties()));
        // Obsidian Detector Rail
        Registry.register(BuiltInRegistries.BLOCK, Id.of("obsidian_detector_rail"), obsidianDetectorRailBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("obsidian_detector_rail"), new BlockItem(obsidianDetectorRailBlock, new Item.Properties()));
        // Bwt Hopper
        Registry.register(BuiltInRegistries.BLOCK, Id.of("hopper"), hopperBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("hopper"), new BlockItem(hopperBlock, new Item.Properties()));
        // Grate
        Registry.register(BuiltInRegistries.BLOCK, Id.of("grate"), grateBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("grate"), new BlockItem(grateBlock, new Item.Properties()));
        // Slats
        Registry.register(BuiltInRegistries.BLOCK, Id.of("slats"), slatsBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("slats"), new BlockItem(slatsBlock, new Item.Properties()));
        // Wicker
        Registry.register(BuiltInRegistries.BLOCK, Id.of("wicker"), wickerPaneBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("wicker"), new BlockItem(wickerPaneBlock, new Item.Properties()));
        // Saw
        Registry.register(BuiltInRegistries.BLOCK, Id.of("saw"), sawBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("saw"), new BlockItem(sawBlock, new Item.Properties()));
        // Pulley
        Registry.register(BuiltInRegistries.BLOCK, Id.of("pulley"), pulleyBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("pulley"), new BlockItem(pulleyBlock, new Item.Properties()));
        // Platform
        Registry.register(BuiltInRegistries.BLOCK, Id.of("platform"), platformBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("platform"), new BlockItem(platformBlock, new Item.Properties()));
        // Turntable
        Registry.register(BuiltInRegistries.BLOCK, Id.of("turntable"), turntableBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("turntable"), new BlockItem(turntableBlock, new Item.Properties()));
        // Stoked Fire
        Registry.register(BuiltInRegistries.BLOCK, Id.of("stoked_fire"), stokedFireBlock);
        // Bellows
        Registry.register(BuiltInRegistries.BLOCK, Id.of("bellows"), bellowsBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("bellows"), new BlockItem(bellowsBlock, new Item.Properties()));
        // Unfired Pottery
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_decorated_pot_with_sherds"), unfiredDecoratedPotBlockWithSherds);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_decorated_pot_with_sherds"), new BlockItem(unfiredDecoratedPotBlockWithSherds, new Item.Properties().component(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY)));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_decorated_pot"), unfiredDecoratedPotBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_decorated_pot"), new BlockItem(unfiredDecoratedPotBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_crucible"), unfiredCrucibleBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_crucible"), new BlockItem(unfiredCrucibleBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_planter"), unfiredPlanterBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_planter"), new BlockItem(unfiredPlanterBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_vase"), unfiredVaseBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_vase"), new BlockItem(unfiredVaseBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_urn"), unfiredUrnBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_urn"), new BlockItem(unfiredUrnBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("unfired_flower_pot"), unfiredFlowerPotBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("unfired_flower_pot"), new BlockItem(unfiredFlowerPotBlock, new Item.Properties()));
        // Kiln
        Registry.register(BuiltInRegistries.BLOCK, Id.of("kiln"), kilnBlock);
        // Blood Wood
        bloodWoodBlocks.register();
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
        // Crucible
        Registry.register(BuiltInRegistries.BLOCK, Id.of("crucible"), crucibleBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("crucible"), new BlockItem(crucibleBlock, new Item.Properties()));
        // Planters
        Registry.register(BuiltInRegistries.BLOCK, Id.of("planter"), planterBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("planter"), new BlockItem(planterBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("soil_planter"), soilPlanterBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("soil_planter"), new BlockItem(soilPlanterBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("soul_sand_planter"), soulSandPlanterBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("soul_sand_planter"), new BlockItem(soulSandPlanterBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("grass_planter"), grassPlanterBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("grass_planter"), new BlockItem(grassPlanterBlock, new Item.Properties()));
        // Vases
        VaseBlock.registerColors(vaseBlocks);
        // Urn
        Registry.register(BuiltInRegistries.BLOCK, Id.of("urn"), urnBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("urn"), new BlockItem(urnBlock, new Item.Properties()));
        // SoulForge
        Registry.register(BuiltInRegistries.BLOCK, Id.of("soul_forge"), soulForgeBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("soul_forge"), new BlockItem(soulForgeBlock, new Item.Properties()));
        // Wool slabs
        DyeUtils.WOOL_COLORS.forEach((dyeColor, woolBlock) -> {
            SlabBlock woolSlabBlock = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(woolBlock));
            woolSlabBlocks.put(dyeColor, woolSlabBlock);
            Registry.register(BuiltInRegistries.BLOCK, Id.of(dyeColor.getName() + "_wool_slab"), woolSlabBlock);
            Registry.register(BuiltInRegistries.ITEM, Id.of(dyeColor.getName() + "_wool_slab"), new BlockItem(woolSlabBlock, new Item.Properties()));
        });
        // Buddy Block
        Registry.register(BuiltInRegistries.BLOCK, Id.of("buddy_block"), buddyBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("buddy_block"), new BlockItem(buddyBlock, new Item.Properties()));
        // Aesthetic compacting blocks
        Registry.register(BuiltInRegistries.BLOCK, Id.of("soap_block"), soapBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("soap_block"), new BlockItem(soapBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("wicker_block"), wickerBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("wicker_block"), new BlockItem(wickerBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("dung_block"), dungBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("dung_block"), new BlockItem(dungBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("padding_block"), paddingBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("padding_block"), new BlockItem(paddingBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("rope_coil_block"), ropeCoilBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("rope_coil_block"), new BlockItem(ropeCoilBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("concentrated_hellfire_block"), concentratedHellfireBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("concentrated_hellfire_block"), new BlockItem(concentratedHellfireBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("wicker_slab"), wickerSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("wicker_slab"), new BlockItem(wickerSlabBlock, new Item.Properties()));
        // Mining charge
        Registry.register(BuiltInRegistries.BLOCK, Id.of("mining_charge"), miningChargeBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("mining_charge"), new BlockItem(miningChargeBlock, new Item.Properties()));
        // Vine trap
        Registry.register(BuiltInRegistries.BLOCK, Id.of("vine_trap"), vineTrapBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("vine_trap"), new BlockItem(vineTrapBlock, new Item.Properties()));
        // Lens
        Registry.register(BuiltInRegistries.BLOCK, Id.of("lens"), lensBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("lens"), new BlockItem(lensBlock, new Item.Properties()));
        Registry.register(BuiltInRegistries.BLOCK, Id.of("lens_beam"), lensBeamBlock);
        Registry.register(BuiltInRegistries.BLOCK, Id.of("lens_beam_glass"), lensBeamGlassBlock);
        // Dirt Slab
        Registry.register(BuiltInRegistries.BLOCK, Id.of("dirt_slab"), dirtSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("dirt_slab"), new BlockItem(dirtSlabBlock, new Item.Properties()));
        // Dirt Path Slab
        Registry.register(BuiltInRegistries.BLOCK, Id.of("dirt_path_slab"), dirtPathSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("dirt_path_slab"), new BlockItem(dirtPathSlabBlock, new Item.Properties()));
        // Grass Slab
        Registry.register(BuiltInRegistries.BLOCK, Id.of("grass_slab"), grassSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("grass_slab"), new BlockItem(grassSlabBlock, new Item.Properties()));
        // Mycelium Slab
        Registry.register(BuiltInRegistries.BLOCK, Id.of("mycelium_slab"), myceliumSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("mycelium_slab"), new BlockItem(myceliumSlabBlock, new Item.Properties()));
        // Podzol Slab
        Registry.register(BuiltInRegistries.BLOCK, Id.of("podzol_slab"), podzolSlabBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("podzol_slab"), new BlockItem(podzolSlabBlock, new Item.Properties()));
        // Nether Groth
        Registry.register(BuiltInRegistries.BLOCK, Id.of("nether_groth"), netherGroth);
        Registry.register(BuiltInRegistries.ITEM, Id.of("nether_groth"), new BlockItem(netherGroth, new Item.Properties()));
        // Grothed Netherrack
        Registry.register(BuiltInRegistries.BLOCK, Id.of("grothed_netherrack"), grothedNetherrackBlock);
        // Aqueduct
        Registry.register(BuiltInRegistries.BLOCK, Id.of("aqueduct"), aqueductBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("aqueduct"), new BlockItem(aqueductBlock, new Item.Properties()));
        // Screw pump
        Registry.register(BuiltInRegistries.BLOCK, Id.of("screw_pump"), screwPumpBlock);
        Registry.register(BuiltInRegistries.ITEM, Id.of("screw_pump"), new BlockItem(screwPumpBlock, new Item.Properties()));

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

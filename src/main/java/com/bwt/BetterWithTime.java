package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserScreenHandler;
import com.bwt.blocks.cauldron.CauldronScreenHandler;
import com.bwt.blocks.crucible.CrucibleScreenHandler;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mech_hopper.MechHopperScreenHandler;
import com.bwt.blocks.mill_stone.MillStoneScreenHandler;
import com.bwt.blocks.mining_charge.MiningChargeExplosion;
import com.bwt.blocks.pulley.PulleyScreenHandler;
import com.bwt.blocks.soul_forge.SoulForgeScreenHandler;
import com.bwt.blocks.turntable.CanRotateHelper;
import com.bwt.blocks.turntable.HorizontalBlockAttachmentHelper;
import com.bwt.blocks.turntable.RotationProcessHelper;
import com.bwt.blocks.turntable.VerticalBlockAttachmentHelper;
import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.entities.BwtEntities;
import com.bwt.gamerules.BwtGameRules;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BwtRecipes;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.Id;
import com.bwt.utils.TrackedDataHandlers;
import com.bwt.utils.kiln_block_cook_overlay.KilnBlockCookingProgressPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterWithTime implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("betterwithtime");

	public static final BwtBlocks blocks = new BwtBlocks();
	public static final BwtBlockEntities blockEntities = new BwtBlockEntities();
	public static final BwtItems items = new BwtItems();
	public static final BwtEntities entities = new BwtEntities();
	public static final BwtRecipes recipes = new BwtRecipes();
	public static final BwtDamageTypes damageTypes = new BwtDamageTypes();
	public static final BwtGameRules gameRules = new BwtGameRules();
	public static final BwtSoundEvents soundEvents = new BwtSoundEvents();
	public static final TrackedDataHandlers dataHandlers = new TrackedDataHandlers();
	public static ScreenHandlerType<BlockDispenserScreenHandler> blockDispenserScreenHandler = new ScreenHandlerType<>(BlockDispenserScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<CauldronScreenHandler> cauldronScreenHandler = new ScreenHandlerType<>(CauldronScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<CrucibleScreenHandler> crucibleScreenHandler = new ScreenHandlerType<>(CrucibleScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<MillStoneScreenHandler> millStoneScreenHandler = new ScreenHandlerType<>(MillStoneScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<PulleyScreenHandler> pulleyScreenHandler = new ScreenHandlerType<>(PulleyScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<MechHopperScreenHandler> mechHopperScreenHandler = new ScreenHandlerType<>(MechHopperScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<SoulForgeScreenHandler> soulForgeScreenHandler = new ScreenHandlerType<>(SoulForgeScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

	static {
		blockDispenserScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("block_dispenser"), blockDispenserScreenHandler);
		cauldronScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("cauldron"), cauldronScreenHandler);
		crucibleScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("crucible"), crucibleScreenHandler);
		millStoneScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("mill_stone"), millStoneScreenHandler);
		pulleyScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("pulley"), pulleyScreenHandler);
		mechHopperScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("hopper"), mechHopperScreenHandler);
		soulForgeScreenHandler = Registry.register(Registries.SCREEN_HANDLER, Id.of("soul_forge"), soulForgeScreenHandler);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		blocks.onInitialize();
		blockEntities.onInitialize();
		items.onInitialize();
		entities.onInitialize();
		recipes.onInitialize();
		damageTypes.onInitialize();
		gameRules.onInitialize();
		soundEvents.onInitialize();
		dataHandlers.onInitialize();

		// Fuel maps
		// Vanilla change to account for the moulding -> stick recipe
		// If uncorrected, you would gain free fuel time by converting your moulding to sticks
		FuelRegistry.INSTANCE.add(Items.STICK, 75);
		FuelRegistry.INSTANCE.add(BwtItems.nethercoalItem, 3200);
		FuelRegistry.INSTANCE.add(BwtItemTags.WOODEN_SIDING_BLOCKS, 150);
		FuelRegistry.INSTANCE.add(BwtItemTags.WOODEN_MOULDING_BLOCKS, 75);
		FuelRegistry.INSTANCE.add(BwtItemTags.WOODEN_CORNER_BLOCKS, 38);
		FuelRegistry.INSTANCE.add(BwtBlocks.axleBlock, 150);
		FuelRegistry.INSTANCE.add(BwtBlocks.axlePowerSourceBlock, 150);
		FuelRegistry.INSTANCE.add(BwtBlocks.bellowsBlock, 450);
//      FuelRegistry.INSTANCE.add(BwtBlocks.bloodWoodBlock)
		FuelRegistry.INSTANCE.add(BwtBlocks.gearBoxBlock, 600);
		FuelRegistry.INSTANCE.add(BwtBlocks.grateBlock, 300);
		FuelRegistry.INSTANCE.add(BwtBlocks.hopperBlock, 300);
		FuelRegistry.INSTANCE.add(BwtBlocks.platformBlock, 375);
		FuelRegistry.INSTANCE.add(BwtBlocks.pulleyBlock, 600);
		FuelRegistry.INSTANCE.add(BwtBlocks.sawBlock, 300);
		FuelRegistry.INSTANCE.add(BwtBlocks.slatsBlock, 300);
//      FuelRegistry.INSTANCE.add(BwtBlocks.screwPumpBlock)
        FuelRegistry.INSTANCE.add(BwtItemTags.WOODEN_TABLE_BLOCKS, 150);
		FuelRegistry.INSTANCE.add(BwtItems.gearItem, 18);
		FuelRegistry.INSTANCE.add(BwtItems.sawDustItem, 150);
		FuelRegistry.INSTANCE.add(BwtItems.soulDustItem, 150);

		// Block Dispenser Behaviors
		BwtBlocks.blockDispenserBlock.registerItemDispenseBehaviors();
		BwtBlocks.blockDispenserBlock.registerBehaviors();
		// Hopper filters
		MechHopperBlock.addDefaultFilters();
		// Turntable attached block handlers
		CanRotateHelper.registerDefaults();
		RotationProcessHelper.registerDefaults();
		HorizontalBlockAttachmentHelper.registerDefaults();
		VerticalBlockAttachmentHelper.registerDefaults();

		LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
			if (!source.isBuiltin()) {
				return;
			}
			if (key.equals(EntityType.WOLF.getLootTableId())) {
				LootPool.Builder poolBuilder = LootPool.builder()
						.with(ItemEntry.builder(BwtItems.wolfChopItem)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)))
						).apply(
                                FurnaceSmeltLootFunction.builder()
                                        .conditionally(
                                                EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true)))
                                        )
						).apply(EnchantedCountIncreaseLootFunction.builder(wrapperLookup, UniformLootNumberProvider.create(0.0f, 1.0f)));

				tableBuilder.pool(poolBuilder);
			}
			if (key.equals(Blocks.COBBLESTONE.getLootTableKey())) {
				tableBuilder.modifyPools(builder -> builder.conditionally(new InvertedLootCondition(MiningChargeExplosion.LOOT_CONDITION)))
						.pool(LootPool.builder().conditionally(MiningChargeExplosion.LOOT_CONDITION).with(ItemEntry.builder(Items.GRAVEL)));
			}
		});

		StrippableBlockRegistry.register(BwtBlocks.bloodWoodBlocks.logBlock, BwtBlocks.bloodWoodBlocks.strippedLogBlock);
		StrippableBlockRegistry.register(BwtBlocks.bloodWoodBlocks.woodBlock, BwtBlocks.bloodWoodBlocks.strippedWoodBlock);

		// Drop hemp seeds from tilled grass 1/25th of the time
		TillableBlockRegistry.register(
			Blocks.GRASS_BLOCK,
			HoeItem::canTillFarmland,
			context -> {
				BlockState result = Blocks.FARMLAND.getDefaultState();
				HoeItem.createTillAction(result).accept(context);
				Item tool = context.getStack().getItem();
				int randBound = 30;
				if (tool instanceof HoeItem hoeItem) {
					randBound -= Math.round(hoeItem.getMaterial().getMiningSpeedMultiplier());
				}
				if (context.getWorld().getRandom().nextInt(randBound) == 0) {
					Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(BwtItems.hempSeedsItem));
				}
			}
		);

		PayloadTypeRegistry.playS2C().register(KilnBlockCookingProgressPayload.ID, KilnBlockCookingProgressPayload.CODEC);

        ResourceManagerHelper.registerBuiltinResourcePack(
                Id.PROGRAMMER_ART_PACK_ID,
                FabricLoader.getInstance().getModContainer(Id.MOD_ID).orElseThrow(),
                Text.literal("BWT Programmer Art"),
                ResourcePackActivationType.NORMAL
        );
	}
}
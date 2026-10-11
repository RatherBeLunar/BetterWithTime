package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotData;
import com.bwt.blocks.block_dispenser.BlockDispenserScreenHandler;
import com.bwt.blocks.cauldron.CauldronScreenHandler;
import com.bwt.blocks.crucible.CrucibleScreenHandler;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mech_hopper.MechHopperScreenHandler;
import com.bwt.blocks.mill_stone.MillStoneScreenHandler;
import com.bwt.blocks.pulley.PulleyScreenHandler;
import com.bwt.blocks.soul_forge.SoulForgeScreenHandler;
import com.bwt.blocks.turntable.CanRotateHelper;
import com.bwt.blocks.turntable.HorizontalBlockAttachmentHelper;
import com.bwt.blocks.turntable.RotationProcessHelper;
import com.bwt.blocks.turntable.VerticalBlockAttachmentHelper;
import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.entities.BwtEntities;
import com.bwt.entities.MiningChargeEntity;
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
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Optional;

public class BetterWithTime implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final BwtBlocks blocks = new BwtBlocks();
	public static final BwtBlockEntities blockEntities = new BwtBlockEntities();
	public static final BwtItems items = new BwtItems();
	public static final BwtEntities entities = new BwtEntities();
	public static final BwtRecipes recipes = new BwtRecipes();
	public static final BwtDamageTypes damageTypes = new BwtDamageTypes();
	public static final BwtGameRules gameRules = new BwtGameRules();
	public static final BwtSoundEvents soundEvents = new BwtSoundEvents();
	public static final TrackedDataHandlers dataHandlers = new TrackedDataHandlers();
	public static MenuType<BlockDispenserScreenHandler> blockDispenserScreenHandler = new MenuType<>(BlockDispenserScreenHandler::new, FeatureFlags.VANILLA_SET);
	public static ExtendedScreenHandlerType<CauldronScreenHandler, AbstractCookingPotData> cauldronScreenHandler = new ExtendedScreenHandlerType<>(CauldronScreenHandler::new, AbstractCookingPotData.PACKET_CODEC);
	public static ExtendedScreenHandlerType<CrucibleScreenHandler, AbstractCookingPotData> crucibleScreenHandler = new ExtendedScreenHandlerType<>(CrucibleScreenHandler::new, AbstractCookingPotData.PACKET_CODEC);
	public static MenuType<MillStoneScreenHandler> millStoneScreenHandler = new MenuType<>(MillStoneScreenHandler::new, FeatureFlags.VANILLA_SET);
	public static MenuType<PulleyScreenHandler> pulleyScreenHandler = new MenuType<>(PulleyScreenHandler::new, FeatureFlags.VANILLA_SET);
	public static MenuType<MechHopperScreenHandler> mechHopperScreenHandler = new MenuType<>(MechHopperScreenHandler::new, FeatureFlags.VANILLA_SET);
	public static MenuType<SoulForgeScreenHandler> soulForgeScreenHandler = new MenuType<>(SoulForgeScreenHandler::new, FeatureFlags.VANILLA_SET);

	static {
		blockDispenserScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("block_dispenser"), blockDispenserScreenHandler);
		cauldronScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("cauldron"), cauldronScreenHandler);
		crucibleScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("crucible"), crucibleScreenHandler);
		millStoneScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("mill_stone"), millStoneScreenHandler);
		pulleyScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("pulley"), pulleyScreenHandler);
		mechHopperScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("hopper"), mechHopperScreenHandler);
		soulForgeScreenHandler = Registry.register(BuiltInRegistries.MENU, Id.of("soul_forge"), soulForgeScreenHandler);
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
		FuelRegistryEvents.BUILD.register(((builder, context) -> {
			builder.add(Items.STICK, 75);
			builder.add(BwtItems.nethercoalItem, 3200);
			builder.add(BwtItemTags.WOODEN_SIDING_BLOCKS, 150);
			builder.add(BwtItemTags.WOODEN_MOULDING_BLOCKS, 75);
			builder.add(BwtItemTags.WOODEN_CORNER_BLOCKS, 38);
			builder.add(BwtItemTags.WOODEN_COLUMN_BLOCKS, 75);
			builder.add(BwtItemTags.WOODEN_PEDESTAL_BLOCKS, 300);
			builder.add(BwtItemTags.WOODEN_TABLE_BLOCKS, 150);
			builder.add(BwtBlocks.axleBlock, 150);
			builder.add(BwtBlocks.axlePowerSourceBlock, 150);
			builder.add(BwtBlocks.bellowsBlock, 450);
			builder.add(BwtBlocks.gearBoxBlock, 600);
			builder.add(BwtBlocks.redstoneClutchBlock, 600);
			builder.add(BwtBlocks.grateBlock, 300);
			builder.add(BwtBlocks.hopperBlock, 300);
			builder.add(BwtBlocks.platformBlock, 375);
			builder.add(BwtBlocks.pulleyBlock, 600);
			builder.add(BwtBlocks.sawBlock, 300);
			builder.add(BwtBlocks.slatsBlock, 300);
//      builder.add(BwtBlocks.screwPumpBlock)
			builder.add(BwtItemTags.WOODEN_TABLE_BLOCKS, 150);
			builder.add(BwtItems.gearItem, 18);
			builder.add(BwtItems.sawDustItem, 150);
			builder.add(BwtItems.soulDustItem, 150);
		}));


        // Composting
        CompostingChanceRegistry.INSTANCE.add(BwtItems.hempSeedsItem, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(BwtItems.hempItem, 0.5F);

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
			if (key.equals(EntityType.WOLF.getDefaultLootTable().orElse(null))) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.add(LootItem.lootTableItem(BwtItems.wolfChopItem)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f)))
						).apply(
                                SmeltItemFunction.smelted()
                                        .when(
                                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true)))
                                        )
						).apply(EnchantedCountIncreaseFunction.lootingMultiplier(wrapperLookup, UniformGenerator.between(0.0f, 1.0f)));

				tableBuilder.withPool(poolBuilder);
			}
			if (key.equals(Blocks.COBBLESTONE.getLootTable().orElse(null))) {
                LootItemCondition miningChargeCondition = MiningChargeEntity.getLootCondition(wrapperLookup.lookupOrThrow(Registries.ENTITY_TYPE));
				tableBuilder.modifyPools(builder -> builder.conditionally(new InvertedLootItemCondition(miningChargeCondition)))
						.withPool(LootPool.lootPool().conditionally(miningChargeCondition).add(LootItem.lootTableItem(Items.GRAVEL)));
			}
		});

		StrippableBlockRegistry.register(BwtBlocks.bloodWoodBlocks.logBlock, BwtBlocks.bloodWoodBlocks.strippedLogBlock);
		StrippableBlockRegistry.register(BwtBlocks.bloodWoodBlocks.woodBlock, BwtBlocks.bloodWoodBlocks.strippedWoodBlock);

		// Drop hemp seeds from tilled grass 1/25th of the time
		TillableBlockRegistry.register(
			Blocks.GRASS_BLOCK,
			HoeItem::onlyIfAirAbove,
			context -> {
				BlockState result = Blocks.FARMLAND.defaultBlockState();
				HoeItem.changeIntoState(result).accept(context);
				Item tool = context.getItemInHand().getItem();
				int randBound = 30;
				if (tool instanceof HoeItem hoeItem) {
                    Tool toolComponent = hoeItem.components().get(DataComponents.TOOL);
					if (toolComponent != null) {
						float speed = toolComponent
								.rules()
								.stream()
								.map(Tool.Rule::speed)
								.filter(Optional::isPresent)
								.map(Optional::get)
								.findFirst()
								.orElse(0f);
						randBound -= Math.round(speed);
					}
				}
				if (context.getLevel().getRandom().nextInt(randBound) == 0) {
					Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(BwtItems.hempSeedsItem));
				}
			}
		);

		PayloadTypeRegistry.playS2C().register(KilnBlockCookingProgressPayload.ID, KilnBlockCookingProgressPayload.CODEC);

        ResourceManagerHelper.registerBuiltinResourcePack(
                Id.PROGRAMMER_ART_PACK_ID,
                FabricLoader.getInstance().getModContainer(Id.MOD_ID).orElseThrow(),
                Component.literal("BWT Programmer Art"),
                ResourcePackActivationType.NORMAL
        );

		ResourceManagerHelper.registerBuiltinResourcePack(
				Id.LEGACY_SOUNDS_PACK_ID,
				FabricLoader.getInstance().getModContainer(Id.MOD_ID).orElseThrow(),
				Component.literal("Legacy Sounds Pack"),
				ResourcePackActivationType.NORMAL
		);
	}
}
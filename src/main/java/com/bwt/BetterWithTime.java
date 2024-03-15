package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserScreenHandler;
import com.bwt.blocks.cauldron.CauldronScreenHandler;
import com.bwt.blocks.mech_hopper.MechHopperBlock;
import com.bwt.blocks.mech_hopper.MechHopperScreenHandler;
import com.bwt.blocks.mill_stone.MillStoneScreenHandler;
import com.bwt.blocks.pulley.PulleyScreenHandler;
import com.bwt.damage_types.BwtDamageTypes;
import com.bwt.entities.BwtEntities;
import com.bwt.gamerules.BwtGameRules;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BwtRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
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

	public static final Identifier MECH_BANG = new Identifier("bwt:mech_bang");
	public static SoundEvent MECH_BANG_SOUND = SoundEvent.of(MECH_BANG);
	public static ScreenHandlerType<BlockDispenserScreenHandler> blockDispenserScreenHandler = new ScreenHandlerType<>(BlockDispenserScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<CauldronScreenHandler> cauldronScreenHandler = new ScreenHandlerType<>(CauldronScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<MillStoneScreenHandler> millStoneScreenHandler = new ScreenHandlerType<>(MillStoneScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<PulleyScreenHandler> pulleyScreenHandler = new ScreenHandlerType<>(PulleyScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<MechHopperScreenHandler> mechHopperScreenHandler = new ScreenHandlerType<>(MechHopperScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

	private static final Identifier WOLF_LOOT_TABLE_ID = EntityType.WOLF.getLootTableId();

	static {
		blockDispenserScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "block_dispenser"), blockDispenserScreenHandler);
		cauldronScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "cauldron"), cauldronScreenHandler);
		millStoneScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "mill_stone"), millStoneScreenHandler);
		pulleyScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "pulley"), pulleyScreenHandler);
		mechHopperScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "hopper"), mechHopperScreenHandler);
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
		Registry.register(Registries.SOUND_EVENT, MECH_BANG, MECH_BANG_SOUND);

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin() && id.equals(WOLF_LOOT_TABLE_ID)) {
				LootPool.Builder poolBuilder = LootPool.builder()
						.with(ItemEntry.builder(BwtItems.wolfChopItem)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)))
						).apply(
                                FurnaceSmeltLootFunction.builder()
                                        .conditionally(
                                                EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true)))
                                        )
						).apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f)));

				tableBuilder.pool(poolBuilder);
			}
		});

		// Drop hemp seeds from tilled grass 1/25th of the time
		TillableBlockRegistry.register(
			Blocks.GRASS_BLOCK,
			HoeItem::canTillFarmland,
			context -> {
				BlockState result = Blocks.FARMLAND.getDefaultState();
				HoeItem.createTillAction(result).accept(context);
				Item tool = context.getStack().getItem();
				int randBound = 25;
				if (tool instanceof HoeItem hoeItem) {
					randBound -= hoeItem.getMaterial().getMiningLevel() * 2;
				}
				if (context.getWorld().getRandom().nextInt(randBound) == 0) {
					Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(BwtItems.hempSeedsItem));
				}
			}
		);

		// Block Dispenser Behaviors
		BwtBlocks.blockDispenserBlock.registerItemDispenseBehaviors();
		BwtBlocks.blockDispenserBlock.registerEntityInhaleBehaviors();
		// Hopper filters
		MechHopperBlock.addDefaultFilters();
	}
}
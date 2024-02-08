package com.bwt;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserScreenHandler;
import com.bwt.blocks.cauldron.CauldronScreenHandler;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import com.bwt.recipes.BwtRecipes;
import com.bwt.tags.BwtTags;
import net.fabricmc.api.ModInitializer;
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
	public static final BwtTags tags = new BwtTags();
	public static final Identifier MECH_BANG = new Identifier("bwt:mech_bang");
	public static SoundEvent MECH_BANG_SOUND = SoundEvent.of(MECH_BANG);
	public static ScreenHandlerType<BlockDispenserScreenHandler> blockDispenserScreenHandler = new ScreenHandlerType<>(BlockDispenserScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static ScreenHandlerType<CauldronScreenHandler> cauldronScreenHandler = new ScreenHandlerType<>(CauldronScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

	static {
		blockDispenserScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "block_dispenser"), blockDispenserScreenHandler);
		cauldronScreenHandler = Registry.register(Registries.SCREEN_HANDLER, new Identifier("bwt", "cauldron"), cauldronScreenHandler);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		blocks.onInitialize();
		blockEntities.onInitialize();
		items.onInitialize();
		entities.onInitialize();
		recipes.onInitialize();
		tags.onInitialize();
		Registry.register(Registries.SOUND_EVENT, MECH_BANG, MECH_BANG_SOUND);
	}
}
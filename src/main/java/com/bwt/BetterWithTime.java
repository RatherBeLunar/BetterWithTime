package com.bwt;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.BwtEntities;
import com.bwt.items.BwtItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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
	public static final BwtItems items = new BwtItems();
	public static final BwtEntities entities = new BwtEntities();

	public static final Identifier MECH_BANG = new Identifier("bwt:mech_bang");
	public static SoundEvent MECH_BANG_SOUND = SoundEvent.of(MECH_BANG);



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		blocks.onInitialize();
		items.onInitialize();
		entities.onInitialize();
		Registry.register(Registries.SOUND_EVENT, MECH_BANG, MECH_BANG_SOUND);
	}
}
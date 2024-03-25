package com.bwt.sounds;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class BwtSoundEvents implements ModInitializer {
    public static SoundEvent MECH_BANG = register("block.mech.band");
    public static SoundEvent MECH_EXPLODE = register("block.mech.explode");
    public static SoundEvent MECH_CREAK = register("block.mech.creak");
    public static SoundEvent ANCHOR_RETRACT = register("block.anchor.retract");
    public static SoundEvent BELLOWS_COMPRESS = register("block.bellows.compress");
    public static SoundEvent COMPANION_CUBE_DEATH = register("block.companion_cube.death");
    public static SoundEvent GEAR_BOX_ACTIVATE = register("block.gear_box.activate");
    public static SoundEvent HAND_CRANK_CLICK = register("block.hand_crank.click");
    public static SoundEvent HIBACHI_IGNITE = register("block.hibachi.ignite");
    public static SoundEvent DETECTOR_CLICK = register("block.detector.click");
    public static SoundEvent SOUL_CONVERSION = register("block.generic.soul_conversion");
    public static SoundEvent MILL_STONE_GRIND = register("block.mill_stone.grind");
    public static SoundEvent TURNTABLE_SETTING_CLICK = register("block.turntable.setting_click");
    public static SoundEvent TURNTABLE_TURNING_CLICK = register("block.turntable.turning_click");
    public static SoundEvent WOLF_DUNG_PRODUCTION = register("entity.wolf.dung.production");
    public static SoundEvent WOLF_DUNG_EFFORT = register("entity.wolf.dung.effort");

    protected static SoundEvent register(String id) {
        return BwtSoundEvents.register(new Identifier("bwt", id));
    }

    protected static SoundEvent register(Identifier id) {
        return BwtSoundEvents.register(id, id);
    }

    protected static RegistryEntry.Reference<SoundEvent> registerReference(String id) {
        return BwtSoundEvents.registerReference(new Identifier(id));
    }

    protected static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id) {
        return BwtSoundEvents.registerReference(id, id);
    }

    protected static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id, Identifier soundId) {
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    protected static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    @Override
    public void onInitialize() {

    }
}

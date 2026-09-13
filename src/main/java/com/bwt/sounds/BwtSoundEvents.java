package com.bwt.sounds;

import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BwtSoundEvents implements ModInitializer {
    public static final SoundEvent MECH_BANG = register("block.mech.bang");
    public static final SoundEvent MECH_EXPLODE = register("block.mech.explode");
    public static final SoundEvent MECH_CREAK = register("block.mech.creak");
    public static final SoundEvent ANCHOR_RETRACT = register("block.anchor.retract");
    public static final SoundEvent BELLOWS_COMPRESS = register("block.bellows.compress");
    public static final SoundEvent COMPANION_CUBE_WHINE = register("block.companion_cube.whine");
    public static final SoundEvent COMPANION_CUBE_DEATH = register("block.companion_cube.death");
    public static final SoundEvent GEAR_BOX_ACTIVATE = register("block.gear_box.activate");
    public static final SoundEvent HAND_CRANK_CLICK = register("block.hand_crank.click");
    public static final SoundEvent HIBACHI_IGNITE = register("block.hibachi.ignite");
    public static final SoundEvent DETECTOR_CLICK = register("block.detector.click");
    public static final SoundEvent BUDDY_CLICK = register("block.buddy.click");
    public static final SoundEvent BLOOD_WOOD_MOAN = register("block.blood_wood_log.moan");
    public static final SoundEvent SOUL_CONVERSION = register("block.generic.soul_conversion");
    public static final SoundEvent MILL_STONE_GRIND = register("block.mill_stone.grind");
    public static final SoundEvent TURNTABLE_SETTING_CLICK = register("block.turntable.setting_click");
    public static final SoundEvent TURNTABLE_TURNING_CLICK = register("block.turntable.turning_click");
    public static final SoundEvent WOLF_DUNG_PRODUCTION = register("entity.wolf.dung.production");
    public static final SoundEvent WOLF_DUNG_EFFORT = register("entity.wolf.dung.effort");
    public static final SoundEvent DYNAMITE_THROW = register("entity.dynamite.throw");
    public static final SoundEvent SOUL_URN_THROW = register("entity.soul_urn.throw");
    public static final SoundEvent DYNAMITE_IGNITE = register("entity.dynamite.ignite");
    public static final SoundEvent MINING_CHARGE_PRIME = register("entity.mining_charge.prime");

    protected static SoundEvent register(String id) {
        return BwtSoundEvents.register(Id.of(id));
    }

    protected static SoundEvent register(ResourceLocation id) {
        return BwtSoundEvents.register(id, id);
    }

    protected static Holder.Reference<SoundEvent> registerReference(String id) {
        return BwtSoundEvents.registerReference(Id.mc(id));
    }

    protected static Holder.Reference<SoundEvent> registerReference(ResourceLocation id) {
        return BwtSoundEvents.registerReference(id, id);
    }

    protected static Holder.Reference<SoundEvent> registerReference(ResourceLocation id, ResourceLocation soundId) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }

    protected static SoundEvent register(ResourceLocation id, ResourceLocation soundId) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }

    @Override
    public void onInitialize() {

    }
}

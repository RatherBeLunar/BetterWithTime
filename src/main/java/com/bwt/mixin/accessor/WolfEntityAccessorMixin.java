package com.bwt.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Wolf.class)
public interface WolfEntityAccessorMixin {
    @Invoker("getDeathSound")
    SoundEvent bwt$getDeathSound();

    @Invoker("getSoundVariant")
    Holder<WolfSoundVariant> bwt$getSoundVariant();
}

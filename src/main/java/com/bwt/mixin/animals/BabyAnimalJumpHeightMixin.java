package com.bwt.mixin.animals;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class BabyAnimalJumpHeightMixin extends Entity {
    public BabyAnimalJumpHeightMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "getJumpBoostVelocityModifier", at = @At("RETURN"))
    public float bwt$shortenBabyJumpHeight(float original) {
        if (!((LivingEntity) ((Object) this) instanceof PassiveEntity passiveEntity) || !passiveEntity.isBaby()) {
            return original;
        }
        return original - 0.1f;
    }
}

package com.bwt.mixin.animals;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class BabyAnimalJumpHeightMixin extends Entity {
    public BabyAnimalJumpHeightMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyReturnValue(method = "getJumpBoostPower", at = @At("RETURN"))
    public float bwt$shortenBabyJumpHeight(float original) {
        if (!((LivingEntity) ((Object) this) instanceof AgeableMob passiveEntity) || !passiveEntity.isBaby()) {
            return original;
        }
        return original - 0.1f;
    }
}

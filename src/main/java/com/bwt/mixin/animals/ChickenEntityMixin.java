package com.bwt.mixin.animals;

import com.bwt.entities.GoToAndPickUpBreedingItemGoal;
import com.bwt.mixin.accessors.MobEntityAccessorMixin;
import net.minecraft.entity.passive.ChickenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin implements MobEntityAccessorMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    public void addGoal(CallbackInfo ci) {
        this.getGoalSelector().add(3, new GoToAndPickUpBreedingItemGoal((ChickenEntity) ((Object) this), 6, 1.5, 1));
    }
}
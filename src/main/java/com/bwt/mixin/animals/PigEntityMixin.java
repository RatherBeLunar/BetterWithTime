package com.bwt.mixin.animals;

import com.bwt.entities.GoToAndPickUpBreedingItemGoal;
import com.bwt.mixin.accessors.MobEntityAccessorMixin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pig.class)
public abstract class PigEntityMixin extends Animal implements MobEntityAccessorMixin {
    protected PigEntityMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    public void addGoal(CallbackInfo ci) {
        this.getGoalSelector().addGoal(3, new GoToAndPickUpBreedingItemGoal(this, 6, 1.5, 1));
    }
}

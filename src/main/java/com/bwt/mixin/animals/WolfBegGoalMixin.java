package com.bwt.mixin.animals;

import com.bwt.entities.WolfIsFedAccess;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WolfBegGoal.class)
public abstract class WolfBegGoalMixin {
    @Shadow
    @Final
    private WolfEntity wolf;

    @ModifyReturnValue(method = "canStart", at = @At("RETURN"))
    public boolean bwt$canStart(boolean original) {
        return original && !((WolfIsFedAccess) this.wolf).bwt$isFed();
    }

    @ModifyReturnValue(method = "shouldContinue", at = @At("RETURN"))
    public boolean bwt$shouldContinue(boolean original) {
        return original && !((WolfIsFedAccess) this.wolf).bwt$isFed();
    }
}

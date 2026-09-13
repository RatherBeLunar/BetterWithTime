package com.bwt.mixin.animals;

import com.bwt.entities.WolfIsFedAccess;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BegGoal.class)
public abstract class WolfBegGoalMixin {
    @Shadow
    @Final
    private Wolf wolf;

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"))
    public boolean bwt$canStart(boolean original) {
        return original && !((WolfIsFedAccess) this.wolf).bwt$isFed();
    }

    @ModifyReturnValue(method = "canContinueToUse", at = @At("RETURN"))
    public boolean bwt$shouldContinue(boolean original) {
        return original && !((WolfIsFedAccess) this.wolf).bwt$isFed();
    }
}

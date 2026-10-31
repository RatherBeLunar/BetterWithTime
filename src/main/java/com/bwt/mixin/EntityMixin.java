package com.bwt.mixin;

import com.bwt.blocks.BwtBlocks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyExpressionValue(
            method = "checkInsideBlocks(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;Lit/unimi/dsi/fastutil/longs/LongSet;I)I -> Lnet/minecraft/world/level/BlockGetter$BlockStepVisitor;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"
            )
    )
    boolean bwt$isLensBeamConsideredAirForEntityIntersection(boolean original, @Local BlockState blockState) {
        if (blockState.is(BwtBlocks.detectorLogicBlock)) {
            return false;
        }
        return original;
    }
}

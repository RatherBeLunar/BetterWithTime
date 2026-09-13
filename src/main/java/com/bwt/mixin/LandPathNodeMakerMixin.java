package com.bwt.mixin;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public class LandPathNodeMakerMixin {
    @Inject(method = "getPathTypeFromState", at = @At(value = "HEAD"), cancellable = true)
    private static void bwt$getCommonNodeType(BlockGetter level, BlockPos pos, CallbackInfoReturnable<PathType> cir) {
        if (level.getBlockState(pos).is(BwtBlocks.vineTrapBlock)) {
            cir.setReturnValue(PathType.TRAPDOOR);
        }
    }
}

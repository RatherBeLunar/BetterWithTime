package com.bwt.mixin.planter_functionality;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCanePlantOnMixin {
    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    public void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos.down()).isOf(BwtBlocks.soilPlanterBlock)) {
            cir.setReturnValue(true);
        }
    }
}

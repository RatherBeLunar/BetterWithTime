package com.bwt.mixin.planter_functionality;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class CropPlantOnMixin {
    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    public void canPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(floor.isIn(BwtBlockTags.CROPS_CAN_PLANT_ON));
    }
}

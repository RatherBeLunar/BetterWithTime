package com.bwt.mixin.planter_and_vase_functionality;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class CropPlantOnMixin {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    public void canPlantOnTop(BlockState floor, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(floor.is(BwtBlockTags.CROPS_CAN_PLANT_ON));
    }
}

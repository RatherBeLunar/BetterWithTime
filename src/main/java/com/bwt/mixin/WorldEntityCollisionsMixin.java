package com.bwt.mixin;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class WorldEntityCollisionsMixin {
    @Inject(method = "canPlace", at = @At(value = "HEAD"), cancellable = true)
    public void canPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (context instanceof BlockDispenserPlacementContext) {
            cir.setReturnValue(true);
        }
    }
}
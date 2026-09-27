package com.bwt.mixin;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class WorldEntityCollisionsMixin {
    @Inject(method = "canPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z"), cancellable = true)
    public void canPlace(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (context instanceof BlockDispenserPlacementContext) {
            cir.setReturnValue(true);
        }
    }
}
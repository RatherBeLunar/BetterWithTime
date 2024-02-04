package com.bwt.mixin;

import com.bwt.blocks.block_dispenser.BlockDispenserPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class WorldEntityCollisionsMixin {
    @Redirect(method = "canPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;canPlace(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Z"))
    public boolean canPlace(World instance, BlockState state, BlockPos pos, ShapeContext shapeContext) {
        if (shapeContext instanceof BlockDispenserPlacementContext) {
            return true;
        }
        return instance.canPlace(state, pos, shapeContext);
    }
}
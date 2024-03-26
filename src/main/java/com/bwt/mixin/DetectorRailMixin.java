package com.bwt.mixin;

import com.bwt.blocks.BwtBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailMixin {
    @ModifyVariable(method = "getCarts(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "HEAD"), index = 4, argsOnly = true)
    public Predicate<Entity> getCarts(Predicate<Entity> entityPredicate, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos) {
        if (world.getBlockState(pos).isOf(BwtBlocks.stoneDetectorRailBlock)) {
            return entityPredicate.and(entity -> entity.hasPassengers() || (entity instanceof AbstractMinecartEntity && !(entity instanceof MinecartEntity)));
        }
        else if (world.getBlockState(pos).isOf(BwtBlocks.obsidianDetectorRailBlock)) {
            return entityPredicate.and(Entity::hasPlayerRider);
        }
        return entityPredicate;
    }
}

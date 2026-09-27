package com.bwt.mixin;

import com.bwt.blocks.BwtBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailMixin {
    @ModifyVariable(method = "getInteractingMinecartOfType(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/lang/Class;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At(value = "HEAD"), argsOnly = true, name = "predicate")
    public Predicate<Entity> getCarts(Predicate<Entity> predicate, @Local(argsOnly = true, name = "level") Level level, @Local(argsOnly = true, name = "blockPos") BlockPos blockPos) {
        if (level.getBlockState(blockPos).is(BwtBlocks.stoneDetectorRailBlock)) {
            return predicate.and(entity -> entity.isVehicle() || (entity instanceof AbstractMinecart && !(entity instanceof Minecart)));
        }
        else if (level.getBlockState(blockPos).is(BwtBlocks.obsidianDetectorRailBlock)) {
            return predicate.and(Entity::hasExactlyOnePlayerPassenger);
        }
        return predicate;
    }
}

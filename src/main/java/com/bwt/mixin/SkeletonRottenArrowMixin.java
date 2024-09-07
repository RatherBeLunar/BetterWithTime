package com.bwt.mixin;

import com.bwt.items.BwtItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractSkeletonEntity.class)
public abstract class SkeletonRottenArrowMixin extends HostileEntity {
    protected SkeletonRottenArrowMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "shootAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/AbstractSkeletonEntity;getProjectileType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
            )
    )
    protected ItemStack rotArrows(ItemStack original) {
        if (original.isOf(Items.ARROW)) {
            return original.copyComponentsToNewStack(BwtItems.rottedArrowItem, original.getCount());
        }
        return original;
    }

    @Override
    protected void drop(ServerWorld serverWorld, DamageSource source) {
        super.drop(serverWorld, source);
    }
}

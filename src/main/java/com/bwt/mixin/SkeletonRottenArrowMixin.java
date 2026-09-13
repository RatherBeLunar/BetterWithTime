package com.bwt.mixin;

import com.bwt.items.BwtItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonRottenArrowMixin extends Monster {
    protected SkeletonRottenArrowMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "performRangedAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/AbstractSkeleton;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    protected ItemStack rotArrows(ItemStack original) {
        if (original.is(Items.ARROW)) {
            return original.transmuteCopy(BwtItems.rottedArrowItem, original.getCount());
        }
        return original;
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel serverLevel, DamageSource source) {
        super.dropAllDeathLoot(serverLevel, source);
    }
}

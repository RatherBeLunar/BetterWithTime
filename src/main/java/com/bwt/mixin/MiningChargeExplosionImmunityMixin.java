package com.bwt.mixin;

import com.bwt.blocks.mining_charge.MiningChargeExplosion;
import com.bwt.tags.BwtItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Explosion;

@Mixin(Entity.class)
public abstract class MiningChargeExplosionImmunityMixin {
    @Inject(method = "ignoreExplosion", at = @At("HEAD"), cancellable = true)
    public void bwt$isImmuneToExplosion(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        if (!(explosion instanceof MiningChargeExplosion)) {
            return;
        }
        if (!((Entity) ((Object) this) instanceof ItemEntity itemEntity)) {
            return;
        }
        if (itemEntity.getItem().is(BwtItemTags.MINING_CHARGE_IMMUNE)) {
            cir.setReturnValue(true);
            return;
        }
    }
}

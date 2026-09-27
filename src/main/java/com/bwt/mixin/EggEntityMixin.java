package com.bwt.mixin;

import com.bwt.items.BwtItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class EggEntityMixin extends ThrowableItemProjectile {
    @Unique
    protected boolean chickenSpawned;

    public EggEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.chickenSpawned = false;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    public void bwt$init1(Level level, LivingEntity owner, CallbackInfo ci) {
        this.chickenSpawned = false;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDD)V", at = @At("TAIL"))
    public void bwt$init2(Level level, double x, double y, double z, CallbackInfo ci) {
        this.chickenSpawned = false;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    public void bwt$init3(EntityType<ThrownEgg> entityType, Level level, CallbackInfo ci) {
        this.chickenSpawned = false;
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public void bwt$preventRawEggDrops(HitResult hitResult, CallbackInfo ci) {
        this.chickenSpawned = true;
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownEgg;discard()V"))
    public void bwt$spawnRawEgg(HitResult hitResult, CallbackInfo ci) {
        if (!this.chickenSpawned) {
            level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(BwtItems.rawEggItem)));
        }
    }
}

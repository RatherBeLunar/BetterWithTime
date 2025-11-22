package com.bwt.mixin;

import com.bwt.utils.DragonOrbHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    @Shadow protected int playerHitTimer;
    @Shadow public abstract int getXpToDrop(ServerWorld world, @Nullable Entity attacker);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "shouldAlwaysDropXp", at = @At("HEAD"), cancellable = true)
    private void setMobsAlwaysDropXP(CallbackInfoReturnable<Boolean> cir) {
        // Compat with Mobs Always Drop - Not sure if it's necessary. Mobs Always Drop has the same injection point
        if (FabricLoader.getInstance().isModLoaded("mobs_always_drop")) return;

        cir.setReturnValue(true);
    }

    // Spawn a dragon orb
    // We inject before the normal spawning of orbs with an early playerHitTimer check
    @Inject(method = "dropXp", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"),
            cancellable = true
    )
    private void beforeSpawn(Entity attacker, CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld serverWorld && playerHitTimer <= 0) {
            DragonOrbHelper.spawn(serverWorld, this.getPos(), this.getXpToDrop(serverWorld, attacker));
            ci.cancel();
        }
    }
}
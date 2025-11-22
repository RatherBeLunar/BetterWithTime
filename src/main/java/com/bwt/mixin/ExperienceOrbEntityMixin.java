package com.bwt.mixin;

import com.bwt.data.BwtDataAttachments;
import com.bwt.data.DragonOrbData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity
{

    public ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        this.getAttachedOrCreate(BwtDataAttachments.dragonOrbData, () -> new DragonOrbData(false));
    }

    // Disable any kind of regular updates for dragon orbs
    @Inject(method = "expensiveUpdate", at = @At("HEAD"), cancellable = true)
    private void onMerge(CallbackInfo ci) {
        var data = this.getAttached(BwtDataAttachments.dragonOrbData);
        if (data != null && data.isValid()) {
            ci.cancel();
        }
    }

    // Change pickup behavior for dragon orbs
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPickup(PlayerEntity player, CallbackInfo ci) {
        var data = this.getAttached(BwtDataAttachments.dragonOrbData);
        if (data != null && data.isValid()) {
            ci.cancel();
        }
    }
}
package com.bwt.mixin.mining_charge;

import com.bwt.entities.MiningChargeEntity;
import com.bwt.utils.RadiusAroundBlockStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(ServerExplosion.class)
public abstract class MiningChargeExplosionFunctionalityMixin {
    @Shadow
    @Final
    private DamageSource damageSource;

    @Accessor
    abstract ServerLevel getLevel();

    @Accessor
    abstract ExplosionDamageCalculator getDamageCalculator();

    @Inject(
            method = "calculateExplodedPositions",
            at = @At("HEAD"),
            cancellable = true
    )
    void bwt$calculateMiningChargeExplodedPositions(CallbackInfoReturnable<List<BlockPos>> cir) {
        ServerExplosion serverExplosion = (ServerExplosion) (Object) this;
        if (serverExplosion.getDirectSourceEntity() instanceof MiningChargeEntity miningChargeEntity) {
            Set<BlockPos> explodedPositions = getMiningChargeExplodedPositions(miningChargeEntity, serverExplosion);
            cir.setReturnValue(new ObjectArrayList<>(explodedPositions));
        }
    }

    @Unique
    Set<BlockPos> getMiningChargeExplodedPositions(MiningChargeEntity miningChargeEntity, ServerExplosion serverExplosion) {
        Set<BlockPos> affectedBlocks = new HashSet<>();
        ServerLevel level = getLevel();
        ExplosionDamageCalculator damageCalculator = getDamageCalculator();

        BlockPos entityBlockPos = BlockPos.containing(serverExplosion.center());

        if (!canDestroyBlock(level, serverExplosion, damageCalculator, entityBlockPos)) {
            // we are in a block that's too tough to destroy.  Abort.
            return affectedBlocks;
        }

        // offset the blast so that it is centered on the block to which we are attached
        BlockPos targetPos = entityBlockPos.relative(miningChargeEntity.getNearestViewDirection().getOpposite());

        if (!canDestroyBlock(level, serverExplosion, damageCalculator, targetPos)) {
            // we are attached to a block that's too tough to destroy.  Center the blast on the charge's
            // position
            targetPos = entityBlockPos;
        }

        RadiusAroundBlockStream
                .allBlocksInRadius(targetPos, 1)
                .filter(pos -> canDestroyBlock(level, serverExplosion, damageCalculator, pos))
                .forEach(affectedBlocks::add);

        // resolve the extra block of penetration towards our facing.

        targetPos = targetPos.relative(miningChargeEntity.getNearestViewDirection().getOpposite());

        if (!canDestroyBlock(level, serverExplosion, damageCalculator, targetPos)) {
            // the block between the source and extra block is too tough, abort
            return affectedBlocks;
        }

        targetPos = targetPos.relative(miningChargeEntity.getNearestViewDirection().getOpposite());

        if (canDestroyBlock(level, serverExplosion, damageCalculator, targetPos)) {
            affectedBlocks.add(targetPos);
        }

        return affectedBlocks;
    }

    @Unique
    protected boolean canDestroyBlock(ServerLevel level, ServerExplosion explosion, ExplosionDamageCalculator damageCalculator, BlockPos pos) {
        if (!level.isInWorldBounds(pos)) {
            return false;
        }
        BlockState blockState = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);
        float power = explosion.radius() - damageCalculator.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState)
                .map(val -> (val + 0.3f) * 0.3f)
                .orElse(0f);
        return power > 0.0f && damageCalculator.shouldBlockExplode(explosion, level, pos, blockState, power);
    }
}

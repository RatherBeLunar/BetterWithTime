package com.bwt.blocks.mining_charge;

import com.bwt.entities.BwtEntities;
import com.bwt.entities.MiningChargeEntity;
import com.bwt.utils.RadiusAroundBlockStream;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MiningChargeExplosion extends ServerExplosion {
    public static final LootItemCondition LOOT_CONDITION = LootItemEntityPropertyCondition.hasProperties(
            LootContext.EntityTarget.THIS,
            EntityPredicate.Builder.entity().of(BwtEntities.miningChargeEntity)
    ).build();

    protected final ServerLevel level;
    protected final ExplosionDamageCalculator behavior;
    protected final MiningChargeEntity miningChargeEntity;
    protected final DamageSource damageSource;

    public MiningChargeExplosion(
            ServerLevel serverLevel,
            @Nullable MiningChargeEntity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator explosionDamageCalculator,
            Vec3 vec3,
            float power,
            boolean fire
    ) {
        super(serverLevel, entity, damageSource, explosionDamageCalculator, vec3, power, fire, Level.ExplosionInteraction.TNT);
        this.level = serverLevel;
        this.behavior = explosionDamageCalculator;
        this.miningChargeEntity = entity;
        this.damageSource = damageSource;
    }

    public void explode() {
        super.explode();
        getToBlow().clear();
        damageBlocks();
    }

    protected void damageBlocks() {
        BlockPos entityBlockPos = BlockPos.containing(center());

        if (!canDestroyBlock(entityBlockPos)) {
            // we are in a block that's too tough to destroy.  Abort.
            return;
        }

        // offset the blast so that it is centered on the block to which we are attached
        BlockPos targetPos = entityBlockPos.relative(miningChargeEntity.getNearestViewDirection().getOpposite());

        if (canDestroyBlock(targetPos)) {
            // we are attached to a block that's too tough to destroy.  Center the blast on the charge's
            // position
            targetPos = BlockPos.containing(center());
        }

        List<BlockPos> affectedBlocks = getToBlow();
        RadiusAroundBlockStream
                .allBlocksInRadius(targetPos, 1)
                .filter(this::canDestroyBlock)
                .forEach(affectedBlocks::add);

        // resolve the extra block of penetration towards our facing.

        targetPos = targetPos.relative(miningChargeEntity.getNearestViewDirection().getOpposite());

        if (!canDestroyBlock(targetPos)) {
            // the block between the source and extra block is too tough, abort
            return;
        }

        targetPos = targetPos.relative(miningChargeEntity.getNearestViewDirection().getOpposite());

        if (canDestroyBlock(targetPos)) {
            // the block between the source and extra block is too tough, abort
            getToBlow().add(targetPos);
        }
    }

    protected boolean canDestroyBlock(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        float power = radius() - behavior.getBlockExplosionResistance(this, level, pos, state, state.getFluidState())
                .map(val -> (val + 0.3f) * 0.3f)
                .orElse(0f);
        return power > 0.0f && this.behavior.shouldBlockExplode(this, this.level, pos, state, power);
    }
}


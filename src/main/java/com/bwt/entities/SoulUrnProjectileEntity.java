package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SoulUrnProjectileEntity extends ThrowableItemProjectile {
    private static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

    public SoulUrnProjectileEntity(EntityType<? extends SoulUrnProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SoulUrnProjectileEntity(Level level, LivingEntity owner) {
        super(BwtEntities.soulUrnProjectileEntity, owner, level);
    }

    public SoulUrnProjectileEntity(Level level, double x, double y, double z) {
        super(BwtEntities.soulUrnProjectileEntity, x, y, z, level);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.DEATH) {
            for (int i = 0; i < 8; i++) {
                this.level()
                        .addParticle(
                                new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                ((double)this.random.nextFloat() - 0.5) * 0.08,
                                ((double)this.random.nextFloat() - 0.5) * 0.08,
                                ((double)this.random.nextFloat() - 0.5) * 0.08
                        );
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level().isClientSide) {
            return;
        }
        if (this.level().getDifficulty().equals(Difficulty.PEACEFUL)) {
            return;
        }

        Ghast ghastEntity = EntityType.GHAST.create(this.level());
        if (ghastEntity != null) {
            float yaw = (this.getYRot() + 180.0F) % 360F;
            ghastEntity.moveTo(this.getX(), this.getY(), this.getZ(), yaw, 0.0F);
            if (!ghastEntity.fudgePositionAfterSizeChange(EMPTY_DIMENSIONS)) {
                return;
            }

            this.level().addFreshEntity(ghastEntity);
        }
        this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return BwtItems.soulUrnItem;
    }
}

package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class SoulUrnProjectileEntity extends ThrownItemEntity {
    private static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

    public SoulUrnProjectileEntity(EntityType<? extends SoulUrnProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SoulUrnProjectileEntity(World world, LivingEntity owner) {
        super(BwtEntities.soulUrnProjectileEntity, owner, world);
    }

    public SoulUrnProjectileEntity(World world, double x, double y, double z) {
        super(BwtEntities.soulUrnProjectileEntity, x, y, z, world);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            for (int i = 0; i < 8; i++) {
                this.getWorld()
                        .addParticle(
                                new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
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
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld().isClient) {
            return;
        }
        if (this.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
            return;
        }

        GhastEntity ghastEntity = EntityType.GHAST.create(this.getWorld());
        if (ghastEntity != null) {
            float yaw = (this.getYaw() + 180.0F) % 360F;
            ghastEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), yaw, 0.0F);
            if (!ghastEntity.recalculateDimensions(EMPTY_DIMENSIONS)) {
                return;
            }

            this.getWorld().spawnEntity(ghastEntity);
        }
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return BwtItems.soulUrnItem;
    }
}

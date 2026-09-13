package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RottedArrowEntity extends AbstractArrow {
    private static final ItemStack DEFAULT_STACK = new ItemStack(BwtItems.rottedArrowItem);

    public RottedArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return BwtItems.broadheadArrowItem.getDefaultInstance();
    }

    public RottedArrowEntity(Level level, double x, double y, double z, ItemStack stack, @Nullable ItemStack weapon) {
        super(BwtEntities.rottedArrowEntity, x, y, z, level, stack, weapon);
    }

    public RottedArrowEntity(Level level, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BwtEntities.rottedArrowEntity, owner, level, stack, shotFrom);
    }

    public void initFromStack(ItemStack stack) {
        setBaseDamage(super.getBaseDamage() / 2);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
        this.discard();
    }

    @Override
    public void handleEntityEvent(byte status) {
        super.handleEntityEvent(status);
        if (status == EntityEvent.DEATH) {
            for (int i = 0; i < 8; ++i) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), DEFAULT_STACK.getBreakingSound(), this.getSoundSource(), 0.13f, 0.8f + this.level().random.nextFloat() * 0.4f, false);
                this.level().addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, DEFAULT_STACK),
                        this.getX(), this.getY(), this.getZ(),
                        ((double)this.random.nextFloat() - 0.5) * 0.08,
                        ((double)this.random.nextFloat() - 0.5) * 0.08,
                        ((double)this.random.nextFloat() - 0.5) * 0.08
                );
            }
        }
    }
}

package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RottedArrowEntity extends PersistentProjectileEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(BwtItems.rottedArrowItem);

    public RottedArrowEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return BwtItems.broadheadArrowItem.getDefaultStack();
    }

    public RottedArrowEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack weapon) {
        super(BwtEntities.rottedArrowEntity, x, y, z, world, stack, weapon);
    }

    public RottedArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BwtEntities.rottedArrowEntity, owner, world, stack, shotFrom);
    }

    public void initFromStack(ItemStack stack) {
        setDamage(super.getDamage() / 2);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
        this.discard();
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            for (int i = 0; i < 8; ++i) {
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), DEFAULT_STACK.getBreakSound(), this.getSoundCategory(), 0.13f, 0.8f + this.getWorld().random.nextFloat() * 0.4f, false);
                this.getWorld().addParticle(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, DEFAULT_STACK),
                        this.getX(), this.getY(), this.getZ(),
                        ((double)this.random.nextFloat() - 0.5) * 0.08,
                        ((double)this.random.nextFloat() - 0.5) * 0.08,
                        ((double)this.random.nextFloat() - 0.5) * 0.08
                );
            }
        }
    }
}

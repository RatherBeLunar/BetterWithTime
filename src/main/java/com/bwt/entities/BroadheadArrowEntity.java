package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BroadheadArrowEntity extends PersistentProjectileEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(BwtItems.broadheadArrowItem);

    public BroadheadArrowEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world, DEFAULT_STACK);
    }

    public BroadheadArrowEntity(World world, double x, double y, double z, ItemStack stack) {
        super(BwtEntities.broadheadArrowEntity, x, y, z, world, stack);
    }

    public BroadheadArrowEntity(World world, LivingEntity owner, ItemStack stack) {
        super(BwtEntities.broadheadArrowEntity, owner, world, stack);
    }

    public void initFromStack(ItemStack stack) {
        setDamage(super.getDamage() * 2);
    }
}

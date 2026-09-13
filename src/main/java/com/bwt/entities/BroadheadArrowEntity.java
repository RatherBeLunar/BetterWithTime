package com.bwt.entities;

import com.bwt.items.BwtItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BroadheadArrowEntity extends AbstractArrow {

    public BroadheadArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return BwtItems.broadheadArrowItem.getDefaultInstance();
    }

    public BroadheadArrowEntity(Level level, double x, double y, double z, ItemStack stack, @Nullable ItemStack weapon) {
        super(BwtEntities.broadheadArrowEntity, x, y, z, level, stack, weapon);
    }

    public BroadheadArrowEntity(Level level, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BwtEntities.broadheadArrowEntity, owner, level, stack, shotFrom);
    }

    public void initFromStack(ItemStack stack) {
        setBaseDamage(super.getBaseDamage() * 2);
    }
}

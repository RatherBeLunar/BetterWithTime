package com.bwt.items;

import com.bwt.entities.BroadheadArrowEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BroadheadArrowItem extends ArrowItem implements ProjectileItem {
    public BroadheadArrowItem(Item.Properties settings) {
        super(settings);
    }

    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        BroadheadArrowEntity arrowEntity = new BroadheadArrowEntity(level, shooter, stack.copyWithCount(1), shotFrom);
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction) {
        BroadheadArrowEntity broadheadArrowEntity = new BroadheadArrowEntity(level, position.x(), position.y(), position.z(), stack.copyWithCount(1), null);
        broadheadArrowEntity.pickup = AbstractArrow.Pickup.ALLOWED;
        return broadheadArrowEntity;
    }
}

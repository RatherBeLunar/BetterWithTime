package com.bwt.items;

import com.bwt.entities.BroadheadArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BroadheadArrowItem extends ArrowItem implements ProjectileItem {
    public BroadheadArrowItem(Item.Settings settings) {
        super(settings);
    }

    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        BroadheadArrowEntity arrowEntity = new BroadheadArrowEntity(world, shooter, stack.copyWithCount(1), shotFrom);
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position position, ItemStack stack, Direction direction) {
        BroadheadArrowEntity broadheadArrowEntity = new BroadheadArrowEntity(world, position.getX(), position.getY(), position.getZ(), stack.copyWithCount(1), null);
        broadheadArrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        return broadheadArrowEntity;
    }
}

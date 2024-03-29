package com.bwt.items;

import com.bwt.entities.BroadheadArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BroadheadArrowItem extends ArrowItem {
    public BroadheadArrowItem(Settings settings) {
        super(settings);
    }

    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        BroadheadArrowEntity arrowEntity = new BroadheadArrowEntity(world, shooter, stack.copyWithCount(1));
        arrowEntity.initFromStack(stack);
        return arrowEntity;
    }
}

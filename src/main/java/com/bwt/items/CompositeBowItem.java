package com.bwt.items;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.function.Predicate;

public class CompositeBowItem extends BowItem {
    public CompositeBowItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    protected int getDurabilityUse(ItemStack projectile) {
        return super.getDurabilityUse(projectile) * 2;
    }

    @Override
    public int getDefaultProjectileRange() {
        return super.getDefaultProjectileRange() * 2;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return super.getAllSupportedProjectiles().and(itemStack -> !itemStack.is(BwtItems.rottedArrowItem));
    }
}

package com.bwt.items;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class CompositeBowItem extends BowItem {
    public CompositeBowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    protected int getWeaponStackDamage(ItemStack projectile) {
        return super.getWeaponStackDamage(projectile) * 2;
    }

    @Override
    public int getRange() {
        return super.getRange() * 2;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return super.getProjectiles().and(itemStack -> !itemStack.isOf(BwtItems.rottedArrowItem));
    }
}

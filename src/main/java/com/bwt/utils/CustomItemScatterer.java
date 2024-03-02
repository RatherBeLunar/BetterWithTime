package com.bwt.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomItemScatterer extends ItemScatterer {
    public static void spawn(World world, BlockPos pos, Inventory inventory) {
        CustomItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void spawn(World world, Entity entity, Inventory inventory) {
        CustomItemScatterer.spawn(world, entity.getX(), entity.getY(), entity.getZ(), inventory);
    }

    private static void spawn(World world, double x, double y, double z, Inventory inventory) {
        for (int i = 0; i < inventory.size(); ++i) {
            CustomItemScatterer.spawn(world, x, y, z, inventory.getStack(i));
        }
    }

    public static void spawn(World world, BlockPos pos, DefaultedList<ItemStack> stacks) {
        stacks.forEach(stack -> CustomItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack));
    }

    public static void spawn(World world, double x, double y, double z, ItemStack stack) {
        x += world.random.nextDouble() * 0.7F + 0.15F;
        y += world.random.nextDouble() * 0.2F + 0.1F;
        z += world.random.nextDouble() * 0.7F + 0.15F;
        float velocityFactor = 0.05F;

        while (!stack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack.split(world.random.nextInt(1) + 1));

            itemEntity.setPickupDelay(10);
            itemEntity.setVelocity(
                    (float)world.random.nextGaussian() * velocityFactor,
                    (float)world.random.nextGaussian() * velocityFactor + 0.2F,
                    (float)world.random.nextGaussian() * velocityFactor
            );
            world.spawnEntity(itemEntity);
        }
    }
}

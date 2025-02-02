package com.bwt.items;

import com.bwt.entities.SoulUrnProjectileEntity;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class SoulUrnItem extends Item implements ProjectileItem {
    public SoulUrnItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(), BwtSoundEvents.SOUL_URN_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!world.isClient) {
            SoulUrnProjectileEntity soulUrnProjectileEntity = new SoulUrnProjectileEntity(world, user);
            soulUrnProjectileEntity.setItem(itemStack);
            soulUrnProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            soulUrnProjectileEntity.refreshPositionAndAngles(user.getPos(), user.getYaw(), 0.0F);
            world.spawnEntity(soulUrnProjectileEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        SoulUrnProjectileEntity soulUrnProjectileEntity = new SoulUrnProjectileEntity(world, pos.getX(), pos.getY(), pos.getZ());
        soulUrnProjectileEntity.setItem(stack);
        soulUrnProjectileEntity.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), direction.asRotation(), 0f);
        return soulUrnProjectileEntity;
    }
}

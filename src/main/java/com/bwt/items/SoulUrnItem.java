package com.bwt.items;

import com.bwt.entities.SoulUrnProjectileEntity;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class SoulUrnItem extends Item implements ProjectileItem {
    public SoulUrnItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        level.playSound(
                null, user.getX(), user.getY(), user.getZ(), BwtSoundEvents.SOUL_URN_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!level.isClientSide) {
            SoulUrnProjectileEntity soulUrnProjectileEntity = new SoulUrnProjectileEntity(level, user);
            soulUrnProjectileEntity.setItem(itemStack);
            soulUrnProjectileEntity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(soulUrnProjectileEntity);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        SoulUrnProjectileEntity soulUrnProjectileEntity = new SoulUrnProjectileEntity(level, pos.x(), pos.y(), pos.z());
        soulUrnProjectileEntity.setItem(stack);
        soulUrnProjectileEntity.moveTo(pos.x(), pos.y(), pos.z(), direction.toYRot(), 0f);
        return soulUrnProjectileEntity;
    }
}

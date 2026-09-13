package com.bwt.items;

import com.bwt.entities.DynamiteEntity;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class DynamiteItem extends Item implements ProjectileItem {
    public DynamiteItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), BwtSoundEvents.DYNAMITE_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
        user.getCooldowns().addCooldown(this, 20);
        if (!level.isClientSide && user instanceof ServerPlayer serverUser) {
            DynamiteEntity dynamiteEntity = new DynamiteEntity(level, user);
            dynamiteEntity.setItem(itemStack);
            dynamiteEntity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, 1.0f, 1.0f);

            for (int i = 0; i < user.getInventory().getContainerSize(); ++i) {
                ItemStack otherStack = user.getInventory().getItem(i);
                if (!otherStack.is(Items.FLINT_AND_STEEL)) {
                    continue;
                }
                if (!serverUser.getAbilities().instabuild) {
                    otherStack.hurtAndBreak(1, user, Player.getSlotForHand(user.getUsedItemHand()));
                }
                dynamiteEntity.ignite();
                break;
            }

            level.addFreshEntity(dynamiteEntity);
        }
        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        DynamiteEntity dynamiteEntity = new DynamiteEntity(pos.x(), pos.y(), pos.z(), level);
        dynamiteEntity.setItem(stack);
        dynamiteEntity.ignite();
        return dynamiteEntity;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .power(1.0f)
                .uncertainty(1.0f)
                .build();
    }
}

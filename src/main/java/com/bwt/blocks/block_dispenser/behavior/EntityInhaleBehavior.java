package com.bwt.blocks.block_dispenser.behavior;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public interface EntityInhaleBehavior {
    default ItemStack getInhaledItems(Entity entity) {
        return ItemStack.EMPTY;
    }
    default DefaultedList<ItemStack> getDroppedItems(Entity entity) {
        return DefaultedList.of();
    }

    void inhale(Entity entity);

    static void registerBehaviors() {
        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.WOLF, new EntityInhaleBehavior() {
            Text name;
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof WolfEntity wolf)) {
                    return;
                }
                wolf.remove(Entity.RemovalReason.KILLED);
                wolf.playSound(SoundEvents.ENTITY_WOLF_DEATH, 0.4f, (wolf.getRandom().nextFloat() - wolf.getRandom().nextFloat()) * 0.2f + 1.0f);
                name = wolf.getCustomName();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(BwtBlocks.companionCubeBlock);
                if (name != null) {
                    itemStack.setCustomName(name);
                }
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.STRING), new ItemStack(Items.STRING));
            }
        });
    }
}

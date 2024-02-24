package com.bwt.blocks.block_dispenser.behavior;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.google.common.collect.Maps;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;

public interface EntityInhaleBehavior {
    default ItemStack getInhaledItems(Entity entity) {
        return ItemStack.EMPTY;
    }
    default DefaultedList<ItemStack> getDroppedItems(Entity entity) {
        return DefaultedList.of();
    }

    default boolean canInhale(Entity entity) {
        return true;
    }
    void inhale(Entity entity);

    EntityInhaleBehavior NOOP = new EntityInhaleBehavior() {
        @Override
        public boolean canInhale(Entity entity) {return false;}
        @Override
        public void inhale(Entity entity) {}
    };

    static void registerBehaviors() {
        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.WOLF, new EntityInhaleBehavior() {
            Text name;

            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof WolfEntity wolf) && !wolf.isBaby();
            }

            @Override
            public void inhale(Entity entity) {
                WolfEntity wolf = ((WolfEntity) entity);
                wolf.remove(Entity.RemovalReason.KILLED);
                wolf.playSound(SoundEvents.ENTITY_WOLF_DEATH, 0.4f, wolf.getSoundPitch());
                name = wolf.getCustomName();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(BwtBlocks.companionCubeBlock);
                if (name != null) {
                    itemStack.setCustomName(name);
                    name = null;
                }
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.STRING), new ItemStack(Items.STRING));
            }
        });
        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.CHICKEN, new EntityInhaleBehavior() {
            Text name;

            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof ChickenEntity chicken) && !chicken.isBaby();
            }

            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof ChickenEntity chicken)) {
                    return;
                }
                chicken.remove(Entity.RemovalReason.KILLED);
                chicken.playSound(SoundEvents.ENTITY_CHICKEN_DEATH, 0.4f, chicken.getSoundPitch());
                name = chicken.getCustomName();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(Items.EGG);
                if (name != null) {
                    itemStack.setCustomName(name);
                    name = null;
                }
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.FEATHER));
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.SHEEP, new EntityInhaleBehavior() {
            private static final Map<DyeColor, ItemConvertible> DROPS = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
                map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
                map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
                map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
                map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
                map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
                map.put(DyeColor.LIME, Blocks.LIME_WOOL);
                map.put(DyeColor.PINK, Blocks.PINK_WOOL);
                map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
                map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
                map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
                map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
                map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
                map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
                map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
                map.put(DyeColor.RED, Blocks.RED_WOOL);
                map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
            });

            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof SheepEntity sheep) && sheep.isShearable();
            }

            @Override
            public void inhale(Entity entity) {
                SheepEntity sheep = ((SheepEntity) entity);
                sheep.playSound(SoundEvents.ENTITY_SHEEP_HURT, 0.4f, sheep.getSoundPitch());
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                return new ItemStack(DROPS.get(((SheepEntity) entity).getColor()).asItem());
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                ((SheepEntity) entity).setSheared(true);
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.STRING));
            }
        });
    }
}

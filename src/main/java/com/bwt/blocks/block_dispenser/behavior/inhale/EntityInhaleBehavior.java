package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.mixin.accessors.ArmorStandAccessorMixin;
import com.bwt.utils.DyeUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;

import java.util.stream.Stream;

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
            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof WolfEntity wolf) && !wolf.isBaby();
            }

            @Override
            public void inhale(Entity entity) {
                WolfEntity wolf = ((WolfEntity) entity);
                wolf.remove(Entity.RemovalReason.KILLED);
                wolf.playSound(SoundEvents.ENTITY_WOLF_DEATH, 0.4f, wolf.getSoundPitch());
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(BwtBlocks.companionCubeBlock);
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.STRING), new ItemStack(Items.STRING));
            }
        });
        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.CHICKEN, new EntityInhaleBehavior() {
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
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(Items.EGG);
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.FEATHER));
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.SHEEP, new EntityInhaleBehavior() {
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
                return new ItemStack(DyeUtils.WOOL_COLORS.get(((SheepEntity) entity).getColor()).asItem());
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                ((SheepEntity) entity).setSheared(true);
                return DefaultedList.copyOf(ItemStack.EMPTY, new ItemStack(Items.STRING));
            }
        });

        EntityInhaleBehavior minecartBehavior = new EntityInhaleBehavior() {
            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof AbstractMinecartEntity minecart) && minecart.isAlive();
            }

            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof AbstractMinecartEntity minecart)) {
                    return;
                }
                minecart.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickBlockStack()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }
        };
        Stream.of(
                EntityType.MINECART,
                EntityType.CHEST_MINECART,
                EntityType.COMMAND_BLOCK_MINECART,
                EntityType.COMMAND_BLOCK_MINECART,
                EntityType.FURNACE_MINECART,
                EntityType.HOPPER_MINECART,
                EntityType.TNT_MINECART
        ).forEach(minecart -> BlockDispenserBlock.registerEntityInhaleBehavior(minecart, minecartBehavior));


        EntityInhaleBehavior boatBehavior = new EntityInhaleBehavior() {
            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof BoatEntity boat) && boat.isAlive();
            }

            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof BoatEntity boat)) {
                    return;
                }
                boat.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickBlockStack()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }
        };
        Stream.of(EntityType.BOAT, EntityType.CHEST_BOAT).forEach(boat -> BlockDispenserBlock.registerEntityInhaleBehavior(boat, boatBehavior));

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.ARMOR_STAND, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof ArmorStandEntity armorStand)) {
                    return;
                }
                armorStand.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickBlockStack()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                if (!(entity instanceof ArmorStandEntity armorStand)) {
                    return DefaultedList.of();
                }
                DefaultedList<ItemStack> heldItems = ((ArmorStandAccessorMixin) armorStand).getHeldItems();
                DefaultedList<ItemStack> armorItems = ((ArmorStandAccessorMixin) armorStand).getArmorItems();
                DefaultedList<ItemStack> returnItems = DefaultedList.of();
                returnItems.addAll(heldItems);
                returnItems.addAll(armorItems);
                return returnItems;
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.ITEM_FRAME, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof ItemFrameEntity itemFrame)) {
                    return;
                }
                itemFrame.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickBlockStack()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                if (!(entity instanceof ItemFrameEntity itemFrame)) {
                    return DefaultedList.of();
                }
                DefaultedList<ItemStack> returnItems = DefaultedList.of();
                returnItems.add(itemFrame.getHeldItemStack());
                return returnItems;
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.GLOW_ITEM_FRAME, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof GlowItemFrameEntity itemFrame)) {
                    return;
                }
                itemFrame.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickBlockStack()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }

            @Override
            public DefaultedList<ItemStack> getDroppedItems(Entity entity) {
                if (!(entity instanceof GlowItemFrameEntity itemFrame)) {
                    return DefaultedList.of();
                }
                DefaultedList<ItemStack> returnItems = DefaultedList.of();
                returnItems.add(itemFrame.getHeldItemStack());
                return returnItems;
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.PAINTING, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof PaintingEntity painting)) {
                    return;
                }
                painting.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickBlockStack()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.setCustomName(entity.getCustomName());
                return itemStack;
            }
        });
    }
}

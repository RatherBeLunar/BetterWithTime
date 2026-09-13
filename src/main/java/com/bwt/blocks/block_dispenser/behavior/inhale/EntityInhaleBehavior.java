package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.mixin.accessors.ArmorStandAccessorMixin;
import com.bwt.utils.DyeUtils;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface EntityInhaleBehavior {
    default ItemStack getInhaledItems(Entity entity) {
        return ItemStack.EMPTY;
    }
    default NonNullList<ItemStack> getDroppedItems(Entity entity) {
        return NonNullList.create();
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
                return (entity instanceof Wolf wolf) && !wolf.isBaby();
            }

            @Override
            public void inhale(Entity entity) {
                Wolf wolf = ((Wolf) entity);
                wolf.remove(Entity.RemovalReason.KILLED);
                wolf.playSound(SoundEvents.WOLF_DEATH, 0.4f, wolf.getVoicePitch());
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(BwtBlocks.companionCubeBlock);
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }

            @Override
            public NonNullList<ItemStack> getDroppedItems(Entity entity) {
                return NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.STRING), new ItemStack(Items.STRING));
            }
        });
        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.CHICKEN, new EntityInhaleBehavior() {
            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof Chicken chicken) && !chicken.isBaby();
            }

            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof Chicken chicken)) {
                    return;
                }
                chicken.remove(Entity.RemovalReason.KILLED);
                chicken.playSound(SoundEvents.CHICKEN_DEATH, 0.4f, chicken.getVoicePitch());
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = new ItemStack(Items.EGG);
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }

            @Override
            public NonNullList<ItemStack> getDroppedItems(Entity entity) {
                return NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.FEATHER));
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.SHEEP, new EntityInhaleBehavior() {
            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof Sheep sheep) && sheep.readyForShearing();
            }

            @Override
            public void inhale(Entity entity) {
                Sheep sheep = ((Sheep) entity);
                sheep.playSound(SoundEvents.SHEEP_HURT, 0.4f, sheep.getVoicePitch());
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                return new ItemStack(DyeUtils.WOOL_COLORS.get(((Sheep) entity).getColor()).asItem());
            }

            @Override
            public NonNullList<ItemStack> getDroppedItems(Entity entity) {
                ((Sheep) entity).setSheared(true);
                return NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.STRING));
            }
        });

        EntityInhaleBehavior minecartBehavior = new EntityInhaleBehavior() {
            @Override
            public boolean canInhale(Entity entity) {
                return (entity instanceof AbstractMinecart minecart) && minecart.isAlive();
            }

            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof AbstractMinecart minecart)) {
                    return;
                }
                minecart.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickResult()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
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
                return (entity instanceof Boat boat) && boat.isAlive();
            }

            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof Boat boat)) {
                    return;
                }
                boat.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickResult()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }
        };
        Stream.of(EntityType.BOAT, EntityType.CHEST_BOAT).forEach(boat -> BlockDispenserBlock.registerEntityInhaleBehavior(boat, boatBehavior));

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.ARMOR_STAND, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof ArmorStand armorStand)) {
                    return;
                }
                armorStand.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickResult()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }

            @Override
            public NonNullList<ItemStack> getDroppedItems(Entity entity) {
                if (!(entity instanceof ArmorStand armorStand)) {
                    return NonNullList.create();
                }
                NonNullList<ItemStack> heldItems = ((ArmorStandAccessorMixin) armorStand).getHandItems();
                NonNullList<ItemStack> armorItems = ((ArmorStandAccessorMixin) armorStand).getArmorItems();
                NonNullList<ItemStack> returnItems = NonNullList.create();
                returnItems.addAll(heldItems);
                returnItems.addAll(armorItems);
                return returnItems;
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.ITEM_FRAME, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof ItemFrame itemFrame)) {
                    return;
                }
                itemFrame.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickResult()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }

            @Override
            public NonNullList<ItemStack> getDroppedItems(Entity entity) {
                if (!(entity instanceof ItemFrame itemFrame)) {
                    return NonNullList.create();
                }
                NonNullList<ItemStack> returnItems = NonNullList.create();
                returnItems.add(itemFrame.getItem());
                return returnItems;
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.GLOW_ITEM_FRAME, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof GlowItemFrame itemFrame)) {
                    return;
                }
                itemFrame.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickResult()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }

            @Override
            public NonNullList<ItemStack> getDroppedItems(Entity entity) {
                if (!(entity instanceof GlowItemFrame itemFrame)) {
                    return NonNullList.create();
                }
                NonNullList<ItemStack> returnItems = NonNullList.create();
                returnItems.add(itemFrame.getItem());
                return returnItems;
            }
        });

        BlockDispenserBlock.registerEntityInhaleBehavior(EntityType.PAINTING, new EntityInhaleBehavior() {
            @Override
            public void inhale(Entity entity) {
                if (!(entity instanceof Painting painting)) {
                    return;
                }
                painting.kill();
            }

            @Override
            public ItemStack getInhaledItems(Entity entity) {
                ItemStack itemStack = (itemStack = entity.getPickResult()) == null ? ItemStack.EMPTY : itemStack;
                itemStack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
                return itemStack;
            }
        });
    }
}

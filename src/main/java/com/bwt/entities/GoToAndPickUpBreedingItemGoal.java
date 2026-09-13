package com.bwt.entities;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class GoToAndPickUpBreedingItemGoal extends Goal {
    protected final Animal animal;
    protected final double searchRadius;
    protected final double pickupRadius;
    protected final double speed;
    @Nullable
    protected ItemEntity targetBreedingItem;

    @Nullable
    protected final Predicate<Animal> wantsFoodCondition;
    @Nullable
    protected final Consumer<ItemStack> foodConsumer;

    public GoToAndPickUpBreedingItemGoal(Animal animal, double searchRadius, double pickupRadius, double speed, @Nullable Predicate<Animal> wantsFoodCondition, @Nullable Consumer<ItemStack> foodConsumer) {
        this.animal = animal;
        this.searchRadius = searchRadius;
        this.pickupRadius = pickupRadius;
        this.speed = speed;
        this.wantsFoodCondition = wantsFoodCondition;
        this.foodConsumer = foodConsumer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public GoToAndPickUpBreedingItemGoal(Animal animal, double searchRadius, double pickupRadius, double speed) {
        this(animal, searchRadius, pickupRadius, speed, null, null);
    }

    protected boolean wantsFood() {
        if (wantsFoodCondition != null && wantsFoodCondition.test(animal)) {
            return true;
        }
        return animal.getAge() == 0 && animal.canFallInLove();
    }


    protected boolean isTargetValid() {
        return targetBreedingItem != null && targetBreedingItem.isAlive() && !targetBreedingItem.getItem().isEmpty();
    }

    @Nullable
    protected ItemEntity findClosestBreedingItem() {
        return animal.level()
                .getEntitiesOfClass(
                        ItemEntity.class,
                        animal.getBoundingBox().inflate(searchRadius),
                        itemEntity -> animal.isFood(itemEntity.getItem())
                )
                .stream()
                .min(Comparator.comparingDouble(animal::distanceToSqr))
                .filter(itemEntity -> animal.distanceTo(itemEntity) < searchRadius)
                .orElse(null);
    }

    @Override
    public boolean canUse() {
        if (!wantsFood()) {
            return false;
        }
        targetBreedingItem = findClosestBreedingItem();
        return isTargetValid();
    }

    @Override
    public boolean canContinueToUse() {
        return isTargetValid() && wantsFood();
    }

    @Override
    public void tick() {
        if (targetBreedingItem == null || !isTargetValid() || !wantsFood() || !animal.isEffectiveAi()) {
            return;
        }
        animal.getLookControl().setLookAt(targetBreedingItem, animal.getHeadRotSpeed(), animal.getMaxHeadXRot());
        if (!(animal instanceof TamableAnimal tameableEntity) || !tameableEntity.isOrderedToSit()) {
            animal.getNavigation().moveTo(targetBreedingItem, speed);
        }
        if (animal.distanceTo(targetBreedingItem) <= pickupRadius) {
            animal.getNavigation().stop();
            if (foodConsumer != null) {
                foodConsumer.accept(targetBreedingItem.getItem().copyWithCount(1));
            }
            targetBreedingItem.getItem().shrink(1);
            animal.setInLove(null);
        }
    }
}

package com.bwt.entities;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PickUpBreedingItemWhileSittingGoal extends Goal {
    protected final TameableEntity animal;
    protected final double searchRadius;
    @Nullable
    protected ItemEntity targetBreedingItem;

    @Nullable
    protected final Predicate<AnimalEntity> wantsFoodCondition;
    @Nullable
    protected final Consumer<ItemStack> foodConsumer;

    public PickUpBreedingItemWhileSittingGoal(TameableEntity animal, double searchRadius, @Nullable Predicate<AnimalEntity> wantsFoodCondition, @Nullable Consumer<ItemStack> foodConsumer) {
        this.animal = animal;
        this.searchRadius = searchRadius;
        this.wantsFoodCondition = wantsFoodCondition;
        this.foodConsumer = foodConsumer;
    }

    protected boolean wantsFood() {
        if (wantsFoodCondition != null && wantsFoodCondition.test(animal)) {
            return true;
        }
        return animal.getBreedingAge() == 0 && animal.canEat();
    }


    protected boolean isTargetValid() {
        return targetBreedingItem != null && targetBreedingItem.isAlive() && !targetBreedingItem.getStack().isEmpty();
    }

    @Nullable
    protected ItemEntity findClosestBreedingItem() {
        return animal.getWorld()
                .getEntitiesByClass(
                        ItemEntity.class,
                        animal.getBoundingBox().expand(searchRadius),
                        itemEntity -> animal.isBreedingItem(itemEntity.getStack())
                )
                .stream()
                .min(Comparator.comparingDouble(animal::squaredDistanceTo))
                .filter(itemEntity -> animal.distanceTo(itemEntity) < searchRadius)
                .orElse(null);
    }

    @Override
    public boolean canStart() {
        if (!animal.isSitting() && !animal.isInSittingPose()) {
            return false;
        }
        if (!wantsFood()) {
            return false;
        }
        targetBreedingItem = findClosestBreedingItem();
        return isTargetValid();
    }

    @Override
    public boolean shouldContinue() {
        return isTargetValid() && wantsFood();
    }

    @Override
    public void tick() {
        if (targetBreedingItem == null || !isTargetValid() || !wantsFood() || !animal.canMoveVoluntarily()) {
            return;
        }
        if (animal.distanceTo(targetBreedingItem) <= searchRadius) {
            if (foodConsumer != null) {
                foodConsumer.accept(targetBreedingItem.getStack().copyWithCount(1));
            }
            targetBreedingItem.getStack().decrement(1);
        }
    }
}

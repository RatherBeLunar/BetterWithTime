package com.bwt.entities;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class PickUpBreedingItemWhileSittingGoal extends Goal {
    protected final TamableAnimal animal;
    protected final double searchRadius;
    @Nullable
    protected ItemEntity targetBreedingItem;

    @Nullable
    protected final Predicate<Animal> wantsFoodCondition;
    @Nullable
    protected final Consumer<ItemStack> foodConsumer;

    public PickUpBreedingItemWhileSittingGoal(TamableAnimal animal, double searchRadius, @Nullable Predicate<Animal> wantsFoodCondition, @Nullable Consumer<ItemStack> foodConsumer) {
        this.animal = animal;
        this.searchRadius = searchRadius;
        this.wantsFoodCondition = wantsFoodCondition;
        this.foodConsumer = foodConsumer;
    }

    protected boolean wantsFood() {
        return wantsFoodCondition != null && wantsFoodCondition.test(animal);
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
        if (!animal.isOrderedToSit() && !animal.isInSittingPose()) {
            return false;
        }
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
        if (animal.distanceTo(targetBreedingItem) <= searchRadius) {
            if (foodConsumer != null) {
                foodConsumer.accept(targetBreedingItem.getItem().copyWithCount(1));
            }
            targetBreedingItem.getItem().shrink(1);
        }
    }
}

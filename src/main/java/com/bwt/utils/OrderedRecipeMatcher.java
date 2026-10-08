package com.bwt.utils;

import com.bwt.mixin.accessor.RecipeManagerAccessorMixin;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class OrderedRecipeMatcher {
    public static Optional<? extends RecipeHolder<? extends CraftingRecipe>> getFirstRecipeOfMultipleTypes(
            ServerLevel serverLevel,
            CraftingInput input,
            List<RecipeType<? extends CraftingRecipe>> recipeTypes
    ) {
        RecipeManager recipeManager = serverLevel.getServer().recipeAccess();
        for (RecipeType<? extends CraftingRecipe> recipeType: recipeTypes) {
            Optional<? extends RecipeHolder<? extends CraftingRecipe>> optionalResult = recipeManager.getRecipeFor(recipeType, input, serverLevel);
            if (optionalResult.isPresent()) {
                return optionalResult;
            }
        }
        return Optional.empty();
    }

    public static NonNullList<ItemStack> getRemainingStacks(
            Level level,
            CraftingInput input,
            List<RecipeType<? extends CraftingRecipe>> recipeTypes
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CraftingRecipe.defaultCraftingReminder(input);
        }

        Optional<? extends RecipeHolder<? extends CraftingRecipe>> optional = getFirstRecipeOfMultipleTypes(serverLevel, input, recipeTypes);
        if (optional.isPresent()) {
            return optional.get().value().getRemainingItems(input);
        } else {
            NonNullList<ItemStack> defaultedList = NonNullList.withSize(input.size(), ItemStack.EMPTY);

            for (int i = 0; i < defaultedList.size(); i++) {
                defaultedList.set(i, input.getItem(i));
            }

            return defaultedList;
        }
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getRecipesFor(RecipeType<T> recipeType, I recipeInput, ServerLevel serverLevel) {
        RecipeMap recipeMap = ((RecipeManagerAccessorMixin) serverLevel.recipeAccess()).getRecipeMap();
        return recipeMap.getRecipesFor(recipeType, recipeInput, serverLevel);
    }

    public static <I extends RecipeInput, R extends Recipe<I>> void getFirstRecipe(List<RecipeHolder<R>> matches, NonNullList<ItemStack> inventoryItems, Predicate<R> predicateConsumer) {
        // For each inventory item, in order
        for (ItemStack inventoryStack : inventoryItems) {
            // Filter down to recipes that contain that item in its ingredients.
            // If there are multiple that match the first ingredient, get the one with the most ingredients
            Iterator<RecipeHolder<R>> matchIterator = matches.stream()
                    .filter(match -> match.value().placementInfo().ingredients().stream().anyMatch(ingredient -> ingredient.test(inventoryStack)))
                    .sorted(Comparator.comparing((RecipeHolder<R> match) -> match.value().placementInfo().ingredients().size()).reversed())
                    .iterator();
            while (matchIterator.hasNext()) {
                R match = matchIterator.next().value();
                if (predicateConsumer.test(match)) {
                    return;
                }
            }
        }
    }
}

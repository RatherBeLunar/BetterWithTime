package com.bwt.utils;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class OrderedRecipeMatcher {
    public static Optional<? extends RecipeHolder<? extends CraftingRecipe>> getFirstRecipeOfMultipleTypes(
            Level level,
            CraftingInput input,
            List<RecipeType<? extends CraftingRecipe>> recipeTypes
    ) {
        RecipeManager recipeManager = level.getRecipeManager();
        for (RecipeType<? extends CraftingRecipe> recipeType: recipeTypes) {
            Optional<? extends RecipeHolder<? extends CraftingRecipe>> optionalResult = recipeManager.getRecipeFor(recipeType, input, level);
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
        Optional<? extends RecipeHolder<? extends CraftingRecipe>> optional = getFirstRecipeOfMultipleTypes(level, input, recipeTypes);
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

    public static <I extends RecipeInput, R extends Recipe<I>> void getFirstRecipe(List<RecipeHolder<R>> matches, NonNullList<ItemStack> inventoryItems, Predicate<R> predicateConsumer) {
        // For each inventory item, in order
        for (ItemStack inventoryStack : inventoryItems) {
            // Filter down to recipes that contain that item in its ingredients.
            // If there are multiple that match the first ingredient, get the one with the most ingredients
            Iterator<RecipeHolder<R>> matchIterator = matches.stream()
                    .filter(match -> match.value().getIngredients().stream().anyMatch(ingredient -> ingredient.test(inventoryStack)))
                    .sorted(Comparator.comparing((RecipeHolder<R> match) -> match.value().getIngredients().size()).reversed())
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

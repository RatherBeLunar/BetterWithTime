package com.bwt.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class OrderedRecipeMatcher {
    public static <I extends RecipeInput, R extends Recipe<I>> void getFirstRecipe(List<RecipeEntry<R>> matches, DefaultedList<ItemStack> inventoryItems, Predicate<R> predicateConsumer) {
        // For each inventory item, in order
        for (ItemStack inventoryStack : inventoryItems) {
            // Filter down to recipes that contain that item in its ingredients.
            // If there are multiple that match the first ingredient, get the one with the most ingredients
            Iterator<RecipeEntry<R>> matchIterator = matches.stream()
                    .filter(match -> match.value().getIngredients().stream().anyMatch(ingredient -> ingredient.test(inventoryStack)))
                    .sorted(Comparator.comparing((RecipeEntry<R> match) -> match.value().getIngredients().size()).reversed())
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

package com.bwt.utils;

import com.bwt.mixin.accessor.RecipeManagerAccessorMixin;
import com.bwt.recipes.BwtRecipes;
import com.bwt.recipes.soul_forge.SoulForgeRecipe;
import com.mojang.datafixers.util.Either;
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
    public static Either<Optional<RecipeHolder<SoulForgeRecipe>>, Optional<RecipeHolder<CraftingRecipe>>> getFirstRecipeForSoulForge(
            ServerLevel serverLevel,
            CraftingInput input
    ) {
        RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();

        Optional<RecipeHolder<SoulForgeRecipe>> resultA = recipeManager.getRecipeFor(BwtRecipes.SOUL_FORGE_RECIPE_TYPE, input, serverLevel);
        if (resultA.isPresent()) {
            return Either.left(resultA);
        }
        Optional<RecipeHolder<CraftingRecipe>> resultB = recipeManager.getRecipeFor(RecipeType.CRAFTING, input, serverLevel);
        return Either.right(resultB);
    }

    public static NonNullList<ItemStack> getRemainingStacksForSoulForge(
            Level level,
            CraftingInput input
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CraftingRecipe.defaultCraftingReminder(input);
        }

        Either<Optional<RecipeHolder<SoulForgeRecipe>>, Optional<RecipeHolder<CraftingRecipe>>> either = getFirstRecipeForSoulForge(serverLevel, input);
        Optional<NonNullList<ItemStack>> optionalRemainingItems = either.map(
                optional -> optional.map(holder -> holder.value().getRemainingItems(input)),
                optional -> optional.map(holder -> holder.value().getRemainingItems(input))
        );
        if (optionalRemainingItems.isPresent()) {
            return optionalRemainingItems.get();
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

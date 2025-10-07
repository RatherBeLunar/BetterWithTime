package com.bwt.integration.wood_good;

import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.saw.SawRecipe;
import com.google.common.base.Preconditions;
import net.mehvahdjukaar.moonlight.api.resources.RecipeTemplate;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SawRecipeTemplate extends RecipeTemplate {
    public static void register() {
        register(SawRecipe.class, SawRecipeTemplate::createSawRecipe);
    }

    public static SawRecipe createSawRecipe(SawRecipe originalRecipe, @NotNull BlockType from, @NotNull BlockType to) {
        Preconditions.checkNotNull(from, "Found null from block type for recipe remapping on recipe " + originalRecipe);
        Preconditions.checkNotNull(to, "Found null to block type for recipe remapping on recipe " + originalRecipe);
        BlockIngredient newIngredient = convertIngredient(originalRecipe.getIngredient(), from, to);
        List<ItemStack> originalResults = originalRecipe.getResults();
        BlockTypeRegistry<BlockType> fromRegistry = from.getRegistry();
        List<ItemStack> newResults = originalResults.stream()
                .map(result -> fromRegistry.getBlockTypeOf(result.getItem()) == from
                        ? convertItemStack(result, from, to)
                        : result
                ).toList();
        CraftingRecipeCategory cat = originalRecipe.getCategory();

        return new SawRecipe(originalRecipe.getGroup(), cat, newIngredient, newResults);
    }

    public static @NotNull BlockIngredient convertIngredient(BlockIngredient ingredient, @NotNull BlockType from, @NotNull BlockType to) {
        return new BlockTypeSwapBlockIngredient(ingredient, from, to);
    }

}
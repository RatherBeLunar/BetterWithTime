package com.bwt.recipes.cooking_pots;

import com.bwt.recipes.IngredientWithCount;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public record CookingPotRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Override
    public int getSize() {
        return items.size();
    }

    public boolean matches(IngredientWithCount ingredient) {
        return items.stream()
                .filter(stack -> ingredient.ingredient().test(stack))
                .map(ItemStack::getCount)
                .reduce(Integer::sum)
                .orElse(0) >= ingredient.count();
    }
}

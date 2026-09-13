package com.bwt.recipes.cooking_pots;

import com.bwt.recipes.IngredientWithCount;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CookingPotRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public int size() {
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

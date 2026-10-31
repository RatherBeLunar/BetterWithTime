package com.bwt.recipes.soul_forge;

import net.minecraft.world.item.crafting.*;

public interface SoulForgeRecipe extends CraftingRecipe {
    @Override
    RecipeSerializer<? extends SoulForgeRecipe> getSerializer();
}

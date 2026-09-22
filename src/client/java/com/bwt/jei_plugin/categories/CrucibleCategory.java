package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.cooking_pots.CauldronRecipe;
import com.bwt.recipes.cooking_pots.CrucibleRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CrucibleCategory extends CookingPotCategory<CrucibleRecipe> {
    public static final RecipeType<CrucibleRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "crucible", CrucibleRecipe.class);

    public CrucibleCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.crucible"), BwtBlocks.crucibleBlock);
    }

    @Override
    public @NotNull RecipeType<CrucibleRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public boolean isStoked() {
        return false;
    }
}

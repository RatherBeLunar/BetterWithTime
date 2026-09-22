package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.cooking_pots.CrucibleRecipe;
import com.bwt.recipes.cooking_pots.StokedCauldronRecipe;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class StokedCrucibleCategory extends CookingPotCategory<StokedCrucibleRecipe> {
    public static final RecipeType<StokedCrucibleRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "stoked_crucible", StokedCrucibleRecipe.class);

    public StokedCrucibleCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.stoked_crucible"), BwtBlocks.crucibleBlock);
    }

    @Override
    public @NotNull RecipeType<StokedCrucibleRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public boolean isStoked() {
        return true;
    }
}

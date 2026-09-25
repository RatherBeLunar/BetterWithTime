package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class StokedCrucibleReclaimCategory extends CookingPotCategory<StokedCrucibleRecipe> {
    public static final RecipeType<StokedCrucibleRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "stoked_crucible_reclaim", StokedCrucibleRecipe.class);

    public StokedCrucibleReclaimCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.stoked_crucible_reclaim"), BwtBlocks.crucibleBlock);
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

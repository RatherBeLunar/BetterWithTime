package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class StokedCrucibleCategory extends CookingPotCategory<StokedCrucibleRecipe> {
    public static final IRecipeType<StokedCrucibleRecipe> TYPE =
            IRecipeType.create(Id.MOD_ID, "stoked_crucible", StokedCrucibleRecipe.class);

    public StokedCrucibleCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.stoked_crucible"), BwtBlocks.crucibleBlock);
    }

    @Override
    public @NotNull IRecipeType<StokedCrucibleRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public boolean isStoked() {
        return true;
    }
}

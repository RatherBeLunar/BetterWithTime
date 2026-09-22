package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.cooking_pots.CauldronRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CauldronCategory extends CookingPotCategory<CauldronRecipe> {
    public static final RecipeType<CauldronRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "cauldron", CauldronRecipe.class);

    public CauldronCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.cauldron"), BwtBlocks.cauldronBlock);
    }

    @Override
    public @NotNull RecipeType<CauldronRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public boolean isStoked() {
        return false;
    }
}

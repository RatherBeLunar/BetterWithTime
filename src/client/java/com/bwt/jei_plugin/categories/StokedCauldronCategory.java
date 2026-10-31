package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.cooking_pots.StokedCauldronRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class StokedCauldronCategory extends CookingPotCategory<StokedCauldronRecipe> {
    public static final IRecipeType<StokedCauldronRecipe> TYPE =
            IRecipeType.create(Id.MOD_ID, "stoked_cauldron", StokedCauldronRecipe.class);

    public StokedCauldronCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.stoked_cauldron"), BwtBlocks.cauldronBlock);
    }

    @Override
    public @NotNull IRecipeType<StokedCauldronRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public boolean isStoked() {
        return true;
    }
}

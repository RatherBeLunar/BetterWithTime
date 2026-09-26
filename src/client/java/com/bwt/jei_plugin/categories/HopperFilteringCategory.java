package com.bwt.jei_plugin.categories;

import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


public class HopperFilteringCategory extends HopperCategoryBase<HopperFilterRecipe> {
    public static final RecipeType<HopperFilterRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "hopper_filtering", HopperFilterRecipe.class);

    public HopperFilteringCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.hopper_filtering"), 18 * 5,18 * 3);
    }

    @Override
    public @NotNull RecipeType<HopperFilterRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HopperFilterRecipe recipe, IFocusGroup focusGroup) {
        renderFilterRecipe(builder, recipe);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, HopperFilterRecipe recipe, IFocusGroup focuses) {
        createFilterRecipeExtras(builder, recipe);
        super.createRecipeExtras(builder, recipe, focuses);
    }
}

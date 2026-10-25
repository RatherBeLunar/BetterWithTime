package com.bwt.jei_plugin.categories;

import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


public class HopperFilteringCategory extends HopperCategoryBase<HopperFilterRecipe> {
    public static final IRecipeType<HopperFilterRecipe> TYPE =
            IRecipeType.create(Id.MOD_ID, "hopper_filtering", HopperFilterRecipe.class);

    public HopperFilteringCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.hopper_filtering"), 18 * 5,18 * 3);
    }

    @Override
    public @NotNull IRecipeType<HopperFilterRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HopperFilterRecipe recipe, IFocusGroup focusGroup) {
        renderFilterRecipe(builder, recipe);
    }

    @Override
    public void draw(HopperFilterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, HopperFilterRecipe recipe, IFocusGroup focuses) {
        createFilterRecipeExtras(builder, recipe);
        super.createRecipeExtras(builder, recipe, focuses);
    }
}

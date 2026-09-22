package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.utils.Id;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SawCategory extends BwtRecipeCategoryBase<SawRecipe> {

    public static final RecipeType<SawRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "saw", SawRecipe.class);

    protected ICraftingGridHelper craftingGridHelper;

    public SawCategory(IGuiHelper guiHelper) {
        super(
                140,
                27,
                Component.translatable("emi.category.bwt.saw"),
                guiHelper,
                BwtBlocks.sawBlock,
                null
        );
        craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public @NotNull RecipeType<SawRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SawRecipe recipe, IFocusGroup focusGroup) {
        IRecipeSlotBuilder inputSlot = builder.addInputSlot(2, 6);
        inputSlot.addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient().getMatchingStacks());

        List<ItemStack> outputs = recipe.getResults();
        for (int i = 0; i < outputs.size(); i++) {
            IRecipeSlotBuilder outputSlot = builder.addOutputSlot(56 + (26 * i), 6).setOutputSlotBackground();
            outputSlot.addIngredients(VanillaTypes.ITEM_STACK, List.of(outputs.get(i)));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SawRecipe recipe, IFocusGroup focuses) {
        super.createRecipeExtras(builder, recipe, focuses);
        builder.addDrawableWidget(FULL_GEAR)
                .setPosition(20, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        builder.addRecipeArrowWidget()
                .setPosition(22, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);
    }
}
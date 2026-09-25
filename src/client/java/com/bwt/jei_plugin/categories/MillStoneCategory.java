package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import com.bwt.utils.Id;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MillStoneCategory extends BwtRecipeCategoryBase<MillStoneRecipe> {

    public static final RecipeType<MillStoneRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "mill_stone", MillStoneRecipe.class);

    public MillStoneCategory(IGuiHelper guiHelper) {
        super(
                118,
                27,
                Component.translatable("emi.category.bwt.mill_stone"),
                guiHelper,
                BwtBlocks.millStoneBlock,
                null
        );
    }

    @Override
    public @NotNull RecipeType<MillStoneRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MillStoneRecipe recipe, IFocusGroup focusGroup) {
        int width = getRecipeGridWidth(recipe);
        int height = 1;
        createAndSetIngredients(builder, recipe.getIngredients(), width, height);
        List<ItemStack> outputs = recipe.getResults();
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(95, 6)
                .setOutputSlotBackground();
        outputSlot.addIngredients(VanillaTypes.ITEM_STACK, outputs);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MillStoneRecipe recipe, IFocusGroup focuses) {
        super.createRecipeExtras(builder, recipe, focuses);
        builder.addDrawableWidget(getAnimatedGearDrawable(40))
                .setPosition(60, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        builder.addRecipeArrowWidget()
                .setPosition(62, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM);
    }

    public int getRecipeGridWidth(MillStoneRecipe recipe) {
        return recipe.getIngredients().size();
    }

    public void createAndSetIngredients(IRecipeLayoutBuilder builder, List<Ingredient> ingredients, int width, int height) {
        List<IRecipeSlotBuilder> inputSlots = createInputSlots(builder, width, height);
        setIngredients(inputSlots, ingredients, width, height);
    }

    private static List<IRecipeSlotBuilder> createInputSlots(IRecipeLayoutBuilder builder, int width, int height) {
        if (width <= 0 || height <= 0) {
            builder.setShapeless();
        }

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int x = 0; x < 3; ++x) {
            IRecipeSlotBuilder slot = builder.addInputSlot(x * 18 + 2, 6).setStandardSlotBackground();
            inputSlots.add(slot);
        }
        return inputSlots;
    }

    public void setIngredients(List<IRecipeSlotBuilder> slotBuilders, List<Ingredient> ingredients, int width, int height) {
        if (slotBuilders.size() < width * height) {
            throw new IllegalArgumentException(String.format("There are not enough slots (%s) to hold a recipe of this size. (%sx%s)", slotBuilders.size(), width, height));
        }

        for (int i = 0; i < ingredients.size(); i++) {
            IRecipeSlotBuilder slot = slotBuilders.get(i);
            Ingredient ingredient = ingredients.get(i);
            if (ingredient != null) {
                slot.addIngredients(ingredient);
            }
        }
    }

}
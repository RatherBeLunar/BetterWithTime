package com.bwt.jei_plugin.categories;

import com.bwt.jei_plugin.DrawableCombined;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.recipes.cooking_pots.AbstractCookingPotRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Iterator;
import java.util.List;

public abstract class CookingPotCategory<T extends AbstractCookingPotRecipe> extends BwtRecipeCategoryBase<T> {
    public CookingPotCategory(IGuiHelper guiHelper, MutableComponent title, ItemLike cookingPotBlock) {
        super(
                182,
                44,
                title,
                guiHelper,
                cookingPotBlock,
                null
        );
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView slotsView, GuiGraphics gui, double mouseX, double mouseY) {
        super.draw(recipe, slotsView, gui, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focusGroup) {
        Iterator<IngredientWithCount> ingredientIterator = recipe.getIngredientsWithCount().iterator();
        Iterator<ItemStack> outputIterator = recipe.getResults().iterator();
        for (int y = 0; y < 2; ++y) {
            for (int x = 0; x < 4; ++x) {
                IRecipeSlotBuilder inputSlot = builder.addInputSlot(x * 18 + 1, y * 18 + 1).setStandardSlotBackground();
                if (ingredientIterator.hasNext()) {
                    IngredientWithCount ingredient = ingredientIterator.next();
                    inputSlot.addIngredients(VanillaTypes.ITEM_STACK, ingredient.getMatchingStacks());
                }
                IRecipeSlotBuilder outputSlot = builder.addOutputSlot(x * 18 + 110, y * 18 + 1).setStandardSlotBackground();
                if (outputIterator.hasNext()) {
                    ItemStack result = outputIterator.next();
                    outputSlot.addIngredients(VanillaTypes.ITEM_STACK, List.of(result));
                }
            }
        }
    }

    public abstract boolean isStoked();

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
        super.createRecipeExtras(builder, recipe, focuses);
        String flameTooltipText = isStoked()
                ? "Stoked fire directly beneath the cooking pot"
                : "Normal (unstoked) fire directly beneath the cooking pot";
        IDrawableStatic flameIcon = guiHelper.getRecipeFlameFilled();
        IDrawableAnimated animatedFill = guiHelper.createAnimatedDrawable(flameIcon, 60, IDrawableAnimated.StartDirection.BOTTOM, false);
        IDrawable drawable = new DrawableCombined(guiHelper.getRecipeFlameEmpty(), animatedFill);
        builder.addDrawableWidget(drawable)
                .setTooltip(FormattedText.of(flameTooltipText))
                .setPosition(0, -15, getWidth(), getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        builder.addRecipeArrowWidget()
                .setPosition(0, 5, getWidth(), getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

}
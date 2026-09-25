package com.bwt.jei_plugin.categories;

import com.bwt.jei_plugin.DrawableCombined;
import com.bwt.recipes.kiln.KilnRecipe;
import com.bwt.utils.Id;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KilnCategory extends BwtRecipeCategoryBase<KilnRecipe> {

    public static final RecipeType<KilnRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "kiln", KilnRecipe.class);

    public KilnCategory(IGuiHelper guiHelper) {
        super(
                140,
                34,
                Component.translatable("emi.category.bwt.kiln"),
                guiHelper,
                Blocks.BRICKS,
                null
        );
    }

    @Override
    public @NotNull RecipeType<KilnRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KilnRecipe recipe, IFocusGroup focusGroup) {
        IRecipeSlotBuilder inputSlot = builder.addInputSlot(2, 1);
        inputSlot.addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient().getMatchingStacks());

        List<ItemStack> outputs = recipe.getDrops();
        for (int i = 0; i < outputs.size(); i++) {
            IRecipeSlotBuilder outputSlot = builder.addOutputSlot(56 + (26 * i), 10).setOutputSlotBackground();
            outputSlot.addIngredients(VanillaTypes.ITEM_STACK, List.of(outputs.get(i)));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, KilnRecipe recipe, IFocusGroup focuses) {
        builder.addDrawableWidget(guiHelper.createDrawableItemLike(Blocks.BRICKS)).setPosition(2, 1 + 15);
        String flameTooltipText = "Stoked fire directly beneath a brick block, with at least 3 other bricks surrounding the ingredient";
        IDrawableStatic flameIcon = guiHelper.getRecipeFlameFilled();
        IDrawableAnimated animatedFill = guiHelper.createAnimatedDrawable(flameIcon, 60, IDrawableAnimated.StartDirection.BOTTOM, false);
        IDrawable drawable = new DrawableCombined(guiHelper.getRecipeFlameEmpty(), animatedFill);
        builder.addDrawableWidget(drawable)
                .setTooltip(FormattedText.of(flameTooltipText))
                .setPosition(24, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        builder.addRecipeArrowWidget()
                .setPosition(26, 12, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        super.createRecipeExtras(builder, recipe, focuses);
    }
}
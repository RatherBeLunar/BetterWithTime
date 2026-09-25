package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.turntable.TurntableRecipe;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TurntableCategory extends BwtRecipeCategoryBase<TurntableRecipe> {

    public static final RecipeType<TurntableRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "turntable", TurntableRecipe.class);

    public TurntableCategory(IGuiHelper guiHelper) {
        super(
                140,
                34,
                Component.translatable("emi.category.bwt.turntable"),
                guiHelper,
                BwtBlocks.turntableBlock,
                null
        );
    }

    @Override
    public @NotNull RecipeType<TurntableRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TurntableRecipe recipe, IFocusGroup focusGroup) {
        IRecipeSlotBuilder inputSlot = builder.addInputSlot(2, 1);
        inputSlot.addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient().getMatchingStacks());

        IRecipeSlotBuilder outputBlockSlot = builder.addOutputSlot(56, 1);
        outputBlockSlot.addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.getOutput().asItem().getDefaultInstance()));
        List<ItemStack> outputs = recipe.getDrops();
        for (int i = 0; i < outputs.size(); i++) {
            IRecipeSlotBuilder outputSlot = builder.addOutputSlot(82 + (26 * i), 10).setOutputSlotBackground();
            outputSlot.addIngredients(VanillaTypes.ITEM_STACK, List.of(outputs.get(i)));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, TurntableRecipe recipe, IFocusGroup focuses) {
        builder.addDrawableWidget(guiHelper.createDrawableItemLike(BwtBlocks.turntableBlock)).setPosition(2, 1 + 15);
        builder.addDrawableWidget(guiHelper.createDrawableItemLike(BwtBlocks.turntableBlock)).setPosition(56, 1 + 15);
        builder.addDrawableWidget(getAnimatedGearDrawable(40))
                .setPosition(24, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        builder.addRecipeArrowWidget()
                .setPosition(26, 12, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        super.createRecipeExtras(builder, recipe, focuses);
    }
}
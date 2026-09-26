package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class HopperCategoryBase<T> extends BwtRecipeCategoryBase<T> {

    public static final RecipeType<HopperFilterRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "hopper_filtering", HopperFilterRecipe.class);

    public static final ResourceLocation BACKGROUND = Id.of("textures/gui/container/hopper_recipe.png");

    public final IDrawableStatic BACKGROUND_WIDGET;
    public final IDrawableStatic IN_HOPPER_ARROW;
    public final IDrawableStatic BELOW_HOPPER_VERTICAL_BAR;
    public final IDrawableStatic BELOW_HOPPER_ACROSS_ARROW;

    public HopperCategoryBase(IGuiHelper guiHelper, MutableComponent title, int width, int height) {
        super(
                width,
                height,
                title,
                guiHelper,
                BwtBlocks.hopperBlock,
                null
        );

        BACKGROUND_WIDGET = guiHelper.createDrawable(BACKGROUND, 0, 0, 18 * 5, 18 * 4);
        IN_HOPPER_ARROW = guiHelper.createDrawable(BACKGROUND, 24, 18*4, 10, 13);
        BELOW_HOPPER_ACROSS_ARROW = guiHelper.createDrawable(BACKGROUND, 0, 72, 34, 13);
        BELOW_HOPPER_VERTICAL_BAR = guiHelper.createDrawable(BACKGROUND, 0, 85, 3, 13);
    }

    public void renderFilterRecipe(IRecipeLayoutBuilder builder, HopperFilterRecipe recipe) {
        List<ItemStack> inputs = List.of(recipe.ingredient().getItems());
        List<ItemStack> filters = List.of(recipe.filter().getItems());
        ItemStack output = recipe.result();
        ItemStack byproduct = recipe.byproduct();
        renderFilterRecipe(builder, inputs, filters, List.of(output), List.of(byproduct));
    }

    protected void renderFilterRecipe(IRecipeLayoutBuilder builder, List<ItemStack> inputs, List<ItemStack> filters, List<ItemStack> inHopperOutput, List<ItemStack> aboveHopperByproduct) {
        builder.addInputSlot(9, 0).setStandardSlotBackground().addIngredients(VanillaTypes.ITEM_STACK, inputs);
        if (aboveHopperByproduct.stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            builder.addOutputSlot(9 * 7, 0).setStandardSlotBackground().addItemStacks(aboveHopperByproduct);
        }
        builder.addSlot(RecipeIngredientRole.CATALYST, 18 * 2, 18).addIngredients(VanillaTypes.ITEM_STACK, filters);
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 18 * 2, 18 * 2).addItemStack(new ItemStack(BwtBlocks.hopperBlock));
        if (inHopperOutput.stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            builder.addOutputSlot(9 * 7, 18 * 2).setStandardSlotBackground().addItemStacks(inHopperOutput);
        }
    }

    public void createFilterRecipeExtras(IRecipeExtrasBuilder builder, HopperFilterRecipe recipe) {
        createFilterRecipeExtras(builder, List.of(recipe.result()));
    }

    public void createFilterRecipeExtras(IRecipeExtrasBuilder builder, List<ItemStack> results) {
        builder.addDrawableWidget(BACKGROUND_WIDGET).setPosition(0, 0);
        if (results.stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            builder.addDrawableWidget(IN_HOPPER_ARROW).setPosition(52, 38);
        }
    }
}

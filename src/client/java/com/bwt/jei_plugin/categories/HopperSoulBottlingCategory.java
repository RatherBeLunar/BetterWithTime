package com.bwt.jei_plugin.categories;

import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import com.bwt.utils.Id;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class HopperSoulBottlingCategory extends HopperCategoryBase<SoulBottlingRecipe> {
    public static final RecipeType<SoulBottlingRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "soul_bottling", SoulBottlingRecipe.class);

    static List<HopperFilterRecipe> filterRecipes = List.of();
    static ArrayList<ItemStack> inputs = new ArrayList<>();
    static ArrayList<ItemStack> filters = new ArrayList<>();
    static ArrayList<ItemStack> outputs = new ArrayList<>();
    static ArrayList<ItemStack> byproducts = new ArrayList<>();

    public HopperSoulBottlingCategory(IGuiHelper guiHelper) {
        super(guiHelper, Component.translatable("emi.category.bwt.hopper_souls"), 18 * 5,18 * 4);
    }

    @Override
    public @NotNull RecipeType<SoulBottlingRecipe> getRecipeType() {
        return TYPE;
    }

    protected static void processSingleFilterRecipe(HopperFilterRecipe filterRecipe) {
        ArrayList<ItemStack> inputsToAdd = new ArrayList<>(List.of(filterRecipe.ingredient().getItems()));
        ArrayList<ItemStack> filtersToAdd = new ArrayList<>(List.of(filterRecipe.filter().getItems()));
        ArrayList<ItemStack> outputsToAdd = new ArrayList<>(List.of(filterRecipe.result()));
        ArrayList<ItemStack> byproductsToAdd = new ArrayList<>(List.of(filterRecipe.byproduct()));
        int maxLength = Stream.of(inputsToAdd, filtersToAdd, outputsToAdd, byproductsToAdd).mapToInt(ArrayList::size).max().orElse(0);
        if (maxLength == 0) {
            return;
        }
        for (ArrayList<ItemStack> list : List.of(inputsToAdd, filtersToAdd, outputsToAdd, byproductsToAdd)) {
            if (list.isEmpty()) {
                list.add(ItemStack.EMPTY);
            }
            // Pad to max length to line up recipe slot displays
            while (list.size() < maxLength) {
                list.add(list.getLast());
            }
        }
        inputs.addAll(inputsToAdd);
        filters.addAll(filtersToAdd);
        outputs.addAll(outputsToAdd);
        byproducts.addAll(byproductsToAdd);
    }

    public static void processFilterRecipes(List<HopperFilterRecipe> filterRecipes) {
        filterRecipes
                .stream()
                .filter(recipe -> recipe.soulCount() > 0)
                .forEach(HopperSoulBottlingCategory::processSingleFilterRecipe);
        if (inputs.stream().allMatch(i -> i.is(inputs.getFirst().getItem()))) {
            inputs = new ArrayList<>(List.of(inputs.getFirst()));
        }
        if (filters.stream().allMatch(i -> i.is(filters.getFirst().getItem()))) {
            filters = new ArrayList<>(List.of(filters.getFirst()));
        }
        if (outputs.stream().allMatch(i -> i.is(outputs.getFirst().getItem()))) {
            outputs = new ArrayList<>(List.of(outputs.getFirst()));
        }
        if (byproducts.stream().allMatch(i -> i.is(byproducts.getFirst().getItem()))) {
            byproducts = new ArrayList<>(List.of(byproducts.getFirst()));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoulBottlingRecipe recipe, IFocusGroup focusGroup) {
        renderFilterRecipe(
                builder,
                inputs.stream().map(itemStack -> itemStack.copyWithCount(recipe.soulCount())).toList(),
                filters,
                outputs.stream().map(itemStack -> itemStack.copyWithCount(recipe.soulCount())).toList(),
                byproducts.stream().map(itemStack -> itemStack.copyWithCount(recipe.soulCount())).toList()
        );
        builder.addInputSlot(9, 18 * 3).setStandardSlotBackground().addIngredients(VanillaTypes.ITEM_STACK, recipe.bottle().getMatchingStacks());
        builder.addOutputSlot(9 * 7, 18 * 3).setStandardSlotBackground().addItemStack(recipe.getResult());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SoulBottlingRecipe recipe, IFocusGroup focuses) {
        createFilterRecipeExtras(builder, outputs);
        builder.addDrawableWidget(BELOW_HOPPER_ACROSS_ARROW).setPosition(28, 56);
        builder.addDrawableWidget(BELOW_HOPPER_VERTICAL_BAR).setPosition(43, 51);
        builder.addDrawableWidget(FULL_GEAR).setPosition(10, 20);
        super.createRecipeExtras(builder, recipe, focuses);
    }
}

package com.bwt.jei_plugin.categories;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.soul_forge.SoulForgeRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeCategory extends BwtRecipeCategoryBase<SoulForgeRecipe> {

    public static final RecipeType<SoulForgeRecipe> TYPE =
            RecipeType.create(Id.MOD_ID, "soul_forge", SoulForgeRecipe.class);

    public SoulForgeCategory(IGuiHelper guiHelper) {
        super(
                134,
                72,
                Component.translatable("emi.category.bwt.soul_forge"),
                guiHelper,
                BwtBlocks.soulForgeBlock,
                null
        );
    }

    @Override
    public @NotNull RecipeType<SoulForgeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public void draw(SoulForgeRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics gui, double mouseX, double mouseY) {
        super.draw(recipe, slotsView, gui, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoulForgeRecipe recipe, IFocusGroup focusGroup) {
        int width = getRecipeGridWidth(recipe);
        int height = getRecipeGridHeight(recipe);
        createAndSetIngredients(builder, recipe.getIngredients(), width, height);
        List<ItemStack> outputs = List.of(recipe.getResultItem(getRegistryAccess()));
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(113, 28)
                .setOutputSlotBackground();
        outputSlot.addIngredients(VanillaTypes.ITEM_STACK, outputs);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SoulForgeRecipe recipe, IFocusGroup focuses) {
        super.createRecipeExtras(builder, recipe, focuses);
        builder.addRecipeArrowWidget()
                .setPosition(79, 0, getWidth() - 61, getHeight(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
    }

    public int getRecipeGridWidth(SoulForgeRecipe recipe) {
        if (recipe instanceof SoulForgeShapedRecipe shapedRecipe) {
            return shapedRecipe.getWidth();
        }
        return 0;
    }

    public int getRecipeGridHeight(SoulForgeRecipe recipe) {
        if (recipe instanceof SoulForgeShapedRecipe shapedRecipe) {
            return shapedRecipe.getHeight();
        }
        return 0;
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
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                IRecipeSlotBuilder slot = builder.addInputSlot(x * 18 + 1, y * 18 + 1).setStandardSlotBackground();
                inputSlots.add(slot);
            }
        }
        return inputSlots;
    }

    public void setIngredients(List<IRecipeSlotBuilder> slotBuilders, List<Ingredient> ingredients, int width, int height) {
        if (width <= 0 || height <= 0) {
            width = height = getShapelessSize(ingredients.size());
        }
        if (slotBuilders.size() < width * height) {
            throw new IllegalArgumentException(String.format("There are not enough slots (%s) to hold a recipe of this size. (%sx%s)", slotBuilders.size(), width, height));
        }

        for (int i = 0; i < ingredients.size(); i++) {
            int index = getCraftingIndex(i, width, height);
            IRecipeSlotBuilder slot = slotBuilders.get(index);
            Ingredient ingredient = ingredients.get(i);
            if (ingredient != null) {
                slot.addIngredients(ingredient);
            }
        }
    }

    private static int getCraftingIndex(int i, int width, int height) {
        int gridWidth = 4;
        int gridHeight = 4;
        if (width == gridWidth && height == gridHeight) {
            return i;
        }
        if (width == 1) {
            if (height == 1) {
                return 0;
            }
            // +1 deliberately shifts 1-wide recipes to the 2nd column
            return (i * gridWidth) + 1;
        }
        if (height == 1) {
            // +gridWidth deliberately shifts 1-tall recipes to the 2nd row
            return i + gridWidth;
        }
        if (width == 2) {
            // +1 shifts to the 2nd and 3rd columns
            int offset = 1;
            if (height == 2) {
                // Offset by a row, so 2x2 recipes are centered
                offset += gridWidth;
            }
            return (i % width) + (Math.floorDiv(i, width) * gridWidth) + offset;
        }
        // All other shapes behave as normal
        return (i % width) + (Math.floorDiv(i, width) * gridWidth);
    }

    private static int getShapelessSize(int total) {
        return (int)Math.floor(Math.sqrt(total)) + 1;
    }

}
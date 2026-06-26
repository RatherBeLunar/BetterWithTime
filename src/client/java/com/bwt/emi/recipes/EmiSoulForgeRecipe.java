package com.bwt.emi.recipes;

import com.bwt.emi.BwtEmiPlugin;
import com.bwt.recipes.soul_forge.SoulForgeRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import com.bwt.utils.Id;
import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiSoulForgeRecipe implements EmiRecipe {
    private final EmiRecipeCategory category;
    protected final Identifier id;
    protected final List<EmiIngredient> input;
    protected final EmiStack output;
    private final int size;
    private final SoulForgeRecipe recipe;

    public EmiSoulForgeRecipe(RecipeEntry<SoulForgeRecipe> recipeEntry) {
        this(recipeEntry.value(), recipeEntry.id());
    }

    public EmiSoulForgeRecipe(SoulForgeRecipe recipe, Identifier id) {
        this.id = Id.of(String.format("%s-%s-%s", "soulforge", id.getNamespace(), id.getPath()));
        if (recipe instanceof SoulForgeShapedRecipe shapedRecipe) {
            this.input = padIngredients(shapedRecipe);
        }
        else {
            this.input = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        }
        this.output = EmiStack.of(recipe.getResult(null));
        this.recipe = recipe;
        this.size = 4;
        this.category = BwtEmiPlugin.SOUL_FORGE;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public @Nullable Identifier getId() {
        return Identifier.of(id.getNamespace(), "/" + id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 118 + (this.size == 4 ? 20 : 0);
    }

    @Override
    public int getDisplayHeight() {
        return 18*this.size;
    }

    public boolean canFit(int width, int height) {
        if (input.size() > size * size) {
            return false;
        }
        for (int i = 0; i < input.size(); i++) {
            int x = i % size;
            int y = i / size;
            if (!input.get(i).isEmpty() && (x >= width || y >= height)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var sizeXOffset =  this.size == 4 ? 18 : 0;
        var sizeYOffset = this.size == 4 ? 10 : 0;
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60 + sizeXOffset, 18 + sizeYOffset);
        if (recipe instanceof ShapelessRecipe) {
            widgets.addTexture(EmiTexture.SHAPELESS, 97 + sizeXOffset, sizeYOffset);
        }
        if (recipe instanceof SoulForgeShapedRecipe shapedRecipe) {
            addShapedWidgets(widgets, shapedRecipe);
        }
        else {
            addShapelessWidgets(widgets);
        }
        widgets.addSlot(output, 92 + sizeXOffset, 14 + sizeYOffset).large(true).recipeContext(this);
    }

    private void addShapedWidgets(WidgetHolder widgets, SoulForgeShapedRecipe shapedRecipe) {
        int i = 0;
        for (int y = 0; y < size; y++) {
            int x;
            for (x = 0; x < size; x++) {
                widgets.addSlot(input.get(i), i % size * 18, i / size * 18);
                i++;
            }
        }
    }

    private void addShapelessWidgets(WidgetHolder widgets) {
        for (int i = 0; i < size * size; i++) {
            if (i < input.size()) {
                widgets.addSlot(input.get(i), i % size * 18, i / size * 18);
            } else {
                widgets.addSlot(EmiStack.of(ItemStack.EMPTY), i % size * 18, i / size * 18);
            }
        }
    }

    private static List<EmiIngredient> padIngredients(SoulForgeShapedRecipe recipe) {
        List<EmiIngredient> list = Lists.newArrayList();
        int i = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (x >= recipe.getWidth() || y >= recipe.getHeight() || i >= recipe.getIngredients().size()) {
                    list.add(EmiStack.EMPTY);
                } else {
                    list.add(EmiIngredient.of(recipe.getIngredients().get(i++)));
                }
            }
        }
        return list;
    }
}

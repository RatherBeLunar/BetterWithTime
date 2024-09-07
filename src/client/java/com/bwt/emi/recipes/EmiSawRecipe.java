package com.bwt.emi.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.emi.BwtEmiPlugin;
import com.bwt.recipes.saw.SawRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class EmiSawRecipe implements EmiRecipe {

    private final EmiRecipeCategory category;
    private final Identifier id;
    private final EmiIngredient ingredient;
    private final List<EmiStack> results;
    private final int displayRows;

    public EmiSawRecipe(EmiRecipeCategory category, Identifier id, SawRecipe recipe) {
        this.category = category;
        this.id = id;
        this.ingredient = BwtEmiPlugin.from(recipe.getIngredient());
        this.results = recipe.getResults().stream().map(EmiStack::of).toList();
        this.displayRows = IntStream.of((int) Math.ceil(this.results.size() / 3.0), 1).max().orElse(1);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public @Nullable Identifier getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.ingredient);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.results;
    }

    @Override
    public int getDisplayWidth() {
        return 20 * 5;
    }

    @Override
    public int getDisplayHeight() {
        return 20 * this.displayRows;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var y = 0;
        var x = 0;
        var i = 0;

        int constantOutputSlots = 3;

        widgets.addSlot(this.ingredient, x, y);
        x = 20;
        widgets.addSlot(EmiStack.of(BwtBlocks.sawBlock), x, y).drawBack(false);
        x = 20 * 2;
        for (EmiIngredient ingredient : this.results) {
            widgets.addSlot(ingredient, x + (i * 18), y).recipeContext(this);
            i++;
        }
        while (i < constantOutputSlots) {
            widgets.addSlot(EmiStack.EMPTY, x + (i % 3 * 18), y + (i / 3 * 18)).recipeContext(this);
            i++;
        }
    }
}

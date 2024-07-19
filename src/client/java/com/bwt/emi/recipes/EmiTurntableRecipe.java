package com.bwt.emi.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.emi.BwtEmiPlugin;
import com.bwt.recipes.TurntableRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EmiTurntableRecipe implements EmiRecipe {

    private final EmiRecipeCategory category;
    private final Identifier id;
    private final EmiIngredient ingredient;
    private final List<EmiStack> drops;
    private final EmiStack output;
    private final int displayRows;

    public EmiTurntableRecipe(EmiRecipeCategory category, Identifier id, TurntableRecipe recipe) {
        this.category = category;
        this.id = id;
        this.ingredient = BwtEmiPlugin.from(recipe.getIngredient());
        this.output = EmiStack.of(recipe.getOutput());
        this.drops = recipe.getDrops().stream().map(EmiStack::of).toList();

        this.displayRows = IntStream.of(1).max().orElse(1);
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
        return List.of(this.output);
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
        widgets.addSlot(EmiStack.of(BwtBlocks.turntableBlock), x, y).drawBack(false);
        x = 20 * 2;

        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(this.output);
        outputs.addAll(this.drops);

        for (EmiStack ingredient : outputs) {
            widgets.addSlot(ingredient, x + (i * 18), y);
            i++;
        }
        while (i < constantOutputSlots) {
            widgets.addSlot(EmiStack.EMPTY, x + (i % 3 * 18), y + (i / 3 * 18));
            i++;
        }
    }
}

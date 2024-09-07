package com.bwt.emi.recipes;

import com.bwt.blocks.BwtBlocks;
import com.bwt.emi.BwtEmiPlugin;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiHopperFilterPermitList implements EmiRecipe {

    private final Identifier id;
    private final EmiIngredient filter;
    private final EmiIngredient permitted;

    public EmiHopperFilterPermitList(Identifier id, EmiIngredient filter, EmiIngredient permitted) {
        this.id = id;
        this.filter = filter;
        this.permitted = permitted;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return BwtEmiPlugin.HOPPER_FILTERING;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 18 * 3;
    }

    @Override
    public int getDisplayHeight() {
        return 18 * 3;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var y = 0;
        var x = 0;

        widgets.addSlot(this.permitted, x+18, y).drawBack(false);

        y = 18;
        widgets.addSlot(this.filter, x,y);
        widgets.addSlot(EmiStack.of(BwtBlocks.hopperBlock), x + 18, y).drawBack(false);
    }
}

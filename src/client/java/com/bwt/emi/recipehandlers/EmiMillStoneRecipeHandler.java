package com.bwt.emi.recipehandlers;

import com.bwt.blocks.mill_stone.MillStoneScreenHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class EmiMillStoneRecipeHandler implements StandardRecipeHandler<MillStoneScreenHandler> {
    private final EmiRecipeCategory category;

    public EmiMillStoneRecipeHandler(EmiRecipeCategory category) {
        this.category = category;
    }

    @Override
    public List<Slot> getInputSources(MillStoneScreenHandler handler) {
        return handler.slots.stream().filter(slot -> slot.id >= MillStoneScreenHandler.SIZE).toList();
    }

    @Override
    public List<Slot> getCraftingSlots(MillStoneScreenHandler handler) {
        return handler.slots.stream().filter(slot -> slot.id < MillStoneScreenHandler.SIZE).filter(slot -> slot.getStack().isEmpty()).toList();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        EmiRecipeCategory category = recipe.getCategory();
        return category.equals(this.category);
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<MillStoneScreenHandler> context) {
        return canFit(context.getScreenHandler(), recipe.getInputs());
    }

    public boolean canFit(MillStoneScreenHandler handler, List<EmiIngredient> ingredients) {
        return this.getCraftingSlots(handler).size() >= ingredients.size();
    }
}
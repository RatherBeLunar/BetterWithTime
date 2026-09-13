package com.bwt.emi.recipehandlers;

import com.bwt.blocks.mill_stone.MillStoneScreenHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import java.util.List;
import net.minecraft.world.inventory.Slot;

public class EmiMillStoneRecipeHandler implements StandardRecipeHandler<MillStoneScreenHandler> {
    private final EmiRecipeCategory category;

    public EmiMillStoneRecipeHandler(EmiRecipeCategory category) {
        this.category = category;
    }

    @Override
    public List<Slot> getInputSources(MillStoneScreenHandler handler) {
        return handler.slots.stream().filter(slot -> slot.getContainerSlot() >= MillStoneScreenHandler.SIZE).toList();
    }

    @Override
    public List<Slot> getCraftingSlots(MillStoneScreenHandler handler) {
        return handler.slots.stream().filter(slot -> slot.getContainerSlot() < MillStoneScreenHandler.SIZE).filter(slot -> slot.getItem().isEmpty()).toList();
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
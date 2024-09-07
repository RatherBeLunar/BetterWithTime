package com.bwt.emi.recipehandlers;

import com.bwt.blocks.cauldron.CauldronScreenHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class EmiCookingPotRecipeHandler<T extends ScreenHandler> implements StandardRecipeHandler<T> {

    private final EmiRecipeCategory category;
    private final int SIZE = 27;
    public EmiCookingPotRecipeHandler(EmiRecipeCategory category) {
        this.category = category;
    }

    //FIXME if there are already items in the cooking pot and you use the + to fill the recipe, it crashes.
    //Will require rewriting some of teh default methods in StandardRecipeHandler

    @Override
    public List<Slot> getInputSources(T handler) {
        return handler.slots.stream().filter(slot -> slot.id >= SIZE).toList();
    }

    @Override
    public List<Slot> getCraftingSlots(T handler) {
        return handler.slots.stream().filter(slot -> slot.id < SIZE).toList();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
    }
}
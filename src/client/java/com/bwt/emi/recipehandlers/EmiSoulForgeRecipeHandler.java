package com.bwt.emi.recipehandlers;

import com.bwt.blocks.soul_forge.SoulForgeScreenHandler;
import com.bwt.emi.BwtEmiPlugin;
import com.bwt.emi.recipes.EmiSoulForgeRecipe;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiSoulForgeRecipeHandler {

    public static List<EmiRecipeCategory> CATEGORIES = List.of(BwtEmiPlugin.SOUL_FORGE, VanillaEmiRecipeCategories.CRAFTING);

    public static class FourByFour implements StandardRecipeHandler<SoulForgeScreenHandler> {
        @Override
        public @Nullable Slot getOutputSlot(SoulForgeScreenHandler handler) {
            return handler.slots.get(0);
        }

        @Override
        public List<Slot> getInputSources(SoulForgeScreenHandler handler) {
            return handler.slots.subList(1, handler.slots.size());
        }

        @Override
        public List<Slot> getCraftingSlots(SoulForgeScreenHandler handler) {
            return handler.slots.subList(1, 17);
        }
        @Override
        public boolean supportsRecipe(EmiRecipe recipe) {
            return CATEGORIES.contains(recipe.getCategory()) && recipe.getInputs().size() > 9;
        }
    }

    public static class ThreeByThree  implements StandardRecipeHandler<SoulForgeScreenHandler> {
        @Override
        public @Nullable Slot getOutputSlot(SoulForgeScreenHandler handler) {
            return handler.slots.get(0);
        }

        @Override
        public List<Slot> getInputSources(SoulForgeScreenHandler handler) {

            List<Slot> slots = new ArrayList<>();
            slots.addAll(handler.slots.subList(1, 4));
            slots.addAll(handler.slots.subList(5, 8));
            slots.addAll(handler.slots.subList(9, 12));
            slots.addAll(handler.slots.subList(17, handler.slots.size()));
            return slots;
        }

        @Override
        public List<Slot> getCraftingSlots(SoulForgeScreenHandler handler) {
            List<Slot> slots = new ArrayList<>();
            slots.addAll(handler.slots.subList(1, 4));
            slots.addAll(handler.slots.subList(5, 8));
            slots.addAll(handler.slots.subList(9, 12));
            return slots;
        }
        @Override
        public boolean supportsRecipe(EmiRecipe recipe) {
            return CATEGORIES.contains(recipe.getCategory()) && recipe.getInputs().size() <= 9;
        }
    }


}

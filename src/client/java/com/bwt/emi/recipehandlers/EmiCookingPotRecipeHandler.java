package com.bwt.emi.recipehandlers;

import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotScreenHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmiCookingPotRecipeHandler<T extends AbstractCookingPotScreenHandler> implements StandardRecipeHandler<T> {

    private final EmiRecipeCategory unstokedCategory;
    private final EmiRecipeCategory stokedCategory;

    public EmiCookingPotRecipeHandler(EmiRecipeCategory unstokedCategory, EmiRecipeCategory stokedCategory) {
        this.unstokedCategory = unstokedCategory;
        this.stokedCategory = stokedCategory;
    }

    @Override
    public List<Slot> getInputSources(T handler) {
        return handler.slots.stream().filter(slot -> slot.id >= AbstractCookingPotScreenHandler.SIZE).toList();
    }

    @Override
    public List<Slot> getCraftingSlots(T handler) {
        return handler.slots.stream().filter(slot -> slot.id < AbstractCookingPotScreenHandler.SIZE).filter(slot -> slot.getStack().isEmpty()).toList();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        EmiRecipeCategory category = recipe.getCategory();
        return category.equals(this.unstokedCategory) || category.equals(this.stokedCategory);
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
        EmiRecipeCategory category = recipe.getCategory();
        if (category.equals(this.stokedCategory) != context.getScreenHandler().isStoked()) {
            return false;
        }
        return canFit(context.getScreenHandler(), recipe.getInputs());
    }

    public boolean canFit(T handler, List<EmiIngredient> ingredients) {
        return this.getCraftingSlots(handler).size() >= ingredients.size();
//        List<Slot> slots = this.getCraftingSlots(handler);
//        List<Slot> nonEmptySlots = slots.stream().filter(slot -> !slot.getStack().isEmpty()).toList();
//        ArrayList<ItemStack> stacksToInsert = ingredients.stream()
//                .map(EmiIngredient::copy)
//                .flatMap(emiIngredient -> emiIngredient.getEmiStacks().stream())
//                .map(EmiStack::getItemStack)
//                .filter(itemStack -> !itemStack.isEmpty())
//                .collect(Collectors.toCollection(ArrayList::new));
//        for (Slot slot : nonEmptySlots) {
//            ItemStack slotStack = slot.getStack();
//            ItemVariant itemVariant = ItemVariant.of(slotStack);
//            for (ItemStack stack : stacksToInsert) {
//                if (itemVariant.matches(stack)) {
//                    int spaceAvailable = slotStack.getMaxCount() - slotStack.getCount();
//                    stack.setCount(stack.getCount() - spaceAvailable);
//                }
//            }
//            stacksToInsert.removeIf(ItemStack::isEmpty);
//            if (stacksToInsert.isEmpty()) {
//                return true;
//            }
//        }
//        List<Slot> emptySlots = slots.stream().filter(slot -> slot.getStack().isEmpty()).toList();
//        return stacksToInsert.size() <= emptySlots.size();
    }
}
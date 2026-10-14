package com.bwt.screens;

import com.bwt.recipes.soul_forge.SoulForgeShapedCraftingRecipeDisplay;
import com.bwt.recipes.soul_forge.SoulForgeShapelessCraftingRecipeDisplay;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public class SoulForgeCraftingRecipeBookComponent extends CraftingRecipeBookComponent {
    public SoulForgeCraftingRecipeBookComponent(AbstractCraftingMenu abstractCraftingMenu) {
        super(abstractCraftingMenu);
    }

    private boolean canDisplay(RecipeDisplay recipeDisplay) {
        int width = this.menu.getGridWidth();
        int height = this.menu.getGridHeight();

        return switch (recipeDisplay) {
            case ShapedCraftingRecipeDisplay display -> width >= display.width() && height >= display.height();
            case SoulForgeShapedCraftingRecipeDisplay display -> width >= display.width() && height >= display.height();
            case ShapelessCraftingRecipeDisplay display -> width * height >= display.ingredients().size();
            case SoulForgeShapelessCraftingRecipeDisplay display -> width * height >= display.ingredients().size();
            default -> false;
        };
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
        recipeCollection.selectRecipes(stackedItemContents, this::canDisplay);
    }

//    @Override
//    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
//        ghostSlots.setResult(this.menu.getResultSlot(), contextMap, recipeDisplay.result());
//        int n = 0;
//        switch (recipeDisplay) {
//            case SoulForgeShapedCraftingRecipeDisplay soulForgeShapedCraftingRecipeDisplay: {
//                List<Slot> list = this.menu.getInputGridSlots();
//                PlaceRecipeHelper.placeRecipe(
//                        this.menu.getGridWidth(),
//                        this.menu.getGridHeight(),
//                        soulForgeShapedCraftingRecipeDisplay.width(),
//                        soulForgeShapedCraftingRecipeDisplay.height(),
//                        soulForgeShapedCraftingRecipeDisplay.ingredients(),
//                        (slotDisplay, i, j, k) -> {
//                            Slot slot = list.get(i);
//                            ghostSlots.setInput(slot, contextMap, (SlotDisplay)slotDisplay);
//                        }
//                );
//                break;
//            }
//            case 1: {
//                ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay) recipeDisplay;
//                List list2 = this.menu.getInputGridSlots();
//                int i2 = Math.min(shapelessCraftingRecipeDisplay.ingredients().size(), list2.size());
//                for (int j2 = 0; j2 < i2; ++j2) {
//                    ghostSlots.setInput((Slot)list2.get(j2), contextMap, (SlotDisplay)shapelessCraftingRecipeDisplay.ingredients().get(j2));
//                }
//                break;
//            }
//        }
//    }
}

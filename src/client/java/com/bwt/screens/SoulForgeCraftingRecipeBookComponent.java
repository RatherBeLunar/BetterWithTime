package com.bwt.screens;

import com.bwt.mixin.client.GhostSlotsAccessorMixin;
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

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
        GhostSlotsAccessorMixin ghostSlotsAccessor = (GhostSlotsAccessorMixin) ghostSlots;
        ghostSlotsAccessor.bwt$setResult(this.menu.getResultSlot(), contextMap, recipeDisplay.result());
        switch (recipeDisplay) {
            case SoulForgeShapedCraftingRecipeDisplay soulForgeShapedCraftingRecipeDisplay:
                List<Slot> list = this.menu.getInputGridSlots();
                PlaceRecipeHelper.placeRecipe(
                        this.menu.getGridWidth(),
                        this.menu.getGridHeight(),
                        soulForgeShapedCraftingRecipeDisplay.width(),
                        soulForgeShapedCraftingRecipeDisplay.height(),
                        soulForgeShapedCraftingRecipeDisplay.ingredients(),
                        (slotDisplay, ix, jx, k) -> {
                            Slot slot = list.get(ix);
                            ghostSlotsAccessor.bwt$setInput(slot, contextMap, slotDisplay);
                        }
                );
                break;
            case SoulForgeShapelessCraftingRecipeDisplay soulForgeShapelessCraftingRecipeDisplay:
                List<Slot> list2 = this.menu.getInputGridSlots();
                int i = Math.min(soulForgeShapelessCraftingRecipeDisplay.ingredients().size(), list2.size());

                for (int j = 0; j < i; j++) {
                    ghostSlotsAccessor.bwt$setInput(list2.get(j), contextMap, soulForgeShapelessCraftingRecipeDisplay.ingredients().get(j));
                }
            default:
                super.fillGhostRecipe(ghostSlots, recipeDisplay, contextMap);
        }
    }
}

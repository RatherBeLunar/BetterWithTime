package com.bwt.blocks.soul_forge;

import com.bwt.recipes.BwtRecipes;
import com.bwt.utils.OrderedRecipeMatcher;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;

public class SoulForgeCraftingResultSlot extends ResultSlot {
    private final CraftingContainer input;
    private final Player player;

    public SoulForgeCraftingResultSlot(Player player, CraftingContainer input, Container inventory, int index, int x, int y) {
        super(player, input, inventory, index, x, y);
        this.input = input;
        this.player = player;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        CraftingInput.Positioned positioned = this.input.asPositionedCraftInput();
        CraftingInput craftingRecipeInput = positioned.input();
        int i = positioned.left();
        int j = positioned.top();
        NonNullList<ItemStack> defaultedList = OrderedRecipeMatcher.getRemainingStacks(
                player.level(),
                craftingRecipeInput,
                List.of(BwtRecipes.SOUL_FORGE_RECIPE_TYPE, RecipeType.CRAFTING)
        );

        for (int k = 0; k < craftingRecipeInput.height(); k++) {
            for (int l = 0; l < craftingRecipeInput.width(); l++) {
                int m = l + i + (k + j) * this.input.getWidth();
                ItemStack itemStack = this.input.getItem(m);
                ItemStack itemStack2 = defaultedList.get(l + k * craftingRecipeInput.width());
                if (!itemStack.isEmpty()) {
                    this.input.removeItem(m, 1);
                    itemStack = this.input.getItem(m);
                }

                if (!itemStack2.isEmpty()) {
                    if (itemStack.isEmpty()) {
                        this.input.setItem(m, itemStack2);
                    } else if (ItemStack.isSameItemSameComponents(itemStack, itemStack2)) {
                        itemStack2.grow(itemStack.getCount());
                        this.input.setItem(m, itemStack2);
                    } else if (!this.player.getInventory().add(itemStack2)) {
                        this.player.drop(itemStack2, false);
                    }
                }
            }
        }
    }
}

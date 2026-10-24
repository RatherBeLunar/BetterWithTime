package com.bwt.mixin.client;

import com.bwt.recipes.soul_forge.SoulForgeShapedCraftingRecipeDisplay;
import com.bwt.recipes.soul_forge.SoulForgeShapelessCraftingRecipeDisplay;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OverlayRecipeComponent.OverlayCraftingRecipeButton.class)
public class OverlayCraftingRecipeButtonMixin {
    @Unique
    private static OverlayRecipeComponent.OverlayRecipeButton.Pos createGridPos(int i, int j, List<ItemStack> list) {
        return new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + i * 7, 3 + j * 7, list);
    }

    @Inject(method = "calculateIngredientsPositions", at = @At("TAIL"))
    private static void bwt$calculateIngredientsPositions(
            RecipeDisplay recipeDisplay,
            ContextMap contextMap,
            CallbackInfoReturnable<List<OverlayRecipeComponent.OverlayRecipeButton.Pos>> cir,
            @Local List<OverlayRecipeComponent.OverlayRecipeButton.Pos> list
    ) {
        switch (recipeDisplay) {
            case SoulForgeShapedCraftingRecipeDisplay soulForgeShapedCraftingRecipeDisplay: {
                PlaceRecipeHelper.placeRecipe(
                        3,
                        3,
                        soulForgeShapedCraftingRecipeDisplay.width(),
                        soulForgeShapedCraftingRecipeDisplay.height(),
                        soulForgeShapedCraftingRecipeDisplay.ingredients(),
                        (slotDisplay, i, j, k) -> {
                            List<ItemStack> stacks = slotDisplay.resolveForStacks(contextMap);
                            if (!stacks.isEmpty()) {
                                list.add(createGridPos(j, k, stacks));
                            }
                });
                break;
            }
            case SoulForgeShapelessCraftingRecipeDisplay soulForgeShapelessCraftingRecipeDisplay: {
                List<SlotDisplay> ingredients = soulForgeShapelessCraftingRecipeDisplay.ingredients();
                for (int i = 0; i < ingredients.size(); ++i) {
                    List<ItemStack> stacks = ingredients.get(i).resolveForStacks(contextMap);
                    if (stacks.isEmpty()) continue;
                    list.add(createGridPos(i % 3, i / 3, stacks));
                }
                break;
            }
            default:
                break;
        }

    }

//    @Inject(method = "getSprite", at = @At("HEAD"), cancellable = true)
//    void bwt$foo(boolean enabled, CallbackInfoReturnable<Identifier> cir) {
//
//    }

}

package com.bwt.mixin;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingRecipe.class)
public interface BaseCraftingGridRecipe extends Recipe<CraftingInput> {
}

package com.bwt.mixin;

import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlaceRecipeHelper.class)
public interface PlaceRecipeHelperMixin {
    @Inject(
            method = "placeRecipe(IILnet/minecraft/world/item/crafting/Recipe;Ljava/lang/Iterable;Lnet/minecraft/recipebook/PlaceRecipeHelper$Output;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static <T> void bwt$placeInSoulForge(int i, int j, Recipe<?> recipe, Iterable<T> iterable, PlaceRecipeHelper.Output<T> output, CallbackInfo ci) {
        if (recipe instanceof SoulForgeShapedRecipe shapedRecipe) {
            PlaceRecipeHelper.placeRecipe(i, j, shapedRecipe.getWidth(), shapedRecipe.getHeight(), iterable, output);
            ci.cancel();
        }
    }
}

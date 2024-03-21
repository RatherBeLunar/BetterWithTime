package com.bwt.generation;

import com.bwt.recipes.DisabledRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.util.Identifier;

public class DisabledVanilaRecipeGenerator extends FabricRecipeProvider {
    public DisabledVanilaRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        disableVanilla("bone_meal", exporter);
    }

    public void disableVanilla(String recipeId, RecipeExporter exporter) {
        exporter.accept(new Identifier(recipeId), new DisabledRecipe(""),null);
    }
}

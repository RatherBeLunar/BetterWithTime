package com.bwt.generation;

import com.bwt.recipes.DisabledRecipe;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DisabledVanilaRecipeGenerator extends FabricRecipeProvider {
    final FabricDataOutput.PathResolver recipesPathResolver;
    final FabricDataOutput.PathResolver advancementsPathResolver;

    public DisabledVanilaRecipeGenerator(FabricDataOutput output) {
        super(output);
        this.recipesPathResolver = output.getResolver(FabricDataOutput.OutputType.DATA_PACK, "recipes");
        this.advancementsPathResolver = output.getResolver(FabricDataOutput.OutputType.DATA_PACK, "advancements");
    }

    @Override
    public void generate(RecipeExporter exporter) {/* this is not called */}

    public void disableRecipes(RecipeExporter exporter) {
        disableVanilla("bone_meal", exporter);
    }

    public void disableVanilla(String recipeId, RecipeExporter exporter) {
        exporter.accept(new Identifier(recipeId), new DisabledRecipe(""),null);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        Set<Identifier> generatedRecipes = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        disableRecipes(new RecipeExporter() {
            @Override
            public void accept(Identifier recipeId, @Nullable Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
                Identifier identifier = getRecipeIdentifier(recipeId);

                if (!generatedRecipes.add(identifier)) {
                    throw new IllegalStateException("Duplicate recipe " + identifier);
                }

                JsonObject recipeJson = Util.getResult(Recipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe), IllegalStateException::new).getAsJsonObject();
                ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(recipe);
                ConditionJsonProvider.write(recipeJson, conditions);

                list.add(DataProvider.writeToPath(writer, recipeJson, recipesPathResolver.resolveJson(identifier)));

                if (advancement != null) {
                    JsonObject advancementJson = Util.getResult(Advancement.CODEC.encodeStart(JsonOps.INSTANCE, advancement.value()), IllegalStateException::new).getAsJsonObject();
                    ConditionJsonProvider.write(advancementJson, conditions);
                    list.add(DataProvider.writeToPath(writer, advancementJson, advancementsPathResolver.resolveJson(getRecipeIdentifier(advancement.id()))));
                }
            }

            @Override
            public Advancement.Builder getAdvancementBuilder() {
                return Advancement.Builder.createUntelemetered();
            }
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }
}

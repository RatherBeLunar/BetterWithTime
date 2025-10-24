package com.bwt.generation;

import com.bwt.recipes.DisabledRecipe;
import com.bwt.utils.Id;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DisabledVanilaRecipeGenerator extends FabricRecipeProvider {
    public DisabledVanilaRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        disableVanilla(Items.DECORATED_POT, exporter);
        disableVanilla(Items.DECORATED_POT, "_simple", exporter);
        disableVanilla(Items.FLOWER_POT, exporter);
        disableVanilla(Items.BONE_MEAL, exporter);
        disableVanilla(Items.BREAD, exporter);
        disableVanilla(Items.CAKE, exporter);
        disableVanilla(Items.COOKIE, exporter);

        disableVanilla(Items.NETHERITE_INGOT, exporter);
        disableVanilla(Items.NETHERITE_INGOT, "_from_netherite_block", exporter);
        disableVanilla(Items.NETHERITE_BLOCK, exporter);
        disableVanilla(Items.NETHERITE_PICKAXE, "_smithing", exporter);
        disableVanilla(Items.NETHERITE_SHOVEL, "_smithing",exporter);
        disableVanilla(Items.NETHERITE_AXE, "_smithing",exporter);
        disableVanilla(Items.NETHERITE_HOE, "_smithing",exporter);
        disableVanilla(Items.NETHERITE_SWORD, "_smithing",exporter);
        disableVanilla(Items.NETHERITE_HELMET, "_smithing", exporter);
        disableVanilla(Items.NETHERITE_CHESTPLATE, "_smithing", exporter);
        disableVanilla(Items.NETHERITE_LEGGINGS, "_smithing", exporter);
        disableVanilla(Items.NETHERITE_BOOTS, "_smithing", exporter);

        disableVanilla(Items.MOSSY_COBBLESTONE, "_from_moss_block", exporter);
        disableVanilla(Items.MOSSY_COBBLESTONE, "_from_vine", exporter);

        disableVanilla(Items.HOPPER, exporter);
    }

    public void disableVanilla(String recipeId, RecipeExporter exporter) {
        exporter.accept(Id.mc(recipeId), new DisabledRecipe(),null);
    }

    public void disableVanilla(ItemConvertible itemConvertible, RecipeExporter exporter) {
        disableVanilla(Registries.ITEM.getId(itemConvertible.asItem()).getPath(), exporter);
    }

    public void disableVanilla(ItemConvertible itemConvertible, String suffix, RecipeExporter exporter) {
        disableVanilla(Registries.ITEM.getId(itemConvertible.asItem()).withSuffixedPath(suffix).getPath(), exporter);
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }
}

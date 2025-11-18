package com.bwt.generation;

import com.bwt.recipes.DisabledRecipe;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class DisabledVanilaRecipeGenerator extends FabricRecipeProvider {
    public DisabledVanilaRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Pots
        disableVanilla(Items.DECORATED_POT, exporter);
        disableVanilla(Items.DECORATED_POT, "_simple", exporter);
        disableVanilla(Items.FLOWER_POT, exporter);
        disableVanilla(convertBetween(Items.INK_SAC, Items.BLACK_DYE), exporter);
        disableVanilla(convertBetween(Items.LAPIS_LAZULI, Items.BLUE_DYE), exporter);
        disableVanilla(convertBetween(Items.COCOA_BEANS, Items.BROWN_DYE), exporter);
        disableVanilla(convertBetween(Items.WITHER_ROSE, Items.BLACK_DYE), exporter);
        disableVanilla(convertBetween(Items.CORNFLOWER, Items.BLUE_DYE), exporter);
        disableVanilla(convertBetween(Items.PITCHER_PLANT, Items.CYAN_DYE), exporter);
        disableVanilla(convertBetween(Items.BLUE_ORCHID, Items.LIGHT_BLUE_DYE), exporter);
        disableVanilla(convertBetween(Items.AZURE_BLUET, Items.LIGHT_GRAY_DYE), exporter);
        disableVanilla(convertBetween(Items.OXEYE_DAISY, Items.LIGHT_GRAY_DYE), exporter);
        disableVanilla(convertBetween(Items.WHITE_TULIP, Items.LIGHT_GRAY_DYE), exporter);
        disableVanilla(convertBetween(Items.ALLIUM, Items.MAGENTA_DYE), exporter);
        disableVanilla(convertBetween(Items.LILAC, Items.MAGENTA_DYE), exporter);
        disableVanilla(convertBetween(Items.ORANGE_TULIP, Items.ORANGE_DYE), exporter);
        disableVanilla(convertBetween(Items.TORCHFLOWER, Items.ORANGE_DYE), exporter);
        disableVanilla(convertBetween(Items.PEONY, Items.PINK_DYE), exporter);
        disableVanilla(convertBetween(Items.PINK_PETALS, Items.PINK_DYE), exporter);
        disableVanilla(convertBetween(Items.PINK_TULIP, Items.PINK_DYE), exporter);
        disableVanilla(convertBetween(Items.BEETROOT, Items.RED_DYE), exporter);
        disableVanilla(convertBetween(Items.POPPY, Items.RED_DYE), exporter);
        disableVanilla(convertBetween(Items.RED_TULIP, Items.RED_DYE), exporter);
        disableVanilla(convertBetween(Items.ROSE_BUSH, Items.RED_DYE), exporter);
        disableVanilla(convertBetween(Items.BONE_MEAL, Items.WHITE_DYE), exporter);
        disableVanilla(convertBetween(Items.LILY_OF_THE_VALLEY, Items.WHITE_DYE), exporter);
        disableVanilla(convertBetween(Items.DANDELION, Items.YELLOW_DYE), exporter);
        disableVanilla(convertBetween(Items.SUNFLOWER, Items.YELLOW_DYE), exporter);

        // Millstone replacements
        disableVanilla(Items.BONE_MEAL, exporter);
        disableVanilla(Items.BREAD, exporter);
        disableVanilla(Items.SUGAR, "_from_sugar_cane", exporter);
        disableVanilla(Items.CAKE, exporter);
        disableVanilla(Items.COOKIE, exporter);

        // Netherite
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

        // Mossy
        disableVanilla(Items.MOSSY_COBBLESTONE, "_from_moss_block", exporter);
        disableVanilla(Items.MOSSY_COBBLESTONE, "_from_vine", exporter);

        // Hopper
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

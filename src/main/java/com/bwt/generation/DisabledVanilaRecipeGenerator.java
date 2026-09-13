package com.bwt.generation;

import com.bwt.recipes.DisabledRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import java.util.concurrent.CompletableFuture;

public class DisabledVanilaRecipeGenerator extends FabricRecipeProvider {
    public DisabledVanilaRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        // Pots
        disableVanilla(Items.DECORATED_POT, exporter);
        disableVanilla(Items.DECORATED_POT, "_simple", exporter);
        disableVanilla(Items.FLOWER_POT, exporter);
        disableVanilla(getConversionRecipeName(Items.INK_SAC, Items.BLACK_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.LAPIS_LAZULI, Items.BLUE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.COCOA_BEANS, Items.BROWN_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.WITHER_ROSE, Items.BLACK_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.CORNFLOWER, Items.BLUE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.PITCHER_PLANT, Items.CYAN_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.BLUE_ORCHID, Items.LIGHT_BLUE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.AZURE_BLUET, Items.LIGHT_GRAY_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.OXEYE_DAISY, Items.LIGHT_GRAY_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.WHITE_TULIP, Items.LIGHT_GRAY_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.ALLIUM, Items.MAGENTA_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.LILAC, Items.MAGENTA_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.ORANGE_TULIP, Items.ORANGE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.TORCHFLOWER, Items.ORANGE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.PEONY, Items.PINK_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.PINK_PETALS, Items.PINK_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.PINK_TULIP, Items.PINK_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.BEETROOT, Items.RED_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.POPPY, Items.RED_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.RED_TULIP, Items.RED_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.ROSE_BUSH, Items.RED_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.BONE_MEAL, Items.WHITE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.LILY_OF_THE_VALLEY, Items.WHITE_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.DANDELION, Items.YELLOW_DYE), exporter);
        disableVanilla(getConversionRecipeName(Items.SUNFLOWER, Items.YELLOW_DYE), exporter);

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

    public void disableVanilla(String recipeId, RecipeOutput exporter) {
        exporter.accept(Id.mc(recipeId), new DisabledRecipe(),null);
    }

    public void disableVanilla(ItemLike itemConvertible, RecipeOutput exporter) {
        disableVanilla(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).getPath(), exporter);
    }

    public void disableVanilla(ItemLike itemConvertible, String suffix, RecipeOutput exporter) {
        disableVanilla(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).withSuffix(suffix).getPath(), exporter);
    }

    @Override
    protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
        return identifier;
    }
}

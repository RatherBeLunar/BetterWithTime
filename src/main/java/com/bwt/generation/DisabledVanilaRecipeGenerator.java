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
        disableVanilla(getConversionRecipeName(Items.BLACK_DYE, Items.INK_SAC), exporter);
        disableVanilla(getConversionRecipeName(Items.BLUE_DYE, Items.LAPIS_LAZULI), exporter);
        disableVanilla(getConversionRecipeName(Items.BROWN_DYE, Items.COCOA_BEANS), exporter);
        disableVanilla(getConversionRecipeName(Items.BLACK_DYE, Items.WITHER_ROSE), exporter);
        disableVanilla(getConversionRecipeName(Items.BLUE_DYE, Items.CORNFLOWER), exporter);
        disableVanilla(getConversionRecipeName(Items.CYAN_DYE, Items.PITCHER_PLANT), exporter);
        disableVanilla(getConversionRecipeName(Items.LIGHT_BLUE_DYE, Items.BLUE_ORCHID), exporter);
        disableVanilla(getConversionRecipeName(Items.LIGHT_GRAY_DYE, Items.AZURE_BLUET), exporter);
        disableVanilla(getConversionRecipeName(Items.LIGHT_GRAY_DYE, Items.OXEYE_DAISY), exporter);
        disableVanilla(getConversionRecipeName(Items.LIGHT_GRAY_DYE, Items.WHITE_TULIP), exporter);
        disableVanilla(getConversionRecipeName(Items.MAGENTA_DYE, Items.ALLIUM), exporter);
        disableVanilla(getConversionRecipeName(Items.MAGENTA_DYE, Items.LILAC), exporter);
        disableVanilla(getConversionRecipeName(Items.ORANGE_DYE, Items.ORANGE_TULIP), exporter);
        disableVanilla(getConversionRecipeName(Items.ORANGE_DYE, Items.TORCHFLOWER), exporter);
        disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.PEONY), exporter);
        disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.PINK_PETALS), exporter);
        disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.PINK_TULIP), exporter);
        disableVanilla(getConversionRecipeName(Items.RED_DYE, Items.BEETROOT), exporter);
        disableVanilla(getConversionRecipeName(Items.RED_DYE, Items.POPPY), exporter);
        disableVanilla(Items.RED_DYE, "_from_tulip", exporter);
        disableVanilla(getConversionRecipeName(Items.RED_DYE, Items.ROSE_BUSH), exporter);
        disableVanilla(getConversionRecipeName(Items.WHITE_DYE, Items.BONE_MEAL), exporter);
        disableVanilla(getConversionRecipeName(Items.WHITE_DYE, Items.LILY_OF_THE_VALLEY), exporter);
        disableVanilla(getConversionRecipeName(Items.YELLOW_DYE, Items.DANDELION), exporter);
        disableVanilla(getConversionRecipeName(Items.YELLOW_DYE, Items.SUNFLOWER), exporter);

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

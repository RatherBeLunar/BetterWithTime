package com.bwt.generation;

import com.bwt.recipes.DisabledRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import java.util.concurrent.CompletableFuture;

public class DisabledVanilaRecipeGenerator extends FabricRecipeProvider {
    public DisabledVanilaRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        return new RecipeProvider(provider, output) {
            @Override
            public void buildRecipes() {
                // Pots
                disableVanilla(Items.DECORATED_POT, output);
                disableVanilla(Items.DECORATED_POT, "_simple", output);
                disableVanilla(Items.FLOWER_POT, output);

                // Dyes
                disableVanilla(getConversionRecipeName(Items.BLACK_DYE, Items.INK_SAC), output);
                disableVanilla(getConversionRecipeName(Items.BLUE_DYE, Items.LAPIS_LAZULI), output);
                disableVanilla(getConversionRecipeName(Items.BROWN_DYE, Items.COCOA_BEANS), output);
                disableVanilla(getConversionRecipeName(Items.BLACK_DYE, Items.WITHER_ROSE), output);
                disableVanilla(getConversionRecipeName(Items.BLUE_DYE, Items.CORNFLOWER), output);
                disableVanilla(getConversionRecipeName(Items.CYAN_DYE, Items.PITCHER_PLANT), output);
                disableVanilla(getConversionRecipeName(Items.GRAY_DYE, Items.CLOSED_EYEBLOSSOM), output);
                disableVanilla(getConversionRecipeName(Items.LIGHT_BLUE_DYE, Items.BLUE_ORCHID), output);
                disableVanilla(getConversionRecipeName(Items.LIGHT_GRAY_DYE, Items.AZURE_BLUET), output);
                disableVanilla(getConversionRecipeName(Items.LIGHT_GRAY_DYE, Items.OXEYE_DAISY), output);
                disableVanilla(getConversionRecipeName(Items.LIGHT_GRAY_DYE, Items.WHITE_TULIP), output);
                disableVanilla(getConversionRecipeName(Items.MAGENTA_DYE, Items.ALLIUM), output);
                disableVanilla(getConversionRecipeName(Items.MAGENTA_DYE, Items.LILAC), output);
                disableVanilla(getConversionRecipeName(Items.ORANGE_DYE, Items.OPEN_EYEBLOSSOM), output);
                disableVanilla(getConversionRecipeName(Items.ORANGE_DYE, Items.ORANGE_TULIP), output);
                disableVanilla(getConversionRecipeName(Items.ORANGE_DYE, Items.TORCHFLOWER), output);
                disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.PEONY), output);
                disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.PINK_PETALS), output);
                disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.PINK_TULIP), output);
                disableVanilla(getConversionRecipeName(Items.PINK_DYE, Items.CACTUS_FLOWER), output);
                disableVanilla(getConversionRecipeName(Items.RED_DYE, Items.BEETROOT), output);
                disableVanilla(getConversionRecipeName(Items.RED_DYE, Items.POPPY), output);
                disableVanilla(Items.RED_DYE, "_from_tulip", output);
                disableVanilla(getConversionRecipeName(Items.RED_DYE, Items.ROSE_BUSH), output);
                disableVanilla(getConversionRecipeName(Items.WHITE_DYE, Items.BONE_MEAL), output);
                disableVanilla(getConversionRecipeName(Items.WHITE_DYE, Items.LILY_OF_THE_VALLEY), output);
                disableVanilla(getConversionRecipeName(Items.YELLOW_DYE, Items.DANDELION), output);
                disableVanilla(getConversionRecipeName(Items.YELLOW_DYE, Items.SUNFLOWER), output);
                disableVanilla(getConversionRecipeName(Items.YELLOW_DYE, Items.WILDFLOWERS), output);

                // Millstone replacements
                disableVanilla(Items.BONE_MEAL, output);
                disableVanilla(Items.BREAD, output);
                disableVanilla(Items.SUGAR, "_from_sugar_cane", output);
                disableVanilla(Items.CAKE, output);
                disableVanilla(Items.COOKIE, output);

                // Netherite
                disableVanilla(Items.NETHERITE_INGOT, output);
                disableVanilla(Items.NETHERITE_INGOT, "_from_netherite_block", output);
                disableVanilla(Items.NETHERITE_BLOCK, output);
                disableVanilla(Items.NETHERITE_PICKAXE, "_smithing", output);
                disableVanilla(Items.NETHERITE_SHOVEL, "_smithing",output);
                disableVanilla(Items.NETHERITE_AXE, "_smithing", output);
                disableVanilla(Items.NETHERITE_HOE, "_smithing", output);
                disableVanilla(Items.NETHERITE_SWORD, "_smithing", output);
                disableVanilla(Items.NETHERITE_HELMET, "_smithing", output);
                disableVanilla(Items.NETHERITE_CHESTPLATE, "_smithing", output);
                disableVanilla(Items.NETHERITE_LEGGINGS, "_smithing", output);
                disableVanilla(Items.NETHERITE_BOOTS, "_smithing", output);

                // Mossy
                disableVanilla(Items.MOSSY_COBBLESTONE, "_from_moss_block", output);
                disableVanilla(Items.MOSSY_COBBLESTONE, "_from_vine", output);

                // Hopper
                disableVanilla(Items.HOPPER, output);
            }
        };
    }

    public void disableVanilla(String recipeId, RecipeOutput output) {
        output.accept(ResourceKey.create(Registries.RECIPE, Id.mc(recipeId)), new DisabledRecipe(),null);
    }

    public void disableVanilla(ItemLike itemConvertible, RecipeOutput output) {
        disableVanilla(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).getPath(), output);
    }

    public void disableVanilla(ItemLike itemConvertible, String suffix, RecipeOutput output) {
        disableVanilla(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).withSuffix(suffix).getPath(), output);
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }

    @Override
    public String getName() {
        return "DisabledVanilaRecipeGenerator";
    }
}

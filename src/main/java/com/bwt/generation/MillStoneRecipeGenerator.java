package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.MillStoneRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class MillStoneRecipeGenerator extends FabricRecipeProvider {
    public MillStoneRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateMillStoneRecipes(exporter);
    }

    protected void generateMillStoneRecipes(RecipeExporter exporter) {
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.COAL).result(BwtItems.coalDustItem).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.NETHERRACK).result(BwtItems.groundNetherrackItem).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(BwtItems.hempItem).result(BwtItems.hempFiberItem, 4).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LEATHER).result(BwtItems.scouredLeatherItem).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.WHEAT).result(BwtItems.flourItem).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.SUGAR_CANE).result(Items.SUGAR).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BONE).result(Items.BONE_MEAL, 3).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(BwtBlocks.companionCubeBlock.asItem())
                .result(BwtItems.wolfChopItem).result(Items.RED_DYE, 3).result(Items.STRING, 10).offerTo(exporter);

        // Dyes
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.INK_SAC).result(Items.BLACK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LAPIS_LAZULI).result(Items.BLUE_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.COCOA_BEANS).result(Items.BROWN_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.WITHER_ROSE).result(Items.BLACK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.CORNFLOWER).result(Items.BLUE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PITCHER_PLANT).result(Items.CYAN_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BLUE_ORCHID).result(Items.LIGHT_BLUE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.AZURE_BLUET).result(Items.LIGHT_GRAY_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.OXEYE_DAISY).result(Items.LIGHT_GRAY_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.WHITE_TULIP).result(Items.LIGHT_GRAY_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.ALLIUM).result(Items.MAGENTA_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LILAC).result(Items.MAGENTA_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.ORANGE_TULIP).result(Items.ORANGE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.TORCHFLOWER).result(Items.ORANGE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PEONY).result(Items.PINK_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PINK_PETALS).result(Items.PINK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PINK_TULIP).result(Items.PINK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BEETROOT).result(Items.RED_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.POPPY).result(Items.RED_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.RED_TULIP).result(Items.RED_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.ROSE_BUSH).result(Items.RED_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BONE_MEAL).result(Items.WHITE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LILY_OF_THE_VALLEY).result(Items.WHITE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.DANDELION).result(Items.YELLOW_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.SUNFLOWER).result(Items.YELLOW_DYE, 2).offerTo(exporter);
    }
}

package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class HopperRecipeGenerator extends FabricRecipeProvider {
    public HopperRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateHopperFilterRecipes(exporter);
        generateSoulBottlingRecipes(exporter);
    }

    protected void generateHopperFilterRecipes(RecipeExporter exporter) {
        HopperFilterRecipe.JsonBuilder.create().filter(BwtBlocks.wickerPaneBlock.asItem()).ingredient(Items.GRAVEL).result(Items.SAND).byproduct(Items.FLINT).offerTo(exporter);
        HopperFilterRecipe.JsonBuilder.create().filter(Items.SOUL_SAND).ingredient(BwtItems.groundNetherrackItem).byproduct(BwtItems.hellfireDustItem).soulCount(1).offerTo(exporter);
        HopperFilterRecipe.JsonBuilder.create().filter(Items.SOUL_SAND).ingredient(BwtItems.soulDustItem).byproduct(BwtItems.sawDustItem).soulCount(1).offerTo(exporter);
    }

    protected void generateSoulBottlingRecipes(RecipeExporter exporter) {
        SoulBottlingRecipe.JsonBuilder.create().bottle(BwtBlocks.urnBlock).soulCount(8).result(BwtItems.soulUrnItem).offerTo(exporter);
    }
}

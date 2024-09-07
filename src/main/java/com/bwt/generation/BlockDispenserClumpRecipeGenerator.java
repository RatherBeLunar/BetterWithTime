package com.bwt.generation;

import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockDispenserClumpRecipeGenerator extends FabricRecipeProvider {
    public BlockDispenserClumpRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.CLAY_BALL).count(4).output(Blocks.CLAY).offerTo(exporter);
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.SNOWBALL).count(4).output(Blocks.SNOW_BLOCK).offerTo(exporter);
    }
}

package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.turntable.TurntableRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TurntableRecipeGenerator extends FabricRecipeProvider {
    public TurntableRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        TurntableRecipe.JsonBuilder.create(Blocks.CLAY, BwtBlocks.unfiredCrucibleBlock).drops(Items.CLAY_BALL).markDefault().offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock).markDefault().offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock).drops(Items.CLAY_BALL).markDefault().offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock).drops(Items.CLAY_BALL).markDefault().offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredMouldBlock).markDefault().offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredMouldBlock, Blocks.AIR).drops(Items.CLAY_BALL).offerTo(exporter);
    }
}

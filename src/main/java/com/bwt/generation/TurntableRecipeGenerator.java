package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.TurntableRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;

public class TurntableRecipeGenerator extends FabricRecipeProvider {
    public TurntableRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        TurntableRecipe.JsonBuilder.create(Blocks.CLAY, BwtBlocks.unfiredCrucibleBlock).drops(Items.CLAY_BALL).offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock).offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock).drops(Items.CLAY_BALL).offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock).drops(Items.CLAY_BALL).offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredMouldBlock).offerTo(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredMouldBlock, Blocks.AIR).drops(Items.CLAY_BALL).offerTo(exporter);
    }
}

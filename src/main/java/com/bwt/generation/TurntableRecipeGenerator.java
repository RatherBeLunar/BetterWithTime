package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.turntable.TurntableRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class TurntableRecipeGenerator extends FabricRecipeProvider {
    public TurntableRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        TurntableRecipe.JsonBuilder.create(Blocks.CLAY, BwtBlocks.unfiredDecoratedPotBlock).markDefault().save(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredDecoratedPotBlock, BwtBlocks.unfiredCrucibleBlock).drops(Items.CLAY_BALL).markDefault().save(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock).markDefault().save(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock).drops(Items.CLAY_BALL).markDefault().save(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock).drops(Items.CLAY_BALL).markDefault().save(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredFlowerPotBlock).markDefault().save(exporter);
        TurntableRecipe.JsonBuilder.create(BwtBlocks.unfiredFlowerPotBlock, Blocks.AIR).drops(Items.CLAY_BALL).save(exporter);
    }
}

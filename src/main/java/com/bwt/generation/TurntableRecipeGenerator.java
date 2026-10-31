package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.turntable.TurntableRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class TurntableRecipeGenerator extends FabricRecipeProvider {
    public TurntableRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }
    
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        return new SharedRecipeProvider(provider, output) {
            @Override
            public void buildRecipes() {
                recipe(Blocks.CLAY, BwtBlocks.unfiredDecoratedPotBlock).save(output);
                recipe(BwtBlocks.unfiredDecoratedPotBlock, BwtBlocks.unfiredCrucibleBlock).drops(Items.CLAY_BALL).save(output);
                recipe(BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock).save(output);
                recipe(BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock).drops(Items.CLAY_BALL).save(output);
                recipe(BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock).drops(Items.CLAY_BALL).save(output);
                recipe(BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredFlowerPotBlock).save(output);
                recipe(BwtBlocks.unfiredFlowerPotBlock, Blocks.AIR).drops(Items.CLAY_BALL).save(output);
            }

            private TurntableRecipe.JsonBuilder recipe(Block input, Block output) {
                return TurntableRecipe.JsonBuilder.create(input, output);
            }
        };
    }

    @Override
    public String getName() {
        return "TurntableRecipeGenerator";
    }
}

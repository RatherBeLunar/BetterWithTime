package com.bwt.generation;

import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class BlockDispenserClumpRecipeGenerator extends FabricRecipeProvider {
    public BlockDispenserClumpRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new RecipeProvider(registryLookup, output) {
            @Override
            public void buildRecipes() {
                BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.CLAY_BALL, 4).output(Blocks.CLAY).save(output);
                BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.SNOWBALL, 4).output(Blocks.SNOW_BLOCK).save(output);
                BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.RAW_IRON, 9).output(Blocks.RAW_IRON_BLOCK).save(output);
                BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.RAW_GOLD, 9).output(Blocks.RAW_GOLD_BLOCK).save(output);
                BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.RAW_COPPER, 9).output(Blocks.RAW_COPPER_BLOCK).save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "BlockDispenserClumpRecipeGenerator";
    }
}

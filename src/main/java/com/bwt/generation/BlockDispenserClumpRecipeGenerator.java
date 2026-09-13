package com.bwt.generation;

import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class BlockDispenserClumpRecipeGenerator extends FabricRecipeProvider {
    public BlockDispenserClumpRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.CLAY_BALL).count(4).output(Blocks.CLAY).save(exporter);
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.SNOWBALL).count(4).output(Blocks.SNOW_BLOCK).save(exporter);
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.RAW_IRON).count(9).output(Blocks.RAW_IRON_BLOCK).save(exporter);
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.RAW_GOLD).count(9).output(Blocks.RAW_GOLD_BLOCK).save(exporter);
        BlockDispenserClumpRecipe.JsonBuilder.create().ingredient(Items.RAW_COPPER).count(9).output(Blocks.RAW_COPPER_BLOCK).save(exporter);
    }
}

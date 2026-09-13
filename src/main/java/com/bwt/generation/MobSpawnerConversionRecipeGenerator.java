package com.bwt.generation;

import com.bwt.recipes.mob_spawner_conversion.MobSpawnerConversionRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class MobSpawnerConversionRecipeGenerator extends FabricRecipeProvider {
    public MobSpawnerConversionRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE).convertsTo(Blocks.MOSSY_COBBLESTONE).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE_SLAB).convertsTo(Blocks.MOSSY_COBBLESTONE_SLAB).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE_STAIRS).convertsTo(Blocks.MOSSY_COBBLESTONE_STAIRS).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE_WALL).convertsTo(Blocks.MOSSY_COBBLESTONE_WALL).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICKS).convertsTo(Blocks.MOSSY_STONE_BRICKS).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICK_SLAB).convertsTo(Blocks.MOSSY_STONE_BRICK_SLAB).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICK_STAIRS).convertsTo(Blocks.MOSSY_STONE_BRICK_STAIRS).save(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICK_WALL).convertsTo(Blocks.MOSSY_STONE_BRICK_WALL).save(exporter);
    }
}

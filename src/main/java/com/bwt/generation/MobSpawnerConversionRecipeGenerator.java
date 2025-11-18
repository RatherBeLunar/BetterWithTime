package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.kiln.KilnRecipe;
import com.bwt.recipes.mob_spawner_conversion.MobSpawnerConversionRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.DyeColor;

import java.util.concurrent.CompletableFuture;

public class MobSpawnerConversionRecipeGenerator extends FabricRecipeProvider {
    public MobSpawnerConversionRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE).convertsTo(Blocks.MOSSY_COBBLESTONE).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE_SLAB).convertsTo(Blocks.MOSSY_COBBLESTONE_SLAB).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE_STAIRS).convertsTo(Blocks.MOSSY_COBBLESTONE_STAIRS).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.COBBLESTONE_WALL).convertsTo(Blocks.MOSSY_COBBLESTONE_WALL).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICKS).convertsTo(Blocks.MOSSY_STONE_BRICKS).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICK_SLAB).convertsTo(Blocks.MOSSY_STONE_BRICK_SLAB).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICK_STAIRS).convertsTo(Blocks.MOSSY_STONE_BRICK_STAIRS).offerTo(exporter);
        MobSpawnerConversionRecipe.JsonBuilder.create(Blocks.STONE_BRICK_WALL).convertsTo(Blocks.MOSSY_STONE_BRICK_WALL).offerTo(exporter);
    }
}

package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.KilnRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.DyeColor;

public class KilnRecipeGenerator extends FabricRecipeProvider {
    public KilnRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Ores
        KilnRecipe.JsonBuilder.create(BlockTags.IRON_ORES).drops(Items.IRON_INGOT).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.GOLD_ORES).drops(Items.GOLD_INGOT).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.COAL_ORES).drops(Items.COAL).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.COPPER_ORES).drops(Items.COPPER_INGOT).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.DIAMOND_ORES).drops(Items.DIAMOND).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.EMERALD_ORES).drops(Items.EMERALD).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.LAPIS_ORES).drops(Items.LAPIS_LAZULI).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.REDSTONE_ORES).drops(Items.REDSTONE).offerTo(exporter);
        // Charcoal
        KilnRecipe.JsonBuilder.create(BlockTags.LOGS).drops(Items.CHARCOAL).offerTo(exporter);
        // Pottery
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredCrucibleBlock).drops(BwtBlocks.crucibleBlock.asItem()).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredVaseBlock).drops(BwtBlocks.vaseBlocks.get(DyeColor.WHITE).asItem()).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredUrnBlock).drops(BwtBlocks.urnBlock.asItem()).offerTo(exporter);
    }
}

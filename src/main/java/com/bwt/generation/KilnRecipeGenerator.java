package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.kiln.KilnRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class KilnRecipeGenerator extends FabricRecipeProvider {
    public KilnRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        // Ores
        KilnRecipe.JsonBuilder.create(BlockTags.IRON_ORES).drops(Items.IRON_INGOT).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.GOLD_ORES).drops(Items.GOLD_INGOT).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.COAL_ORES).drops(Items.COAL).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.COPPER_ORES).drops(Items.COPPER_INGOT).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.DIAMOND_ORES).drops(Items.DIAMOND).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.EMERALD_ORES).drops(Items.EMERALD).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.LAPIS_ORES).drops(Items.LAPIS_LAZULI).save(exporter);
        KilnRecipe.JsonBuilder.create(BlockTags.REDSTONE_ORES).drops(Items.REDSTONE).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.ANCIENT_DEBRIS).drops(Items.NETHERITE_SCRAP).save(exporter);
        // Ore blocks
        KilnRecipe.JsonBuilder.create(ConventionalBlockTags.STORAGE_BLOCKS_RAW_IRON).drops(Items.IRON_INGOT, 9).save(exporter);
        KilnRecipe.JsonBuilder.create(ConventionalBlockTags.STORAGE_BLOCKS_RAW_GOLD).drops(Items.GOLD_INGOT, 9).save(exporter);
        KilnRecipe.JsonBuilder.create(ConventionalBlockTags.STORAGE_BLOCKS_RAW_COPPER).drops(Items.COPPER_INGOT, 9).save(exporter);
        // Charcoal
        KilnRecipe.JsonBuilder.create(BlockTags.LOGS).drops(Items.CHARCOAL).save(exporter);
        // Pottery
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredDecoratedPotBlock).drops(Items.DECORATED_POT).markDefault().save(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredDecoratedPotBlockWithSherds).drops(Items.DECORATED_POT).markDefault().save(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredCrucibleBlock).drops(BwtBlocks.crucibleBlock).markDefault().save(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredPlanterBlock).drops(BwtBlocks.planterBlock).markDefault().save(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredVaseBlock).drops(BwtBlocks.vaseBlocks.get(DyeColor.WHITE)).markDefault().save(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredUrnBlock).drops(BwtBlocks.urnBlock).markDefault().save(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredFlowerPotBlock).drops(Items.FLOWER_POT).markDefault().save(exporter);

        // Terracotta
        KilnRecipe.JsonBuilder.create(Blocks.CLAY).drops(Blocks.TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.WHITE_TERRACOTTA).drops(Blocks.WHITE_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.LIGHT_GRAY_TERRACOTTA).drops(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.GRAY_TERRACOTTA).drops(Blocks.GRAY_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.BLACK_TERRACOTTA).drops(Blocks.BLACK_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.BROWN_TERRACOTTA).drops(Blocks.BROWN_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.RED_TERRACOTTA).drops(Blocks.RED_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.ORANGE_TERRACOTTA).drops(Blocks.ORANGE_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.YELLOW_TERRACOTTA).drops(Blocks.YELLOW_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.LIME_TERRACOTTA).drops(Blocks.LIME_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.GREEN_TERRACOTTA).drops(Blocks.GREEN_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.CYAN_TERRACOTTA).drops(Blocks.CYAN_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.LIGHT_BLUE_TERRACOTTA).drops(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.BLUE_TERRACOTTA).drops(Blocks.BLUE_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.PURPLE_TERRACOTTA).drops(Blocks.PURPLE_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.MAGENTA_TERRACOTTA).drops(Blocks.MAGENTA_GLAZED_TERRACOTTA).save(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.PINK_TERRACOTTA).drops(Blocks.PINK_GLAZED_TERRACOTTA).save(exporter);
    }
}

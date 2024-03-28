package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.KilnRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
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
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredCrucibleBlock).drops(BwtBlocks.crucibleBlock).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredPlanterBlock).drops(BwtBlocks.planterBlock).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredVaseBlock).drops(BwtBlocks.vaseBlocks.get(DyeColor.WHITE)).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredUrnBlock).drops(BwtBlocks.urnBlock).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(BwtBlocks.unfiredMouldBlock).drops(BwtItems.mouldItem).offerTo(exporter);

        // Terracotta
        KilnRecipe.JsonBuilder.create(Blocks.CLAY).drops(Blocks.TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.WHITE_TERRACOTTA).drops(Blocks.WHITE_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.LIGHT_GRAY_TERRACOTTA).drops(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.GRAY_TERRACOTTA).drops(Blocks.GRAY_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.BLACK_TERRACOTTA).drops(Blocks.BLACK_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.BROWN_TERRACOTTA).drops(Blocks.BROWN_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.RED_TERRACOTTA).drops(Blocks.RED_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.ORANGE_TERRACOTTA).drops(Blocks.ORANGE_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.YELLOW_TERRACOTTA).drops(Blocks.YELLOW_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.LIME_TERRACOTTA).drops(Blocks.LIME_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.GREEN_TERRACOTTA).drops(Blocks.GREEN_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.CYAN_TERRACOTTA).drops(Blocks.CYAN_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.LIGHT_BLUE_TERRACOTTA).drops(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.BLUE_TERRACOTTA).drops(Blocks.BLUE_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.PURPLE_TERRACOTTA).drops(Blocks.PURPLE_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.MAGENTA_TERRACOTTA).drops(Blocks.MAGENTA_GLAZED_TERRACOTTA).offerTo(exporter);
        KilnRecipe.JsonBuilder.create(Blocks.PINK_TERRACOTTA).drops(Blocks.PINK_GLAZED_TERRACOTTA).offerTo(exporter);
    }
}

package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.recipes.kiln.KilnRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class KilnRecipeGenerator extends FabricRecipeProvider {
    public KilnRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new SharedRecipeProvider(registryLookup, output) {
            @Override
            public void buildRecipes() {
                // Ores
                kilnRecipe(BlockTags.IRON_ORES).drops(Items.IRON_INGOT).save(output);
                kilnRecipe(BlockTags.GOLD_ORES).drops(Items.GOLD_INGOT).save(output);
                kilnRecipe(BlockTags.COAL_ORES).drops(Items.COAL).save(output);
                kilnRecipe(BlockTags.COPPER_ORES).drops(Items.COPPER_INGOT).save(output);
                kilnRecipe(BlockTags.DIAMOND_ORES).drops(Items.DIAMOND).save(output);
                kilnRecipe(BlockTags.EMERALD_ORES).drops(Items.EMERALD).save(output);
                kilnRecipe(BlockTags.LAPIS_ORES).drops(Items.LAPIS_LAZULI).save(output);
                kilnRecipe(BlockTags.REDSTONE_ORES).drops(Items.REDSTONE).save(output);
                kilnRecipe(Blocks.ANCIENT_DEBRIS).drops(Items.NETHERITE_SCRAP).save(output);
                // Ore blocks
                kilnRecipe(ConventionalBlockTags.STORAGE_BLOCKS_RAW_IRON).drops(Items.IRON_INGOT, 9).save(output);
                kilnRecipe(ConventionalBlockTags.STORAGE_BLOCKS_RAW_GOLD).drops(Items.GOLD_INGOT, 9).save(output);
                kilnRecipe(ConventionalBlockTags.STORAGE_BLOCKS_RAW_COPPER).drops(Items.COPPER_INGOT, 9).save(output);
                // Charcoal
                kilnRecipe(BlockTags.LOGS).drops(Items.CHARCOAL).save(output);
                // Pottery
                kilnRecipe(BwtBlocks.unfiredDecoratedPotBlock).drops(Items.DECORATED_POT).save(output);
                kilnRecipe(BwtBlocks.unfiredDecoratedPotBlockWithSherds).drops(Items.DECORATED_POT).save(output);
                kilnRecipe(BwtBlocks.unfiredCrucibleBlock).drops(BwtBlocks.crucibleBlock).save(output);
                kilnRecipe(BwtBlocks.unfiredPlanterBlock).drops(BwtBlocks.planterBlock).save(output);
                kilnRecipe(BwtBlocks.unfiredVaseBlock).drops(BwtBlocks.vaseBlocks.get(DyeColor.WHITE)).save(output);
                kilnRecipe(BwtBlocks.unfiredUrnBlock).drops(BwtBlocks.urnBlock).save(output);
                kilnRecipe(BwtBlocks.unfiredFlowerPotBlock).drops(Items.FLOWER_POT).save(output);

                // Terracotta
                kilnRecipe(Blocks.CLAY).drops(Blocks.TERRACOTTA).save(output);
                kilnRecipe(Blocks.WHITE_TERRACOTTA).drops(Blocks.WHITE_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.LIGHT_GRAY_TERRACOTTA).drops(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.GRAY_TERRACOTTA).drops(Blocks.GRAY_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.BLACK_TERRACOTTA).drops(Blocks.BLACK_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.BROWN_TERRACOTTA).drops(Blocks.BROWN_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.RED_TERRACOTTA).drops(Blocks.RED_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.ORANGE_TERRACOTTA).drops(Blocks.ORANGE_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.YELLOW_TERRACOTTA).drops(Blocks.YELLOW_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.LIME_TERRACOTTA).drops(Blocks.LIME_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.GREEN_TERRACOTTA).drops(Blocks.GREEN_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.CYAN_TERRACOTTA).drops(Blocks.CYAN_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.LIGHT_BLUE_TERRACOTTA).drops(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.BLUE_TERRACOTTA).drops(Blocks.BLUE_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.PURPLE_TERRACOTTA).drops(Blocks.PURPLE_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.MAGENTA_TERRACOTTA).drops(Blocks.MAGENTA_GLAZED_TERRACOTTA).save(output);
                kilnRecipe(Blocks.PINK_TERRACOTTA).drops(Blocks.PINK_GLAZED_TERRACOTTA).save(output);
            }

            KilnRecipe.JsonBuilder kilnRecipe(Block block) {
                return KilnRecipe.JsonBuilder.create(block);
            }

            KilnRecipe.JsonBuilder kilnRecipe(TagKey<Block> blockTag) {
                return KilnRecipe.JsonBuilder.create(blocks, blockTag);
            }
        };
    }

    @Override
    public String getName() {
        return "KilnRecipeGenerator";
    }
}

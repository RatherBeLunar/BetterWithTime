package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class MillStoneRecipeGenerator extends FabricRecipeProvider {
    public MillStoneRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new SharedRecipeProvider(registryLookup, output) {
            @Override
            public void buildRecipes() {
                generateMillStoneRecipes(output);
            }

            private void generateMillStoneRecipes(RecipeOutput output) {
                create().ingredient(Items.COAL).result(BwtItems.coalDustItem).save(output);
                create().ingredient(Items.NETHERRACK).result(BwtItems.groundNetherrackItem).save(output);
                create().ingredient(BwtItems.hempItem).result(BwtItems.hempFiberItem, 4).save(output);
                create().ingredient(Items.LEATHER).result(BwtItems.scouredLeatherItem).save(output);
                create().ingredient(Items.WHEAT).result(BwtItems.flourItem).save(output);
                create().ingredient(Items.SUGAR_CANE).result(Items.SUGAR).save(output);
                create().ingredient(Items.BONE).result(Items.BONE_MEAL, 3).save(output);
                create().ingredient(BwtBlocks.companionCubeBlock.asItem())
                        .result(BwtItems.wolfChopItem).result(Items.RED_DYE, 3).result(Items.STRING, 10).save(output);

                // Dyes
                create().ingredient(Items.INK_SAC).result(Items.BLACK_DYE).save(output);
                create().ingredient(Items.LAPIS_LAZULI).result(Items.BLUE_DYE, 2).save(output);
                create().ingredient(Items.COCOA_BEANS).result(Items.BROWN_DYE).save(output);
                create().ingredient(Items.WITHER_ROSE).result(Items.BLACK_DYE).save(output);
                create().ingredient(Items.CORNFLOWER).result(Items.BLUE_DYE).save(output);
                create().ingredient(Items.PITCHER_PLANT).result(Items.CYAN_DYE, 2).save(output);
                create().ingredient(Items.BLUE_ORCHID).result(Items.LIGHT_BLUE_DYE).save(output);
                create().ingredient(Items.AZURE_BLUET).result(Items.LIGHT_GRAY_DYE).save(output);
                create().ingredient(Items.OXEYE_DAISY).result(Items.LIGHT_GRAY_DYE).save(output);
                create().ingredient(Items.WHITE_TULIP).result(Items.LIGHT_GRAY_DYE).save(output);
                create().ingredient(Items.ALLIUM).result(Items.MAGENTA_DYE).save(output);
                create().ingredient(Items.LILAC).result(Items.MAGENTA_DYE, 2).save(output);
                create().ingredient(Items.ORANGE_TULIP).result(Items.ORANGE_DYE).save(output);
                create().ingredient(Items.TORCHFLOWER).result(Items.ORANGE_DYE).save(output);
                create().ingredient(Items.PEONY).result(Items.PINK_DYE, 2).save(output);
                create().ingredient(Items.PINK_PETALS).result(Items.PINK_DYE).save(output);
                create().ingredient(Items.PINK_TULIP).result(Items.PINK_DYE).save(output);
                create().ingredient(Items.BEETROOT).result(Items.RED_DYE).save(output);
                create().ingredient(Items.POPPY).result(Items.RED_DYE).save(output);
                create().ingredient(Items.RED_TULIP).result(Items.RED_DYE).save(output);
                create().ingredient(Items.ROSE_BUSH).result(Items.RED_DYE, 2).save(output);
                create().ingredient(Items.BONE_MEAL).result(Items.WHITE_DYE).save(output);
                create().ingredient(Items.LILY_OF_THE_VALLEY).result(Items.WHITE_DYE).save(output);
                create().ingredient(Items.DANDELION).result(Items.YELLOW_DYE).save(output);
                create().ingredient(Items.SUNFLOWER).result(Items.YELLOW_DYE, 2).save(output);
            }

            private MillStoneRecipe.JsonBuilder create() {
                return MillStoneRecipe.JsonBuilder.create(this);
            }
        };
    }

    @Override
    public String getName() {
        return "MillStoneRecipeGenerator";
    }
}

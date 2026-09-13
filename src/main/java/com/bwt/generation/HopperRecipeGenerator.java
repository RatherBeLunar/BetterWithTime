package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class HopperRecipeGenerator extends FabricRecipeProvider {
    public HopperRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateHopperFilterRecipes(exporter);
        generateSoulBottlingRecipes(exporter);
    }

    protected void generateHopperFilterRecipes(RecipeOutput exporter) {
        HopperFilterRecipe.JsonBuilder.create().filter(BwtBlocks.wickerPaneBlock.asItem()).ingredient(Items.GRAVEL).result(Items.SAND).byproduct(Items.FLINT).save(exporter);
        HopperFilterRecipe.JsonBuilder.create().filter(Items.SOUL_SAND).ingredient(BwtItems.groundNetherrackItem).byproduct(BwtItems.hellfireDustItem).soulCount(1).markDefault().save(exporter);
        HopperFilterRecipe.JsonBuilder.create().filter(Items.SOUL_SAND).ingredient(BwtItems.soulDustItem).byproduct(BwtItems.sawDustItem).soulCount(1).save(exporter);
    }

    protected void generateSoulBottlingRecipes(RecipeOutput exporter) {
        SoulBottlingRecipe.JsonBuilder.create().bottle(BwtBlocks.urnBlock).soulCount(8).result(BwtItems.soulUrnItem).markDefault().save(exporter);
    }
}

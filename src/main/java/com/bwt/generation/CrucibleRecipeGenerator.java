package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.StokedCrucibleRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;

public class CrucibleRecipeGenerator extends FabricRecipeProvider {
    public CrucibleRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateUnstoked(exporter);
        generateStoked(exporter);
    }

    private void generateUnstoked(RecipeExporter exporter) {
        // Probably never gonna put anything here
    }

    private void generateStoked(RecipeExporter exporter) {
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_INGOT).ingredient(BwtItems.coalDustItem).ingredient(BwtItems.soulUrnItem).result(BwtItems.soulforgedSteelItem).offerTo(exporter, "soulforged_steel");
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.COBBLESTONE).result(Items.STONE).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.SAND).result(Items.GLASS).offerTo(exporter);

        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLD_INGOT).ingredient(BwtItems.concentratedHellfireItem, 9).result(Items.REDSTONE, 63).offerTo(exporter, "redstone_synthesis_from_gold_ingots");
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLD_NUGGET).ingredient(BwtItems.concentratedHellfireItem, 1).result(Items.REDSTONE, 7).offerTo(exporter, "redstone_synthesis_from_gold_nuggets");

        generateResmelting(exporter);
    }

    private void generateResmelting(RecipeExporter exporter) {
        // Iron armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_HELMET).result(Items.IRON_INGOT, 5).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_CHESTPLATE).result(Items.IRON_INGOT, 8).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_LEGGINGS).result(Items.IRON_INGOT, 7).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_BOOTS).result(Items.IRON_INGOT, 4).offerTo(exporter);
        // Iron tools
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_PICKAXE).result(Items.IRON_INGOT, 3).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_SHOVEL).result(Items.IRON_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_AXE).result(Items.IRON_INGOT, 3).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_HOE).result(Items.IRON_INGOT, 2).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_SWORD).result(Items.IRON_INGOT, 2).offerTo(exporter);
        // Chain armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_HELMET).result(Items.IRON_INGOT, 5).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_CHESTPLATE).result(Items.IRON_INGOT, 8).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_LEGGINGS).result(Items.IRON_INGOT, 7).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_BOOTS).result(Items.IRON_INGOT, 4).offerTo(exporter);
        // Gold armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_HELMET).result(Items.GOLD_INGOT, 5).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_CHESTPLATE).result(Items.GOLD_INGOT, 8).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_LEGGINGS).result(Items.GOLD_INGOT, 7).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_BOOTS).result(Items.GOLD_INGOT, 4).offerTo(exporter);
        // Gold tools
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_PICKAXE).result(Items.GOLD_INGOT, 3).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_SHOVEL).result(Items.GOLD_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_AXE).result(Items.GOLD_INGOT, 3).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_HOE).result(Items.GOLD_INGOT, 2).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_SWORD).result(Items.GOLD_INGOT, 2).offerTo(exporter);
        // TODO SFS armor and tools
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtBlocks.cauldronBlock.asItem()).result(Items.IRON_INGOT, 7).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.RAIL, 8).result(Items.IRON_INGOT, 3).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.POWERED_RAIL).result(Items.GOLD_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.DETECTOR_RAIL).result(Items.IRON_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtBlocks.stoneDetectorRailBlock.asItem()).result(Items.IRON_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtBlocks.obsidianDetectorRailBlock.asItem()).result(Items.IRON_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.COMPASS).result(Items.IRON_INGOT, 4).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CLOCK).result(Items.GOLD_INGOT, 4).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.MINECART).result(Items.IRON_INGOT, 5).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.SHEARS).result(Items.IRON_INGOT, 2).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.FLINT_AND_STEEL).result(Items.IRON_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLD_NUGGET, 9).result(Items.GOLD_INGOT, 1).offerTo(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_NUGGET, 9).result(Items.IRON_INGOT, 1).offerTo(exporter);
    }
}

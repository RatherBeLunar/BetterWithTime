package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.CauldronRecipe;
import com.bwt.recipes.MillStoneRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateMillStoneRecipes(exporter);
        generateFoods(exporter);
        generateOtherCauldronRecipes(exporter);
        generateCraftingRecipes(exporter);
    }

    public void generateCraftingRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.gearItem, 2)
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .input('s', Items.STICK)
                .input('p', ItemTags.PLANKS)
                .criterion(FabricRecipeProvider.hasItem(Items.STICK), FabricRecipeProvider.conditionsFromItem(Items.STICK))
                .criterion("has_planks", FabricRecipeProvider.conditionsFromTag(ItemTags.PLANKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.handCrankBlock)
                .pattern("  s")
                .pattern(" s ")
                .pattern("cgc")
                .input('s', Items.STICK)
                .input('c', ItemTags.STONE_CRAFTING_MATERIALS)
                .input('g', BwtItems.gearItem)
                .criterion(FabricRecipeProvider.hasItem(BwtItems.gearItem), FabricRecipeProvider.conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.millStoneBlock)
                .pattern("sss")
                .pattern("sss")
                .pattern("sgs")
                .input('s', Items.STONE)
                .input('g', BwtItems.gearItem)
                .criterion(FabricRecipeProvider.hasItem(BwtItems.gearItem), FabricRecipeProvider.conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .pattern("ibi")
                .pattern("iwi")
                .pattern("iii")
                .input('i', Items.IRON_INGOT)
                .input('b', Items.BONE)
                .input('w', Items.WATER_BUCKET)
                .criterion(FabricRecipeProvider.hasItem(Items.BONE), FabricRecipeProvider.conditionsFromItem(Items.BONE))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.lightBlockBlock)
                .pattern(" p ")
                .pattern("pfp")
                .pattern(" r ")
                .input('p', ConventionalItemTags.GLASS_PANES)
                .input('f', BwtItems.filamentItem)
                .input('r', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(BwtItems.filamentItem), FabricRecipeProvider.conditionsFromItem(BwtItems.filamentItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.fabricItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("fff")
                .input('f', BwtItems.hempFiberItem)
                .criterion(FabricRecipeProvider.hasItem(BwtItems.hempFiberItem), FabricRecipeProvider.conditionsFromItem(BwtItems.hempFiberItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("ppp")
                .input('f', BwtItems.fabricItem)
                .input('p', ItemTags.PLANKS)
                .criterion(FabricRecipeProvider.hasItem(BwtItems.fabricItem), FabricRecipeProvider.conditionsFromItem(BwtItems.fabricItem))
                .offerTo(exporter);
    }

    public void addNewGenericFood(Item input, Item output, RecipeExporter exporter) {
        CauldronRecipe.JsonBuilder.createFood().ingredient(input).result(output).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(input), RecipeCategory.FOOD, output, 0.35f, 200).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, 100, input, output, 0.35f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, CampfireCookingRecipe::new, 600, input, output, 0.35f);
    }

    protected void generateFoods(RecipeExporter exporter) {
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.BEEF).result(Items.COOKED_BEEF).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.CHICKEN).result(Items.COOKED_CHICKEN).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.COD).result(Items.COOKED_COD).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.KELP).result(Items.DRIED_KELP).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.SALMON).result(Items.COOKED_SALMON).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.MUTTON).result(Items.COOKED_MUTTON).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.PORKCHOP).result(Items.COOKED_PORKCHOP).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.POTATO).result(Items.BAKED_POTATO).offerTo(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.RABBIT).result(Items.COOKED_RABBIT).offerTo(exporter);
        addNewGenericFood(BwtItems.wolfChopItem, BwtItems.cookedWolfChopItem, exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(BwtItems.flourItem).result(BwtItems.donutItem).offerTo(exporter);
    }

    private void generateOtherCauldronRecipes(RecipeExporter exporter) {
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GLOWSTONE_DUST).ingredient(Items.REDSTONE).ingredient(BwtItems.hempFiberItem).result(BwtItems.filamentItem).offerTo(exporter);
    }

    protected void generateMillStoneRecipes(RecipeExporter exporter) {
        MillStoneRecipe.JsonBuilder.create().ingredient(BwtItems.hempItem).result(BwtItems.hempFiberItem, 4).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LEATHER).result(BwtItems.scouredLeatherItem).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.WHEAT).result(BwtItems.flourItem).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.SUGAR_CANE).result(Items.SUGAR).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BONE).result(Items.BONE_MEAL, 3).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(BwtBlocks.companionCubeBlock.asItem())
                .result(BwtItems.wolfChopItem).result(Items.RED_DYE, 3).result(Items.STRING, 10).offerTo(exporter);

        // Dyes
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.INK_SAC).result(Items.BLACK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LAPIS_LAZULI).result(Items.BLUE_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.COCOA_BEANS).result(Items.BROWN_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.WITHER_ROSE).result(Items.BLACK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.CORNFLOWER).result(Items.BLUE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PITCHER_PLANT).result(Items.CYAN_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BLUE_ORCHID).result(Items.LIGHT_BLUE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.AZURE_BLUET).result(Items.LIGHT_GRAY_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.OXEYE_DAISY).result(Items.LIGHT_GRAY_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.WHITE_TULIP).result(Items.LIGHT_GRAY_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.ALLIUM).result(Items.MAGENTA_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LILAC).result(Items.MAGENTA_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.ORANGE_TULIP).result(Items.ORANGE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.TORCHFLOWER).result(Items.ORANGE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PEONY).result(Items.PINK_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PINK_PETALS).result(Items.PINK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.PINK_TULIP).result(Items.PINK_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BEETROOT).result(Items.RED_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.POPPY).result(Items.RED_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.RED_TULIP).result(Items.RED_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.ROSE_BUSH).result(Items.RED_DYE, 2).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.BONE_MEAL).result(Items.WHITE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.LILY_OF_THE_VALLEY).result(Items.WHITE_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.DANDELION).result(Items.YELLOW_DYE).offerTo(exporter);
        MillStoneRecipe.JsonBuilder.create().ingredient(Items.SUNFLOWER).result(Items.YELLOW_DYE, 2).offerTo(exporter);
    }
}

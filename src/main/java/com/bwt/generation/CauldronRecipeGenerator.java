package com.bwt.generation;

import com.bwt.items.BwtItems;
import com.bwt.recipes.CauldronRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;

public class CauldronRecipeGenerator extends FabricRecipeProvider {
    public CauldronRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateFoods(exporter);
        generateOtherCauldronRecipes(exporter);

        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem, 8).result(BwtItems.concentratedHellfireItem).offerTo(exporter);
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
        // Foul foods
        Registries.ITEM.stream().filter(Item::isFood).filter(item -> !item.equals(BwtItems.foulFoodItem)).forEach(item -> {
            CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.dungItem).ingredient(item).result(BwtItems.foulFoodItem).offerTo(exporter, "foul_food_from_" + Registries.ITEM.getId(item).getPath());
        });
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.dungItem).ingredient(BwtItems.scouredLeatherItem).result(BwtItems.tannedLeatherItem).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GLOWSTONE_DUST).ingredient(Items.REDSTONE).ingredient(BwtItems.hempFiberItem).result(BwtItems.filamentItem).offerTo(exporter);
    }
}

package com.bwt.generation;

import com.bwt.items.BwtItems;
import com.bwt.recipes.CauldronRecipe;
import com.bwt.recipes.StokedCauldronRecipe;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.DyeUtils;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Util;

import java.util.Map;

public class CauldronRecipeGenerator extends FabricRecipeProvider {
    public CauldronRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateUnstoked(exporter);
        generateStoked(exporter);
    }

    private void generateUnstoked(RecipeExporter exporter) {
        generateFoods(exporter);
        // Foul foods
        Registries.ITEM.stream().filter(Item::isFood).filter(item -> !item.equals(BwtItems.foulFoodItem)).forEach(item -> {
            CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.dungItem).ingredient(item).result(BwtItems.foulFoodItem).offerTo(exporter, "foul_food_from_" + Registries.ITEM.getId(item).getPath());
        });
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.dungItem).ingredient(BwtItems.scouredLeatherItem).result(BwtItems.tannedLeatherItem).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GLOWSTONE_DUST).ingredient(Items.REDSTONE).ingredient(BwtItems.hempFiberItem).result(BwtItems.filamentItem).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem, 8).result(BwtItems.concentratedHellfireItem).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem).ingredient(BwtItems.coalDustItem).result(BwtItems.nethercoalItem, 2).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem, 8).ingredient(BwtItems.potashItem).result(BwtItems.netherSludgeItem, 8).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.SAND).ingredient(Items.GRAVEL).ingredient(BwtItems.soulUrnItem).ingredient(Items.BUCKET).result(BwtItems.cementBucketItem).offerTo(exporter);
        // TODO blood wood sapling
        // TODO nether groth spores
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GUNPOWDER, 5).ingredient(Items.SAND, 4).result(Items.TNT).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.CACTUS).result(Items.GREEN_DYE).offerTo(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.STRING, 4).result(Items.WHITE_WOOL).offerTo(exporter, RecipeProvider.getItemPath(Items.WHITE_WOOL) + "_from_string_in_cauldron");
        Registries.ITEM.stream().filter(item -> item instanceof DyeItem).forEach(dyeItem -> {
            Item dyedWool = DyeUtils.WOOL_COLORS.get(((DyeItem) dyeItem).getColor()).asItem();
            CauldronRecipe.JsonBuilder.create().ingredient(Items.WHITE_WOOL).ingredient(dyeItem).result(dyedWool).offerTo(exporter, RecipeProvider.getItemPath(dyedWool) + "_from_cauldron_dyeing_with_" + RecipeProvider.getItemPath(dyeItem));
        });
        DyeUtils.WOOL_COLORS.values().stream().map(ItemConvertible::asItem).forEach(woolItem ->
                CauldronRecipe.JsonBuilder.create().ingredient(woolItem).ingredient(BwtItems.potashItem).result(Items.WHITE_WOOL).offerTo(exporter, RecipeProvider.getItemPath(Items.WHITE_WOOL) + "_from_cauldron_washing_" + RecipeProvider.getItemPath(woolItem))
        );
    }

    private void generateStoked(RecipeExporter exporter) {
        // Glue
        Map<Item, Integer> GLUE_AMOUNTS = Util.make(Maps.newHashMap(), map -> {
            map.put(Items.LEATHER_HELMET, 5);
            map.put(Items.LEATHER_CHESTPLATE, 8);
            map.put(Items.LEATHER_LEGGINGS, 7);
            map.put(Items.LEATHER_BOOTS, 4);
            map.put(Items.SADDLE, 3);
            map.put(Items.LEATHER, 1);
            map.put(BwtItems.scouredLeatherItem, 1);
            map.put(BwtItems.tannedLeatherItem, 1);
            // TODO tanned leather armor, gimp armor, breeding harness
        });
        GLUE_AMOUNTS.forEach((key, value) -> StokedCauldronRecipe.JsonBuilder.create().ingredient(key).result(BwtItems.glueItem, value).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemPath(key)));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.strapItem, 8).result(BwtItems.glueItem, 1).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemPath(BwtItems.strapItem));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.beltItem, 2).result(BwtItems.glueItem, 1).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemPath(BwtItems.beltItem));
        // Tallow
        for (Item item : new Item[]{Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.BEEF, Items.COOKED_BEEF, Items.MUTTON, Items.COOKED_MUTTON, BwtItems.wolfChopItem, BwtItems.cookedWolfChopItem}) {
            StokedCauldronRecipe.JsonBuilder.create().ingredient(item).result(BwtItems.tallowItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemPath(item));
        }
        // Potash
        StokedCauldronRecipe.JsonBuilder.create().ingredient(ItemTags.LOGS).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_logs");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(ItemTags.PLANKS, 6).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_planks");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItemTags.WOODEN_SIDING_BLOCKS, 12).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_siding");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItemTags.WOODEN_MOULDING_BLOCKS, 24).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_moulding");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItemTags.WOODEN_CORNER_BLOCKS, 48).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_corners");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.sawDustItem, 16).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_saw_dust");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.soulDustItem, 16).result(BwtItems.potashItem).offerTo(exporter, RecipeProvider.getItemPath(BwtItems.potashItem) + "_from_cauldron_rendering_soul_dust");

        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.ARROW, 8).result(Items.FLINT, 2).result(Items.STICK).result(Items.FEATHER).offerTo(exporter, "cauldron_rendering_arrows");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.rottedArrowItem, 8).result(Items.FLINT, 2).offerTo(exporter, "cauldron_rendering_rotted_arrows");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.potashItem).ingredient(BwtItems.tallowItem).result(BwtItems.soapItem).offerTo(exporter);
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.ROTTEN_FLESH, 4).ingredient(Items.BONE_MEAL, 4).ingredient(Items.SUGAR).result(BwtItems.kibbleItem).offerTo(exporter);


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
        CauldronRecipe.JsonBuilder.createFood().ingredient(BwtItems.flourItem).result(BwtItems.donutItem, 4).offerTo(exporter);
    }
}

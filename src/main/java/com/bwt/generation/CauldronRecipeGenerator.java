package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.cooking_pots.CauldronRecipe;
import com.bwt.recipes.cooking_pots.StokedCauldronRecipe;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.Id;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CauldronRecipeGenerator extends FabricRecipeProvider {
    public CauldronRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateUnstoked(exporter);
        generateStoked(exporter);
    }

    private void generateUnstoked(RecipeOutput exporter) {
        generateFoods(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.dungItem).ingredient(BwtItems.scouredLeatherItem)
                .result(BwtItems.tannedLeatherItem).markDefault().save(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GLOWSTONE_DUST).ingredient(Items.REDSTONE).ingredient(BwtItems.hempFiberItem)
                .result(BwtItems.filamentItem).group("filament").markDefault().save(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GLOWSTONE_DUST).ingredient(Items.REDSTONE).ingredient(Items.STRING)
                .result(BwtItems.filamentItem).group("filament").save(exporter, RecipeProvider.getItemName(BwtItems.filamentItem) + "_from_string");
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem, 8)
                .result(BwtItems.concentratedHellfireItem).markDefault().group("concentrated_hellfire").save(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem).ingredient(BwtItems.coalDustItem).result(BwtItems.nethercoalItem, 2)
                .markDefault().save(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.hellfireDustItem, 8).ingredient(BwtItems.potashItem).result(Items.NETHER_BRICK, 8).save(exporter);
//        CauldronRecipe.JsonBuilder.create().ingredient(Items.SAND).ingredient(Items.GRAVEL).ingredient(BwtItems.soulUrnItem).ingredient(Items.BUCKET)
//                .result(BwtItems.cementBucketItem).markDefault().offerTo(exporter);
        CauldronRecipe.JsonBuilder.create()
                .ingredient(Items.OAK_SAPLING)
                .ingredient(Items.NETHER_WART)
                .ingredient(Items.RED_MUSHROOM)
                .ingredient(Items.BROWN_MUSHROOM)
                .ingredient(Items.CRIMSON_FUNGUS)
                .ingredient(Items.WARPED_FUNGUS)
                .ingredient(BwtItems.soulUrnItem, 8)
                .result(BwtBlocks.bloodWoodBlocks.saplingItem)
                .markDefault()
                .save(exporter);
        CauldronRecipe.JsonBuilder.create()
                .ingredient(Items.NETHER_WART)
                .ingredient(Items.RED_MUSHROOM)
                .ingredient(Items.BROWN_MUSHROOM)
                .ingredient(Items.MYCELIUM)
                .ingredient(BwtItems.dungItem)
                .ingredient(BwtItems.soulUrnItem, 8)
                .result(BwtBlocks.netherGroth.asItem())
                .result(Items.DIRT)
                .save(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.GUNPOWDER, 5).ingredient(Items.SAND, 4).result(Items.TNT).save(exporter);
        CauldronRecipe.JsonBuilder.create().ingredient(Items.CACTUS).result(Items.GREEN_DYE).save(exporter);
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof DyeItem).forEach(dyeItem -> {
            Item dyedWool = DyeUtils.WOOL_COLORS.get(((DyeItem) dyeItem).getDyeColor()).asItem();
            Item dyedWoolSlab = BwtBlocks.woolSlabBlocks.get(((DyeItem) dyeItem).getDyeColor()).asItem();
            CauldronRecipe.JsonBuilder.create().ingredient(Items.WHITE_WOOL).ingredient(dyeItem).result(dyedWool).save(exporter, RecipeProvider.getItemName(dyedWool) + "_from_cauldron_dyeing_with_" + RecipeProvider.getItemName(dyeItem));
            CauldronRecipe.JsonBuilder.create().ingredient(BwtBlocks.woolSlabBlocks.get(DyeColor.WHITE).asItem()).ingredient(dyeItem).result(dyedWoolSlab).save(exporter, RecipeProvider.getItemName(dyedWoolSlab) + "_from_cauldron_dyeing_with_" + RecipeProvider.getItemName(dyeItem));
        });
        DyeUtils.WOOL_COLORS.values().stream().map(ItemLike::asItem).forEach(woolItem ->
                CauldronRecipe.JsonBuilder.create().ingredient(woolItem).ingredient(BwtItems.potashItem).result(Items.WHITE_WOOL).save(exporter, RecipeProvider.getItemName(Items.WHITE_WOOL) + "_from_cauldron_washing_" + RecipeProvider.getItemName(woolItem))
        );
        BwtBlocks.woolSlabBlocks.values().stream().map(ItemLike::asItem).forEach(woolSlabItem ->
                CauldronRecipe.JsonBuilder.create().ingredient(woolSlabItem).ingredient(BwtItems.potashItem).result(Items.WHITE_WOOL).save(exporter, RecipeProvider.getItemName(Items.WHITE_WOOL) + "_from_cauldron_washing_" + RecipeProvider.getItemName(woolSlabItem))
        );
        CauldronRecipe.JsonBuilder.create().ingredient(BwtItems.soapItem).ingredient(Items.STICKY_PISTON, 4).result(Items.PISTON, 4).save(exporter, Id.of("sticky_piston_washing"));
    }

    private void generateStoked(RecipeOutput exporter) {
        // Glue
        Map<Item, Integer> GLUE_AMOUNTS = Util.make(Maps.newHashMap(), map -> {
            map.put(Items.LEATHER_HELMET, 5);
            map.put(Items.LEATHER_CHESTPLATE, 8);
            map.put(Items.LEATHER_LEGGINGS, 7);
            map.put(Items.LEATHER_BOOTS, 4);
            map.put(Items.LEATHER_HORSE_ARMOR, 7);
            map.put(Items.SADDLE, 3);
            map.put(Items.LEATHER, 1);
            map.put(BwtItems.scouredLeatherItem, 1);
            map.put(BwtItems.tannedLeatherItem, 1);
            // TODO tanned leather armor, gimp armor, breeding harness
        });
        GLUE_AMOUNTS.forEach((key, value) -> StokedCauldronRecipe.JsonBuilder.create().ingredient(key).result(BwtItems.glueItem, value).save(exporter, RecipeProvider.getItemName(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(key)));
        EmiDefaultsGenerator.addDefaultRecipe("/" + RecipeProvider.getItemName(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.LEATHER));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.strapItem, 8).result(BwtItems.glueItem, 1).save(exporter, RecipeProvider.getItemName(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(BwtItems.strapItem));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.beltItem, 2).result(BwtItems.glueItem, 1).save(exporter, RecipeProvider.getItemName(BwtItems.glueItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(BwtItems.beltItem));
        // Tallow
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.PORKCHOP)
                .result(BwtItems.tallowItem)
                .markDefault()
                .save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.PORKCHOP));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.COOKED_PORKCHOP).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.COOKED_PORKCHOP));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.wolfChopItem, 8).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(BwtItems.wolfChopItem));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.cookedWolfChopItem, 8).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(BwtItems.cookedWolfChopItem));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.BEEF, 4).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.BEEF));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.COOKED_BEEF, 4).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.COOKED_BEEF));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.MUTTON, 4).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.MUTTON));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.COOKED_MUTTON, 4).result(BwtItems.tallowItem).save(exporter, RecipeProvider.getItemName(BwtItems.tallowItem) + "_from_cauldron_rendering_" + RecipeProvider.getItemName(Items.COOKED_MUTTON));
        // Potash
        StokedCauldronRecipe.JsonBuilder.create().ingredient(ItemTags.LOGS).result(BwtItems.potashItem).save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_logs");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(ItemTags.PLANKS, 6)
                .result(BwtItems.potashItem)
                .markDefault()
                .save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_planks");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItemTags.WOODEN_SIDING_BLOCKS, 12).result(BwtItems.potashItem).save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_siding");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItemTags.WOODEN_MOULDING_BLOCKS, 24).result(BwtItems.potashItem).save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_moulding");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItemTags.WOODEN_CORNER_BLOCKS, 48).result(BwtItems.potashItem).save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_corners");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.sawDustItem, 16).result(BwtItems.potashItem).save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_saw_dust");
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.soulDustItem, 16).result(BwtItems.potashItem).save(exporter, RecipeProvider.getItemName(BwtItems.potashItem) + "_from_cauldron_rendering_soul_dust");

        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.ARROW, 8).result(Items.FLINT, 2).result(Items.STICK).result(Items.FEATHER).save(exporter, Id.of("cauldron_rendering_arrows"));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.rottedArrowItem, 8).result(Items.FLINT, 2).save(exporter, Id.of("cauldron_rendering_rotted_arrows"));
        StokedCauldronRecipe.JsonBuilder.create().ingredient(BwtItems.potashItem).ingredient(BwtItems.tallowItem)
                .result(BwtItems.soapItem)
                .group("soap")
                .markDefault()
                .save(exporter);
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.ROTTEN_FLESH, 4).ingredient(Items.BONE_MEAL, 4).ingredient(Items.SUGAR)
                .result(BwtItems.kibbleItem)
                .markDefault()
                .save(exporter);
        StokedCauldronRecipe.JsonBuilder.create().ingredient(Items.BOW).result(Items.STICK, 2).result(Items.STRING, 2).save(exporter, Id.of("cauldron_bow_recycling"));
    }


    public void addNewGenericFood(Item input, Item output, RecipeOutput exporter) {
        CauldronRecipe.JsonBuilder.createFood().ingredient(input).result(output).save(exporter);
        addNewVanillaCookingRecipe(input, output, exporter);
    }

    public void addNewVanillaCookingRecipe(Item input, Item output, RecipeOutput exporter) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, 0.35f, 200).unlockedBy(RecipeProvider.getHasName(input), RecipeProvider.has(input)).save(exporter);
        RecipeProvider.simpleCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, 100, input, output, 0.35f);
        RecipeProvider.simpleCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, 600, input, output, 0.35f);
    }

    protected void generateFoods(RecipeOutput exporter) {
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.BEEF).result(Items.COOKED_BEEF).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.CHICKEN).result(Items.COOKED_CHICKEN).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.COD).result(Items.COOKED_COD).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.KELP).result(Items.DRIED_KELP).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.SALMON).result(Items.COOKED_SALMON).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.MUTTON).result(Items.COOKED_MUTTON).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.PORKCHOP).result(Items.COOKED_PORKCHOP).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.POTATO).result(Items.BAKED_POTATO).save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(Items.RABBIT).result(Items.COOKED_RABBIT).save(exporter);
        addNewGenericFood(BwtItems.wolfChopItem, BwtItems.cookedWolfChopItem, exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(BwtItems.flourItem)
                .result(BwtItems.donutItem, 4)
                .markDefault()
                .save(exporter);
        CauldronRecipe.JsonBuilder.createFood().ingredient(BwtItems.rawEggItem)
                .result(BwtItems.poachedEggItem)
                .markDefault()
                .save(exporter);
        addNewVanillaCookingRecipe(BwtItems.rawEggItem, BwtItems.friedEggItem, exporter);
        CauldronRecipe.JsonBuilder.createFood()
                .ingredient(Items.MILK_BUCKET, 3)
                .ingredient(Items.SUGAR, 2)
                .ingredient(BwtItems.flourItem, 3)
                .ingredient(BwtItems.rawEggItem, 1)
                .result(Items.CAKE)
                .markDefault()
                .save(exporter);
        CauldronRecipe.JsonBuilder.createFood()
                .ingredient(BwtItems.flourItem, 2)
                .ingredient(Items.COCOA_BEANS)
                .result(Items.COOKIE, 8)
                .markDefault()
                .save(exporter);
    }
}

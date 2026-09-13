package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.cooking_pots.AbstractCookingPotRecipe;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

import static com.bwt.recipes.cooking_pots.AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM;


public class CrucibleRecipeGenerator extends FabricRecipeProvider {
    public CrucibleRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateUnstoked(exporter);
        generateStoked(exporter);
    }

    private void generateUnstoked(RecipeOutput exporter) {
        // Probably never gonna put anything here
    }

    private void generateStoked(RecipeOutput exporter) {
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_INGOT).ingredient(Items.GOLD_INGOT).ingredient(BwtItems.coalDustItem).ingredient(BwtItems.soulUrnItem).result(Items.NETHERITE_INGOT).markDefault().save(exporter, Id.of("netherite_ingot_smelting"));
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_SCRAP, 4).ingredient(Items.GOLD_INGOT, 4).result(Items.NETHERITE_INGOT).save(exporter, Id.of("netherite_ingot_from_scrap"));
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.COBBLESTONE).result(Items.STONE).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.SAND).result(Items.GLASS).save(exporter);

        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLD_INGOT).ingredient(BwtItems.concentratedHellfireItem, 9).result(Items.REDSTONE, 63).save(exporter, Id.of("redstone_synthesis_from_gold_ingots"));
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLD_NUGGET).ingredient(BwtItems.concentratedHellfireItem, 1).result(Items.REDSTONE, 7).save(exporter, Id.of("redstone_synthesis_from_gold_nuggets"));

        generateResmelting(exporter);
    }

    private void generateResmelting(RecipeOutput exporter) {
        // Iron armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_HELMET).result(Items.IRON_INGOT, 5).cookingCategory(RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_CHESTPLATE).result(Items.IRON_INGOT, 8).cookingCategory(RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_LEGGINGS).result(Items.IRON_INGOT, 7).cookingCategory(RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_BOOTS).result(Items.IRON_INGOT, 4).cookingCategory(RECLAIM).save(exporter);
        // Iron tools
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_PICKAXE).result(Items.IRON_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_SHOVEL).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_AXE).result(Items.IRON_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_HOE).result(Items.IRON_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_SWORD).result(Items.IRON_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        // Chain armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_HELMET).result(Items.IRON_INGOT, 5).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_CHESTPLATE).result(Items.IRON_INGOT, 8).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_LEGGINGS).result(Items.IRON_INGOT, 7).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CHAINMAIL_BOOTS).result(Items.IRON_INGOT, 4).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        // Gold armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_HELMET).result(Items.GOLD_INGOT, 5).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_CHESTPLATE).result(Items.GOLD_INGOT, 8).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_LEGGINGS).result(Items.GOLD_INGOT, 7).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_BOOTS).result(Items.GOLD_INGOT, 4).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        // Gold tools
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_PICKAXE).result(Items.GOLD_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_SHOVEL).result(Items.GOLD_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_AXE).result(Items.GOLD_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_HOE).result(Items.GOLD_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_SWORD).result(Items.GOLD_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        // Netherite armor
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_HELMET).result(Items.NETHERITE_INGOT, 8).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_CHESTPLATE).result(Items.NETHERITE_INGOT, 12).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_LEGGINGS).result(Items.NETHERITE_INGOT, 6).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_BOOTS).result(Items.NETHERITE_INGOT, 6).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        // Netherite tools and block
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtItems.netheriteMattockItem).result(Items.NETHERITE_INGOT, 4).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtItems.netheriteBattleAxeItem).result(Items.NETHERITE_INGOT, 5).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_PICKAXE).result(Items.NETHERITE_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_SHOVEL).result(Items.NETHERITE_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_AXE).result(Items.NETHERITE_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_HOE).result(Items.NETHERITE_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_SWORD).result(Items.NETHERITE_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.NETHERITE_BLOCK).result(Items.NETHERITE_INGOT, 16).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);

        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtBlocks.cauldronBlock.asItem()).result(Items.IRON_INGOT, 7).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.RAIL, 8).result(Items.IRON_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.POWERED_RAIL).result(Items.GOLD_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.DETECTOR_RAIL).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_DOOR).result(Items.IRON_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_TRAPDOOR).result(Items.IRON_INGOT, 4).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtBlocks.stoneDetectorRailBlock.asItem()).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(BwtBlocks.obsidianDetectorRailBlock.asItem()).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.COMPASS).result(Items.IRON_INGOT, 4).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CLOCK).result(Items.GOLD_INGOT, 4).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.MINECART).result(Items.IRON_INGOT, 5).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.SHEARS).result(Items.IRON_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.FLINT_AND_STEEL).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.CROSSBOW).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_HORSE_ARMOR).result(Items.IRON_INGOT, 7).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLDEN_HORSE_ARMOR).result(Items.IRON_INGOT, 7).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.GOLD_NUGGET, 9).result(Items.GOLD_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.IRON_NUGGET, 9).result(Items.IRON_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);

        // Copper stuff
//        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.COPPER_NUGGET, 9).result(Items.COPPER_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).offerTo(exporter);
        for (Item copperBulb : new Item[]{
                Items.COPPER_BULB, Items.WAXED_COPPER_BULB,
                Items.EXPOSED_COPPER_BULB, Items.WAXED_EXPOSED_COPPER_BULB,
                Items.WEATHERED_COPPER_BULB, Items.WAXED_WEATHERED_COPPER_BULB,
                Items.OXIDIZED_COPPER_BULB, Items.WAXED_OXIDIZED_COPPER_BULB,
        }) {
            StokedCrucibleRecipe.JsonBuilder.create().ingredient(copperBulb).result(Items.COPPER_INGOT, 6).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        }
        for (Item copperDoor : new Item[]{
                Items.COPPER_DOOR, Items.WAXED_COPPER_DOOR,
                Items.EXPOSED_COPPER_DOOR, Items.WAXED_EXPOSED_COPPER_DOOR,
                Items.WEATHERED_COPPER_DOOR, Items.WAXED_WEATHERED_COPPER_DOOR,
                Items.OXIDIZED_COPPER_DOOR, Items.WAXED_OXIDIZED_COPPER_DOOR,
        }) {
            StokedCrucibleRecipe.JsonBuilder.create().ingredient(copperDoor).result(Items.COPPER_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        }
        for (Item copperTrapDoor : new Item[]{
                Items.COPPER_TRAPDOOR, Items.WAXED_COPPER_TRAPDOOR,
                Items.EXPOSED_COPPER_TRAPDOOR, Items.WAXED_EXPOSED_COPPER_TRAPDOOR,
                Items.WEATHERED_COPPER_TRAPDOOR, Items.WAXED_WEATHERED_COPPER_TRAPDOOR,
                Items.OXIDIZED_COPPER_TRAPDOOR, Items.WAXED_OXIDIZED_COPPER_TRAPDOOR,
        }) {
            // TODO change this to 4 once the recipe changes in 1.21.9
            StokedCrucibleRecipe.JsonBuilder.create().ingredient(copperTrapDoor).result(Items.COPPER_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        }
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.BRUSH).result(Items.COPPER_INGOT, 1).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.LIGHTNING_ROD).result(Items.COPPER_INGOT, 3).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
        StokedCrucibleRecipe.JsonBuilder.create().ingredient(Items.SPYGLASS).result(Items.COPPER_INGOT, 2).cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM).save(exporter);
    }
}

package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import com.bwt.recipes.cooking_pots.AbstractCookingPotRecipe;
import com.bwt.recipes.cooking_pots.StokedCrucibleRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


public class CrucibleRecipeGenerator extends FabricRecipeProvider {
    public CrucibleRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new SharedRecipeProvider(registryLookup, output) {
            @Override
            public void buildRecipes() {
                generateUnstoked();
                generateStoked();
            }

            private void generateUnstoked() {
                // Probably never gonna put anything here
            }

            private void generateStoked() {
                createStoked().ingredient(Items.IRON_INGOT).ingredient(Items.GOLD_INGOT).ingredient(BwtItems.coalDustItem).ingredient(BwtItems.soulUrnItem).result(Items.NETHERITE_INGOT).save(output, "netherite_ingot_smelting");
                createStoked().ingredient(Items.NETHERITE_SCRAP, 4).ingredient(Items.GOLD_INGOT, 4).result(Items.NETHERITE_INGOT).save(output, "netherite_ingot_from_scrap");
                createStoked().ingredient(Items.COBBLESTONE).result(Items.STONE).save(output);
                createStoked().ingredient(Items.SAND).result(Items.GLASS).save(output);

                createStoked().ingredient(Items.GOLD_INGOT).ingredient(BwtItems.concentratedHellfireItem, 9).result(Items.REDSTONE, 63).save(output, "redstone_synthesis_from_gold_ingots");
                createStoked().ingredient(Items.GOLD_NUGGET).ingredient(BwtItems.concentratedHellfireItem, 1).result(Items.REDSTONE, 7).save(output, "redstone_synthesis_from_gold_nuggets");

                generateResmelting();
            }

            private void generateResmelting() {
                // Chain armor
                reclaim(Items.CHAINMAIL_HELMET, Items.IRON_INGOT, 5);
                reclaim(Items.CHAINMAIL_CHESTPLATE, Items.IRON_INGOT, 8);
                reclaim(Items.CHAINMAIL_LEGGINGS, Items.IRON_INGOT, 7);
                reclaim(Items.CHAINMAIL_BOOTS, Items.IRON_INGOT, 4);

                resmeltIronStuff();
                resmeltCopperStuff();
                resmeltGoldStuff();
                resmeltNetheriteStuff();
            }

            private void resmeltIronStuff() {
                // Iron armor
                reclaim(Items.IRON_HELMET, Items.IRON_INGOT, 5);
                reclaim(Items.IRON_CHESTPLATE, Items.IRON_INGOT, 8);
                reclaim(Items.IRON_LEGGINGS, Items.IRON_INGOT, 7);
                reclaim(Items.IRON_BOOTS, Items.IRON_INGOT, 4);
                // Iron tools
                reclaim(Items.IRON_PICKAXE, Items.IRON_INGOT, 3);
                reclaim(Items.IRON_SHOVEL, Items.IRON_INGOT, 1);
                reclaim(Items.IRON_AXE, Items.IRON_INGOT, 3);
                reclaim(Items.IRON_HOE, Items.IRON_INGOT, 2);
                reclaim(Items.IRON_SWORD, Items.IRON_INGOT, 2);
                // Iron misc
                reclaim(Items.ACTIVATOR_RAIL, Items.IRON_INGOT, 1);
                reclaim().ingredient(ItemTags.ANVIL).result(Items.IRON_INGOT, 9).save(output, "smelt_anvil_in_crucible");
                reclaim(Items.BUCKET, Items.IRON_INGOT, 3);
                reclaim().ingredient(Items.CAULDRON).result(Items.IRON_INGOT, 7).save(output, "smelt_vanilla_cauldron_in_crucible");
                reclaim(BwtBlocks.cauldronBlock, Items.IRON_INGOT, 7);
                reclaim(Items.COMPASS, Items.IRON_INGOT, 4);
                reclaim(Items.CROSSBOW, Items.IRON_INGOT, 1);
                reclaim(Items.DETECTOR_RAIL, Items.IRON_INGOT, 1);
                reclaim(Items.FLINT_AND_STEEL, Items.IRON_INGOT, 1);
                reclaim(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT, 2);
                reclaim(Items.IRON_BARS, 8, Items.IRON_INGOT, 3);
                reclaim(Items.IRON_CHAIN, Items.IRON_INGOT, 1);
                reclaim(Items.IRON_DOOR, Items.IRON_INGOT, 2);
                reclaim(Items.IRON_TRAPDOOR, Items.IRON_INGOT, 4);
                reclaim(Items.MINECART, Items.IRON_INGOT, 5);
                reclaim(Items.RAIL, 8, Items.IRON_INGOT, 3);
                reclaim(Items.SHEARS, Items.IRON_INGOT, 2);

                reclaim(Items.IRON_HORSE_ARMOR, Items.IRON_INGOT, 7);

                reclaim(BwtBlocks.stoneDetectorRailBlock, Items.IRON_INGOT, 1);
                reclaim(BwtBlocks.obsidianDetectorRailBlock, Items.IRON_INGOT, 1);

                reclaim(Items.IRON_NUGGET, 9, Items.IRON_INGOT, 1);
            }

            private void resmeltCopperStuff() {
                // Copper armor
                reclaim(Items.COPPER_HELMET, Items.COPPER_INGOT, 5);
                reclaim(Items.COPPER_CHESTPLATE, Items.COPPER_INGOT, 8);
                reclaim(Items.COPPER_LEGGINGS, Items.COPPER_INGOT, 7);
                reclaim(Items.COPPER_BOOTS, Items.COPPER_INGOT, 4);
                // Copper tools
                reclaim(Items.COPPER_PICKAXE, Items.COPPER_INGOT, 3);
                reclaim(Items.COPPER_SHOVEL, Items.COPPER_INGOT, 1);
                reclaim(Items.COPPER_AXE, Items.COPPER_INGOT, 3);
                reclaim(Items.COPPER_HOE, Items.COPPER_INGOT, 2);
                reclaim(Items.COPPER_SWORD, Items.COPPER_INGOT, 2);
                // Copper misc
                reclaim(Items.BRUSH, Items.COPPER_INGOT, 1);
                Items.COPPER_BARS.forEach(copperBars -> reclaim(copperBars, 8, Items.COPPER_INGOT, 3));
                Items.COPPER_CHAIN.forEach(copperChain -> reclaim(copperChain, Items.COPPER_INGOT, 1));
                Stream.of(
                        Items.COPPER_CHEST, Items.WAXED_COPPER_CHEST,
                        Items.EXPOSED_COPPER_CHEST, Items.WAXED_EXPOSED_COPPER_CHEST,
                        Items.WEATHERED_COPPER_CHEST, Items.WAXED_WEATHERED_COPPER_CHEST,
                        Items.OXIDIZED_COPPER_CHEST, Items.WAXED_OXIDIZED_COPPER_CHEST
                ).forEach(copperChest ->
                        reclaim(copperChest, Items.COPPER_INGOT, 8)
                );
                Stream.of(
                        Items.COPPER_DOOR, Items.WAXED_COPPER_DOOR,
                        Items.EXPOSED_COPPER_DOOR, Items.WAXED_EXPOSED_COPPER_DOOR,
                        Items.WEATHERED_COPPER_DOOR, Items.WAXED_WEATHERED_COPPER_DOOR,
                        Items.OXIDIZED_COPPER_DOOR, Items.WAXED_OXIDIZED_COPPER_DOOR
                ).forEach(copperDoor ->
                        reclaim(copperDoor, Items.COPPER_INGOT, 2)
                );
                Stream.of(
                        Items.COPPER_TRAPDOOR, Items.WAXED_COPPER_TRAPDOOR,
                        Items.EXPOSED_COPPER_TRAPDOOR, Items.WAXED_EXPOSED_COPPER_TRAPDOOR,
                        Items.WEATHERED_COPPER_TRAPDOOR, Items.WAXED_WEATHERED_COPPER_TRAPDOOR,
                        Items.OXIDIZED_COPPER_TRAPDOOR, Items.WAXED_OXIDIZED_COPPER_TRAPDOOR
                ).forEach(copperTrapDoor ->
                        reclaim(copperTrapDoor, Items.COPPER_INGOT, 4)
                );
                reclaim(Items.LIGHTNING_ROD, Items.COPPER_INGOT, 3);
                reclaim(Items.SPYGLASS, Items.COPPER_INGOT, 2);

                Stream.of(
                        Items.COPPER_BULB, Items.WAXED_COPPER_BULB,
                        Items.EXPOSED_COPPER_BULB, Items.WAXED_EXPOSED_COPPER_BULB,
                        Items.WEATHERED_COPPER_BULB, Items.WAXED_WEATHERED_COPPER_BULB,
                        Items.OXIDIZED_COPPER_BULB, Items.WAXED_OXIDIZED_COPPER_BULB
                ).forEach(copperBulb ->
                        reclaim(copperBulb, Items.COPPER_INGOT, 6)
                );

                reclaim(Items.COPPER_NUGGET, 9, Items.COPPER_INGOT, 1);
            }

            private void resmeltGoldStuff() {
                // Gold armor
                reclaim(Items.GOLDEN_HELMET, Items.GOLD_INGOT, 5);
                reclaim(Items.GOLDEN_CHESTPLATE, Items.GOLD_INGOT, 8);
                reclaim(Items.GOLDEN_LEGGINGS, Items.GOLD_INGOT, 7);
                reclaim(Items.GOLDEN_BOOTS, Items.GOLD_INGOT, 4);
                // Gold tools
                reclaim(Items.GOLDEN_PICKAXE, Items.GOLD_INGOT, 3);
                reclaim(Items.GOLDEN_SHOVEL, Items.GOLD_INGOT, 1);
                reclaim(Items.GOLDEN_AXE, Items.GOLD_INGOT, 3);
                reclaim(Items.GOLDEN_HOE, Items.GOLD_INGOT, 2);
                reclaim(Items.GOLDEN_SWORD, Items.GOLD_INGOT, 2);
                // Gold misc
                reclaim(Items.CLOCK, Items.GOLD_INGOT, 4);
                reclaim(Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, 2);
                reclaim(Items.POWERED_RAIL, Items.GOLD_INGOT, 1);

                reclaim(Items.GOLDEN_HORSE_ARMOR, Items.GOLD_INGOT, 7);

                reclaim(Items.GOLD_NUGGET, 9, Items.GOLD_INGOT, 1);
            }

            private void resmeltNetheriteStuff() {
                // Netherite armor
                reclaim(Items.NETHERITE_HELMET, Items.NETHERITE_INGOT, 8);
                reclaim(Items.NETHERITE_CHESTPLATE, Items.NETHERITE_INGOT, 12);
                reclaim(Items.NETHERITE_LEGGINGS, Items.NETHERITE_INGOT, 6);
                reclaim(Items.NETHERITE_BOOTS, Items.NETHERITE_INGOT, 6);
                // Netherite tools and block
                reclaim(BwtItems.netheriteMattockItem, Items.NETHERITE_INGOT, 4);
                reclaim(BwtItems.netheriteBattleAxeItem, Items.NETHERITE_INGOT, 5);
                reclaim(Items.NETHERITE_PICKAXE, Items.NETHERITE_INGOT, 3);
                reclaim(Items.NETHERITE_SHOVEL, Items.NETHERITE_INGOT, 1);
                reclaim(Items.NETHERITE_AXE, Items.NETHERITE_INGOT, 3);
                reclaim(Items.NETHERITE_HOE, Items.NETHERITE_INGOT, 2);
                reclaim(Items.NETHERITE_SWORD, Items.NETHERITE_INGOT, 2);
                reclaim(Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT, 16);
            }

            StokedCrucibleRecipe.JsonBuilder createStoked() {
                return StokedCrucibleRecipe.JsonBuilder.create(this);
            }

            StokedCrucibleRecipe.JsonBuilder reclaim() {
                return (StokedCrucibleRecipe.JsonBuilder) createStoked().cookingCategory(AbstractCookingPotRecipe.CookingPotRecipeCategory.RECLAIM);
            }

            void reclaim(ItemLike input, Item result, int resultCount) {
                reclaim().ingredient(input.asItem()).result(result, resultCount).save(output);
            }

            void reclaim(ItemLike input, int inputCount, Item result, int resultCount) {
                reclaim().ingredient(input.asItem(), inputCount).result(result, resultCount).save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "CrucibleRecipeGenerator";
    }
}

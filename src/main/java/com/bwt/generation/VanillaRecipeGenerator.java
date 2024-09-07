package com.bwt.generation;

import com.bwt.items.BwtItems;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class VanillaRecipeGenerator extends FabricRecipeProvider {
    public VanillaRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.DETECTOR_RAIL, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .input('i', Items.IRON_INGOT)
                .input('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(Blocks.RAIL), conditionsFromItem(Blocks.RAIL))
                .offerTo(exporter);

        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(BwtItems.flourItem), RecipeCategory.FOOD, Items.BREAD, 0.35f, 200).criterion(RecipeProvider.hasItem(BwtItems.flourItem), RecipeProvider.conditionsFromItem(BwtItems.flourItem)).offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.netheriteMattockItem)
                .pattern("sss ")
                .pattern(" h s")
                .pattern(" h  ")
                .pattern(" h  ")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.netheriteBattleAxeItem)
                .pattern("sss")
                .pattern("shs")
                .pattern(" h ")
                .pattern(" h ")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
    }
}

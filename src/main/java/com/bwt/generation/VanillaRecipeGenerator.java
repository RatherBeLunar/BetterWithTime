package com.bwt.generation;

import com.bwt.items.BwtItems;
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
import net.minecraft.registry.tag.ItemTags;

public class VanillaRecipeGenerator extends FabricRecipeProvider {
    public VanillaRecipeGenerator(FabricDataOutput output) {
        super(output);
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
    }
}

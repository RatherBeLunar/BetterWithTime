package com.bwt.generation;

import com.bwt.items.BwtItems;
import com.bwt.utils.Id;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class VanillaRecipeGenerator extends FabricRecipeProvider {
    public VanillaRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // The detector rail recipe needs to be entirely replaced with no option of re-insertion,
        // since we need it to use the wooden plate. So it goes in the MC namespace
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.DETECTOR_RAIL, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .input('i', Items.IRON_INGOT)
                .input('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(Blocks.RAIL), conditionsFromItem(Blocks.RAIL))
                .offerTo(exporter, Registries.ITEM.getId(Items.DETECTOR_RAIL));

        // Bread is a separate recipe that doesn't overwrite anything, so it goes in BWT namespace
        // There's a DisabledRecipe for the vanilla bread recipe that can be toggled independently of this
        CookingRecipeJsonBuilder.createSmelting(
                Ingredient.ofItems(BwtItems.flourItem),
                RecipeCategory.FOOD,
                Items.BREAD,
                0.35f,
                200
        )
                .criterion(RecipeProvider.hasItem(BwtItems.flourItem), RecipeProvider.conditionsFromItem(BwtItems.flourItem))
                .offerTo(exporter, Id.of(Registries.ITEM.getId(Items.BREAD).getPath()));
    }

    // Don't enforce the ID into any specific namespace
    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }
}

package com.bwt.generation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class VanillaRecipeGenerator extends RecipeProvider {
    public VanillaRecipeGenerator(DataOutput output) {
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
    }
}

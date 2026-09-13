package com.bwt.generation;

import com.bwt.items.BwtItems;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class VanillaRecipeGenerator extends FabricRecipeProvider {
    public VanillaRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        // The detector rail recipe needs to be entirely replaced with no option of re-insertion,
        // since we need it to use the wooden plate. So it goes in the MC namespace
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.DETECTOR_RAIL, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(Blocks.RAIL), has(Blocks.RAIL))
                .save(exporter, BuiltInRegistries.ITEM.getKey(Items.DETECTOR_RAIL));

        // Bread is a separate recipe that doesn't overwrite anything, so it goes in BWT namespace
        // There's a DisabledRecipe for the vanilla bread recipe that can be toggled independently of this
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(BwtItems.flourItem),
                RecipeCategory.FOOD,
                Items.BREAD,
                0.35f,
                200
        )
                .unlockedBy(RecipeProvider.getHasName(BwtItems.flourItem), RecipeProvider.has(BwtItems.flourItem))
                .save(exporter, Id.of(BuiltInRegistries.ITEM.getKey(Items.BREAD).getPath()));

        // Hopper recipe is getting overridden here via a disabled recipe + BWT recipe
        // so a modpack maker could re-enable the vanilla one and still keep the BWT one
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.HOPPER, 2)
                .define('C', Blocks.CHEST)
                .define('N', Items.NETHERITE_INGOT)
                .pattern("N N")
                .pattern("NCN")
                .pattern(" N ")
                .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), RecipeProvider.has(Items.NETHERITE_INGOT))
                .save(exporter, Id.of(BuiltInRegistries.ITEM.getKey(Items.HOPPER).getPath()));
    }

    // Don't enforce the ID into any specific namespace
    @Override
    protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
        return identifier;
    }
}

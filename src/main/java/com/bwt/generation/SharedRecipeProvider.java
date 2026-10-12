package com.bwt.generation;

import com.bwt.recipes.BlockIngredient;
import com.bwt.recipes.IngredientWithCount;
import com.bwt.utils.Id;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

public abstract class SharedRecipeProvider extends RecipeProvider {
    protected final HolderGetter<Item> items;
    protected final HolderGetter<Block> blocks;

    protected SharedRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
        this.items = provider.lookupOrThrow(Registries.ITEM);
        this.blocks = provider.lookupOrThrow(Registries.BLOCK);
    }

    public HolderGetter<Item> items() {
        return this.items;
    }

    public HolderGetter<Block> blocks() {
        return this.blocks;
    }

    public BlockIngredient blockIngredientOf(TagKey<Block> blockTagKey) {
        return BlockIngredient.of(blocks.getOrThrow(blockTagKey));
    }

    public Ingredient ingredientOf(TagKey<Item> itemTagKey) {
        return Ingredient.of(items.getOrThrow(itemTagKey));
    }

    public IngredientWithCount ingredientWithCountOf(TagKey<Item> itemTagKey, int count) {
        return IngredientWithCount.of(items.getOrThrow(itemTagKey), count);
    }

    public IngredientWithCount ingredientWithCountOf(TagKey<Item> itemTagKey) {
        return ingredientWithCountOf(itemTagKey, 1);
    }
}

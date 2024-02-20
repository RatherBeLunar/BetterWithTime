package com.bwt.recipes;

import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtRecipes implements ModInitializer {
    public static final RecipeType<BlockDispenserClumpRecipe> BLOCK_DISPENSER_CLUMP_RECIPE_TYPE = new RecipeType<>() { public String toString() { return "block_dispenser_clump"; } };
    public static final RecipeSerializer<BlockDispenserClumpRecipe> BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER = new BlockDispenserClumpRecipe.Serializer();
    public static final CauldronRecipeType CAULDRON_RECIPE_TYPE = new CauldronRecipeType();
    public static final CauldronRecipe.Serializer CAULDRON_RECIPE_SERIALIZER = new CauldronRecipe.Serializer(CauldronRecipe::new);
    public static final MillStoneRecipeType MILL_STONE_RECIPE_TYPE = new MillStoneRecipeType();
    public static final MillStoneRecipe.Serializer MILL_STONE_RECIPE_SERIALIZER = new MillStoneRecipe.Serializer(MillStoneRecipe::new);
    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "cauldron"), CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "cauldron"), CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "mill_stone"), MILL_STONE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "mill_stone"), MILL_STONE_RECIPE_SERIALIZER);
    }
}

package com.bwt.recipes;

import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BwtRecipes implements ModInitializer {
    public static final RecipeType<BlockDispenserClumpRecipe> BLOCK_DISPENSER_CLUMP_RECIPE_TYPE = new RecipeType<>() {};
    public static final RecipeSerializer<BlockDispenserClumpRecipe> BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER = new BlockDispenserClumpRecipe.Serializer();
    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER);
    }
}

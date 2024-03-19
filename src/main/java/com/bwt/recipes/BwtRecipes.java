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
    public static final StokedCauldronRecipeType STOKED_CAULDRON_RECIPE_TYPE = new StokedCauldronRecipeType();
    public static final StokedCauldronRecipe.Serializer STOKED_CAULDRON_RECIPE_SERIALIZER = new AbstractCookingPotRecipe.Serializer(StokedCauldronRecipe::new);
    public static final MillStoneRecipeType MILL_STONE_RECIPE_TYPE = new MillStoneRecipeType();
    public static final MillStoneRecipe.Serializer MILL_STONE_RECIPE_SERIALIZER = new MillStoneRecipe.Serializer(MillStoneRecipe::new);
    public static final SawRecipeType SAW_RECIPE_TYPE = new SawRecipeType();
    public static final SawRecipe.Serializer SAW_RECIPE_SERIALIZER = new SawRecipe.Serializer(SawRecipe::new);
    public static final TurntableRecipeType TURNTABLE_RECIPE_TYPE = new TurntableRecipeType();
    public static final TurntableRecipe.Serializer TURNTABLE_RECIPE_SERIALIZER = new TurntableRecipe.Serializer(TurntableRecipe::new);
    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "cauldron"), CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "cauldron"), CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "stoked_cauldron"), STOKED_CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "stoked_cauldron"), STOKED_CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "mill_stone"), MILL_STONE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "mill_stone"), MILL_STONE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "saw"), SAW_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "saw"), SAW_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "turntable"), TURNTABLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "turntable"), TURNTABLE_RECIPE_SERIALIZER);
    }
}

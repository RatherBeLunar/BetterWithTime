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
    public static final CauldronRecipeType CAULDRON_RECIPE_TYPE = new CauldronRecipeType();
    public static final CauldronRecipe.Serializer CAULDRON_RECIPE_SERIALIZER = new CauldronRecipe.Serializer(CauldronRecipe::new);
    public static final CrucibleRecipeType CRUCIBLE_RECIPE_TYPE = new CrucibleRecipeType();
    public static final CrucibleRecipe.Serializer CRUCIBLE_RECIPE_SERIALIZER = new CrucibleRecipe.Serializer(CrucibleRecipe::new);
    public static final StokedCauldronRecipeType STOKED_CAULDRON_RECIPE_TYPE = new StokedCauldronRecipeType();
    public static final StokedCauldronRecipe.Serializer STOKED_CAULDRON_RECIPE_SERIALIZER = new StokedCauldronRecipe.Serializer(StokedCauldronRecipe::new);
    public static final StokedCrucibleRecipeType STOKED_CRUCIBLE_RECIPE_TYPE = new StokedCrucibleRecipeType();
    public static final StokedCrucibleRecipe.Serializer STOKED_CRUCIBLE_RECIPE_SERIALIZER = new StokedCrucibleRecipe.Serializer(StokedCrucibleRecipe::new);
    public static final RecipeType<MillStoneRecipe> MILL_STONE_RECIPE_TYPE = new RecipeType<>() {};
    public static final MillStoneRecipe.Serializer MILL_STONE_RECIPE_SERIALIZER = new MillStoneRecipe.Serializer(MillStoneRecipe::new);
    public static final RecipeType<SawRecipe> SAW_RECIPE_TYPE = new RecipeType<>() {};
    public static final SawRecipe.Serializer SAW_RECIPE_SERIALIZER = new SawRecipe.Serializer(SawRecipe::new);
    public static final RecipeType<TurntableRecipe> TURNTABLE_RECIPE_TYPE = new RecipeType<>() {};
    public static final TurntableRecipe.Serializer TURNTABLE_RECIPE_SERIALIZER = new TurntableRecipe.Serializer(TurntableRecipe::new);
    public static final RecipeType<KilnRecipe> KILN_RECIPE_TYPE = new RecipeType<>() {};
    public static final KilnRecipe.Serializer KILN_RECIPE_SERIALIZER = new KilnRecipe.Serializer(KilnRecipe::new);
    public static final RecipeType<SoulForgeShapedRecipe> SOUL_FORGE_SHAPED_RECIPE_TYPE = new RecipeType<>() {};
    public static final SoulForgeShapedRecipe.Serializer SOUL_FORGE_SHAPED_RECIPE_SERIALIZER = new SoulForgeShapedRecipe.Serializer();
    public static final RecipeType<DisabledRecipe> DISABLED_RECIPE_TYPE = new RecipeType<>() {};
    public static final DisabledRecipe.Serializer DISABLED_RECIPE_SERIALIZER = new DisabledRecipe.Serializer(DisabledRecipe::new);
    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "cauldron"), CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "cauldron"), CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "stoked_cauldron"), STOKED_CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "stoked_cauldron"), STOKED_CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "crucible"), CRUCIBLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "crucible"), CRUCIBLE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "stoked_crucible"), STOKED_CRUCIBLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "stoked_crucible"), STOKED_CRUCIBLE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "mill_stone"), MILL_STONE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "mill_stone"), MILL_STONE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "saw"), SAW_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "saw"), SAW_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "turntable"), TURNTABLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "turntable"), TURNTABLE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "kiln"), KILN_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "kiln"), KILN_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "soul_forge_shaped"), SOUL_FORGE_SHAPED_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "soul_forge_shaped"), SOUL_FORGE_SHAPED_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, new Identifier("bwt", "disabled"), DISABLED_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("bwt", "disabled"), DISABLED_RECIPE_SERIALIZER);
    }
}

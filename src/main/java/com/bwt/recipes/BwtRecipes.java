package com.bwt.recipes;

import com.bwt.recipes.block_dispenser_clump.BlockDispenserClumpRecipe;
import com.bwt.recipes.cooking_pots.*;
import com.bwt.recipes.hopper_filter.HopperFilterRecipe;
import com.bwt.recipes.kiln.KilnRecipe;
import com.bwt.recipes.mill_stone.MillStoneRecipe;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.recipes.soul_bottling.SoulBottlingRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapelessRecipe;
import com.bwt.recipes.turntable.TurntableRecipe;
import com.bwt.utils.Id;
import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

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
    public static final MillStoneRecipe.Serializer MILL_STONE_RECIPE_SERIALIZER = new MillStoneRecipe.Serializer();
    public static final RecipeType<HopperFilterRecipe> HOPPER_FILTER_RECIPE_TYPE = new RecipeType<>() {};
    public static final HopperFilterRecipe.Serializer HOPPER_FILTER_RECIPE_SERIALIZER = new HopperFilterRecipe.Serializer();
    public static final RecipeType<SoulBottlingRecipe> SOUL_BOTTLING_RECIPE_TYPE = new RecipeType<>() {};
    public static final SoulBottlingRecipe.Serializer SOUL_BOTTLING_RECIPE_SERIALIZER = new SoulBottlingRecipe.Serializer();
    public static final RecipeType<SawRecipe> SAW_RECIPE_TYPE = new RecipeType<>() {};
    public static final SawRecipe.Serializer SAW_RECIPE_SERIALIZER = new SawRecipe.Serializer();
    public static final RecipeType<TurntableRecipe> TURNTABLE_RECIPE_TYPE = new RecipeType<>() {};
    public static final TurntableRecipe.Serializer TURNTABLE_RECIPE_SERIALIZER = new TurntableRecipe.Serializer();
    public static final RecipeType<KilnRecipe> KILN_RECIPE_TYPE = new RecipeType<>() {};
    public static final KilnRecipe.Serializer KILN_RECIPE_SERIALIZER = new KilnRecipe.Serializer();
    public static final RecipeType<CraftingRecipe> SOUL_FORGE_RECIPE_TYPE = new RecipeType<>() {};
    public static final SoulForgeShapedRecipe.Serializer SOUL_FORGE_SHAPED_RECIPE_SERIALIZER = new SoulForgeShapedRecipe.Serializer();
    public static final SoulForgeShapelessRecipe.Serializer SOUL_FORGE_SHAPELESS_RECIPE_SERIALIZER = new SoulForgeShapelessRecipe.Serializer();
    public static final RecipeType<DisabledRecipe> DISABLED_RECIPE_TYPE = new RecipeType<>() {};
    public static final DisabledRecipe.Serializer DISABLED_RECIPE_SERIALIZER = new DisabledRecipe.Serializer();
    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_TYPE, Id.of("block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("block_dispenser_clump"), BLOCK_DISPENSER_CLUMP_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("cauldron"), CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("cauldron"), CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("stoked_cauldron"), STOKED_CAULDRON_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("stoked_cauldron"), STOKED_CAULDRON_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("crucible"), CRUCIBLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("crucible"), CRUCIBLE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("stoked_crucible"), STOKED_CRUCIBLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("stoked_crucible"), STOKED_CRUCIBLE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("mill_stone"), MILL_STONE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("mill_stone"), MILL_STONE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("hopper_filter"), HOPPER_FILTER_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("hopper_filter"), HOPPER_FILTER_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("soul_bottling"), SOUL_BOTTLING_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("soul_bottling"), SOUL_BOTTLING_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("saw"), SAW_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("saw"), SAW_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("turntable"), TURNTABLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("turntable"), TURNTABLE_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("kiln"), KILN_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("kiln"), KILN_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("soul_forge"), SOUL_FORGE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("soul_forge_shaped"), SOUL_FORGE_SHAPED_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("soul_forge_shapeless"), SOUL_FORGE_SHAPELESS_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_TYPE, Id.of("disabled"), DISABLED_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, Id.of("disabled"), DISABLED_RECIPE_SERIALIZER);
    }
}

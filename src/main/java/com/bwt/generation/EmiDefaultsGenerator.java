package com.bwt.generation;

import com.bwt.utils.Id;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


//Generate the EMI Recipe Defaults file as specified by
//https://github.com/emilyploszaj/emi/wiki/Recipe-Defaults
//This is necessary for building Recipe Trees.
public class EmiDefaultsGenerator implements DataProvider {
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
    private final FabricDataOutput output;

    private static final ArrayList<Identifier> defaultRecipeIdentifiers = new ArrayList<>();
    public static void addDefaultRecipe(Identifier identifier) {
        if (defaultRecipeIdentifiers.contains(identifier)) {
            throw new KeyAlreadyExistsException("duplicate defaulted recipe " + identifier.toString());
        }
        defaultRecipeIdentifiers.add(identifier);
    }

    public static void addBwtRecipe(Identifier identifier) {
        addDefaultRecipe(Id.of(identifier.getPath()));
    }

    public static void addDefaultRecipe(String... recipeIds) {
        for (String recipeId : recipeIds) {
            addDefaultRecipe(Id.of(recipeId));
        }
    }

    public static void addDefaultRecipe(ItemConvertible... itemConvertibles) {
        for (ItemConvertible itemConvertible : itemConvertibles) {
            addBwtRecipe(Registries.ITEM.getId(itemConvertible.asItem()));
        }
    }


    public EmiDefaultsGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    public void addDefaults() {
        addDefaultRecipe(Id.of("emi", "/world/stripping/bwt/blood_wood"));
        addDefaultRecipe(Id.of("emi", "/world/flattening/bwt/dirt_slab"));
        addDefaultRecipe(Id.of("emi", "/world/stripping/bwt/blood_wood_log"));

        addDefaultRecipe(
                "fabric",
                "wood_blade",
                "rope",
                "blood_wood_stairs",
                "grass_planter",
                "belt",
                "soulforge-bwt-detector_block",
                "pulley",
                "hibachi",
                "soulforge-bwt-buddy_block",
                "composite_bow",
                "blood_wood_fence_gate",
                "wicker",
                "blood_wood_slab",
                "platform",
                "blood_wood_trapdoor",
                "dye_vase_gray",
                "water_wheel",
                "tallow_from_cauldron_rendering_porkchop",
                "poached_egg_from_cauldron",
                "black_wool_slab",
                "axle",
                "blood_wood_fence",
                "gear",
                "oak_planks_table",
                "haft",
                "light_blue_wool_slab",
                "white_wool_slab",
                "redstone_clutch",
                "blood_wood_pressure_plate",
                "hand_crank",
                "rope_coil_block",
                "soulforge-bwt-netherite_mattock",
                "padding",
                "dye_vase_cyan",
                "soulforge-bwt-armor_plate",
                "red_wool_slab",
                "dirt_slab_from_block",
                "oak_planks_pedestal",
                "blood_wood_button",
                "dynamite",
                "saw_companion_cube",
                "mill_stone",
                "soulforge-bwt-broadhead",
                "green_wool_slab",
                "strap",
                "pink_wool_slab",
                "soul_sand_planter",
                "soulforge-bwt-lens",
                "potash_from_cauldron_rendering_saw_dust",
                "dye_vase_green",
                "podzol_slab_from_block",
                "soulforge-bwt-obsidian_pressure_plate",
                "broadhead_arrow",
                "grass_slab_from_block",
                "soulforge-bwt-netherite_battle_axe",
                "recombine_oak_planks_corner",
                "dye_vase_magenta",
                "purple_wool_slab",
                "dye_vase_light_gray",
                "dye_vase_brown",
                "dye_vase_light_blue",
                "light_block",
                "dye_vase_blue",
                "blood_wood",
                "cyan_wool_slab",
                "kibble_from_stoked_cauldron",
                "mining_charge_with_glue",
                "yellow_wool_slab",
                "gray_wool_slab",
                "mycelium_slab_from_block",
                "stone_detector_rail",
                "cauldron",
                "dye_vase_orange",
                "vine_trap",
                "dye_vase_purple",
                "sail",
                "turntable",
                "soap_block",
                "bellows",
                "windmill",
                "magenta_wool_slab",
                "wicker_block",
                "dye_vase_pink",
                "brown_wool_slab",
                "padding_block",
                "grate",
                "blood_wood_sapling_from_cauldron",
                "saw",
                "soil_planter",
                "blue_wool_slab",
                "wicker_slab_from_block",
                "oak_planks_column",
                "obsidian_detector_rail",
                "anchor",
                "soulforge-bwt-block_dispenser",
                "dye_vase_yellow",
                "slats",
                "recombine_oak_planks_moulding",
                "light_gray_wool_slab",
                "soulforge-bwt-redstone_eye",
                "dye_vase_lime",
                "soul_forge",
                "glue_from_cauldron_rendering_leather",
                "orange_wool_slab",
                "lime_wool_slab",
                "soap_from_stoked_cauldron",
                "blood_wood_door",
                "mech_hopper",
                "cooked_wolf_chop",
                "donut_from_cauldron",
                "soul_urn_from_soul_bottling",
                "dye_vase_black",
                "dye_vase_red",
                "gear_box",
                "saw_oak_planks_moulding",
                "dung_block",
                "concentrated_hellfire_block"
        );
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        addDefaults();

        DataOutput.PathResolver recipeDefaults = this.output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "recipe/defaults");
        Path bwtRecipeDefaultsFile = recipeDefaults.resolveJson(Id.of("emi", Id.MOD_ID));

        JsonObject object = new JsonObject();
        JsonArray added = new JsonArray();
        defaultRecipeIdentifiers.forEach(id -> added.add(id.toString()));
        object.add("added", added);
        return DataProvider.writeToPath(writer, object, bwtRecipeDefaultsFile);
    }

    @Override
    public String getName() {
        return "EmiDefaults";
    }


}

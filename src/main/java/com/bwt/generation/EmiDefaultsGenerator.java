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
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


//Generate the EMI Recipe Defaults file as specified by
//https://github.com/emilyploszaj/emi/wiki/Recipe-Defaults
//This is necessary for building Recipe Trees.
public class EmiDefaultsGenerator implements DataProvider {
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;
    private final FabricDataOutput output;

    private static final Set<Identifier> defaultRecipeIdentifiers = Sets.newHashSet();
    public static void addDefaultRecipe(Identifier identifier) {
        defaultRecipeIdentifiers.add(identifier);
    }
    public static void addBwtRecipe(Identifier identifier) {
        addDefaultRecipe(Id.of(identifier.getPath()));
    }


    public EmiDefaultsGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    public void addDefaults() {
        addDefaultRecipe(Id.of("hopper_soul_urn"));
        addDefaultRecipe(Id.of("fabric"));
        addDefaultRecipe(Id.of("wood_blade"));
        addDefaultRecipe(Id.of("rope"));
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        addDefaults();

        var recipeDefaults = this.output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "recipe/defaults");
        var bwtRecipeDefaultsFile = recipeDefaults.resolveJson(Id.of("emi", Id.MOD_ID));

        JsonObject object = new JsonObject();
        var added = new JsonArray();
        defaultRecipeIdentifiers.forEach(id -> added.add(id.toString()));
        object.add("added", added);
        return DataProvider.writeToPath(writer, object, bwtRecipeDefaultsFile);
    }

    @Override
    public String getName() {
        return "EmiDefaults";
    }


}

package com.bwt.generation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    protected BlockDispenserClumpRecipeGenerator blockDispenserClumpRecipeGenerator;
    protected CauldronRecipeGenerator cauldronRecipeGenerator;
    protected CrucibleRecipeGenerator crucibleRecipeGenerator;
    protected CraftingRecipeGenerator craftingRecipeGenerator;
    protected VanillaRecipeGenerator vanillaRecipeGenerator;
    protected DisabledVanilaRecipeGenerator disabledVanilaRecipeGenerator;
    protected HopperRecipeGenerator hopperRecipeGenerator;
    protected MillStoneRecipeGenerator millStoneRecipeGenerator;
    protected MobSpawnerConversionRecipeGenerator mobSpawnerConversionRecipeGenerator;
    protected SawRecipeGenerator sawRecipeGenerator;
    protected TurntableRecipeGenerator turntableRecipeGenerator;
    protected KilnRecipeGenerator kilnRecipeGenerator;
    protected SoulForgeRecipeGenerator soulForgeRecipeGenerator;

    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
        this.blockDispenserClumpRecipeGenerator = new BlockDispenserClumpRecipeGenerator(output, registriesFuture);
        this.cauldronRecipeGenerator = new CauldronRecipeGenerator(output, registriesFuture);
        this.crucibleRecipeGenerator = new CrucibleRecipeGenerator(output, registriesFuture);
        this.craftingRecipeGenerator = new CraftingRecipeGenerator(output, registriesFuture);
        this.vanillaRecipeGenerator = new VanillaRecipeGenerator(output, registriesFuture);
        this.disabledVanilaRecipeGenerator = new DisabledVanilaRecipeGenerator(output, registriesFuture);
        this.hopperRecipeGenerator = new HopperRecipeGenerator(output, registriesFuture);
        this.millStoneRecipeGenerator = new MillStoneRecipeGenerator(output, registriesFuture);
        this.mobSpawnerConversionRecipeGenerator = new MobSpawnerConversionRecipeGenerator(output, registriesFuture);
        this.sawRecipeGenerator = new SawRecipeGenerator(output, registriesFuture);
        this.turntableRecipeGenerator = new TurntableRecipeGenerator(output, registriesFuture);
        this.kilnRecipeGenerator = new KilnRecipeGenerator(output, registriesFuture);
        this.soulForgeRecipeGenerator = new SoulForgeRecipeGenerator(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        blockDispenserClumpRecipeGenerator.generate(exporter);
        cauldronRecipeGenerator.generate(exporter);
        crucibleRecipeGenerator.generate(exporter);
        craftingRecipeGenerator.generate(exporter);
        vanillaRecipeGenerator.generate(exporter);
        hopperRecipeGenerator.generate(exporter);
        millStoneRecipeGenerator.generate(exporter);
        mobSpawnerConversionRecipeGenerator.generate(exporter);
        sawRecipeGenerator.generate(exporter);
        turntableRecipeGenerator.generate(exporter);
        kilnRecipeGenerator.generate(exporter);
        soulForgeRecipeGenerator.generate(exporter);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup wrapperLookup) {
        return CompletableFuture.allOf(super.run(writer, wrapperLookup), disabledVanilaRecipeGenerator.run(writer, wrapperLookup));
    }
}

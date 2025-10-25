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
    protected EmiDefaultsGenerator emiDefaultsGenerator;

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
        this.emiDefaultsGenerator = new EmiDefaultsGenerator(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {}

    @Override
    public CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup wrapperLookup) {
        return CompletableFuture.allOf(
                disabledVanilaRecipeGenerator.run(writer, wrapperLookup),
                blockDispenserClumpRecipeGenerator.run(writer, wrapperLookup),
                cauldronRecipeGenerator.run(writer, wrapperLookup),
                crucibleRecipeGenerator.run(writer, wrapperLookup),
                craftingRecipeGenerator.run(writer, wrapperLookup),
                vanillaRecipeGenerator.run(writer, wrapperLookup),
                hopperRecipeGenerator.run(writer, wrapperLookup),
                millStoneRecipeGenerator.run(writer, wrapperLookup),
                mobSpawnerConversionRecipeGenerator.run(writer, wrapperLookup),
                sawRecipeGenerator.run(writer, wrapperLookup),
                turntableRecipeGenerator.run(writer, wrapperLookup),
                kilnRecipeGenerator.run(writer, wrapperLookup),
                soulForgeRecipeGenerator.run(writer, wrapperLookup),
                emiDefaultsGenerator.run(writer)
        );
    }
}

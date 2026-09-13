package com.bwt.generation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.RecipeOutput;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    protected final BlockDispenserClumpRecipeGenerator blockDispenserClumpRecipeGenerator;
    protected final CauldronRecipeGenerator cauldronRecipeGenerator;
    protected final CrucibleRecipeGenerator crucibleRecipeGenerator;
    protected final CraftingRecipeGenerator craftingRecipeGenerator;
    protected final VanillaRecipeGenerator vanillaRecipeGenerator;
    protected final DisabledVanilaRecipeGenerator disabledVanilaRecipeGenerator;
    protected final HopperRecipeGenerator hopperRecipeGenerator;
    protected final MillStoneRecipeGenerator millStoneRecipeGenerator;
    protected final MobSpawnerConversionRecipeGenerator mobSpawnerConversionRecipeGenerator;
    protected final SawRecipeGenerator sawRecipeGenerator;
    protected final TurntableRecipeGenerator turntableRecipeGenerator;
    protected final KilnRecipeGenerator kilnRecipeGenerator;
    protected final SoulForgeRecipeGenerator soulForgeRecipeGenerator;
    protected final EmiDefaultsGenerator emiDefaultsGenerator;

    public RecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
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
        this.emiDefaultsGenerator = new EmiDefaultsGenerator(output);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {}

    @Override
    public CompletableFuture<?> run(CachedOutput writer, HolderLookup.Provider wrapperLookup) {
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

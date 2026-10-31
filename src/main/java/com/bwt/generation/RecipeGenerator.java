package com.bwt.generation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

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
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new RecipeProvider(registryLookup, output) {
            @Override
            public void buildRecipes() {

            }
        };
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        return CompletableFuture.allOf(
                disabledVanilaRecipeGenerator.run(writer),
                blockDispenserClumpRecipeGenerator.run(writer),
                cauldronRecipeGenerator.run(writer),
                crucibleRecipeGenerator.run(writer),
                craftingRecipeGenerator.run(writer),
                vanillaRecipeGenerator.run(writer),
                hopperRecipeGenerator.run(writer),
                millStoneRecipeGenerator.run(writer),
                mobSpawnerConversionRecipeGenerator.run(writer),
                sawRecipeGenerator.run(writer),
                turntableRecipeGenerator.run(writer),
                kilnRecipeGenerator.run(writer),
                soulForgeRecipeGenerator.run(writer)
        );
    }

    @Override
    public String getName() {
        return "RecipeGenerator";
    }
}

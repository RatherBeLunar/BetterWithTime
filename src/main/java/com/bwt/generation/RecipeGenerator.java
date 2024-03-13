package com.bwt.generation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;

public class RecipeGenerator extends FabricRecipeProvider {
    protected BlockDispenserClumpRecipeGenerator blockDispenserClumpRecipeGenerator;
    protected CauldronRecipeGenerator cauldronRecipeGenerator;
    protected CraftingRecipeGenerator craftingRecipeGenerator;
    protected VanillaRecipeGenerator vanillaRecipeGenerator;
    protected MillStoneRecipeGenerator millStoneRecipeGenerator;
    protected SawRecipeGenerator sawRecipeGenerator;

    public RecipeGenerator(FabricDataOutput output) {
        super(output);
        this.blockDispenserClumpRecipeGenerator = new BlockDispenserClumpRecipeGenerator(output);
        this.cauldronRecipeGenerator = new CauldronRecipeGenerator(output);
        this.craftingRecipeGenerator = new CraftingRecipeGenerator(output);
        this.vanillaRecipeGenerator = new VanillaRecipeGenerator(output);
        this.millStoneRecipeGenerator = new MillStoneRecipeGenerator(output);
        this.sawRecipeGenerator = new SawRecipeGenerator(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        blockDispenserClumpRecipeGenerator.generate(exporter);
        cauldronRecipeGenerator.generate(exporter);
        craftingRecipeGenerator.generate(exporter);
        vanillaRecipeGenerator.generate(exporter);
        millStoneRecipeGenerator.generate(exporter);
        sawRecipeGenerator.generate(exporter);
    }
}

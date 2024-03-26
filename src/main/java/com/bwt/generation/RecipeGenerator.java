package com.bwt.generation;

import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {
    protected BlockDispenserClumpRecipeGenerator blockDispenserClumpRecipeGenerator;
    protected CauldronRecipeGenerator cauldronRecipeGenerator;
    protected CrucibleRecipeGenerator crucibleRecipeGenerator;
    protected CraftingRecipeGenerator craftingRecipeGenerator;
    protected VanillaRecipeGenerator vanillaRecipeGenerator;
    protected DisabledVanilaRecipeGenerator disabledVanilaRecipeGenerator;
    protected MillStoneRecipeGenerator millStoneRecipeGenerator;
    protected SawRecipeGenerator sawRecipeGenerator;
    protected TurntableRecipeGenerator turntableRecipeGenerator;
    protected KilnRecipeGenerator kilnRecipeGenerator;

    public RecipeGenerator(FabricDataOutput output) {
        super(output);
        this.blockDispenserClumpRecipeGenerator = new BlockDispenserClumpRecipeGenerator(output);
        this.cauldronRecipeGenerator = new CauldronRecipeGenerator(output);
        this.crucibleRecipeGenerator = new CrucibleRecipeGenerator(output);
        this.craftingRecipeGenerator = new CraftingRecipeGenerator(output);
        this.vanillaRecipeGenerator = new VanillaRecipeGenerator(output);
        this.disabledVanilaRecipeGenerator = new DisabledVanilaRecipeGenerator(output);
        this.millStoneRecipeGenerator = new MillStoneRecipeGenerator(output);
        this.sawRecipeGenerator = new SawRecipeGenerator(output);
        this.turntableRecipeGenerator = new TurntableRecipeGenerator(output);
        this.kilnRecipeGenerator = new KilnRecipeGenerator(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        blockDispenserClumpRecipeGenerator.generate(exporter);
        cauldronRecipeGenerator.generate(exporter);
        crucibleRecipeGenerator.generate(exporter);
        craftingRecipeGenerator.generate(exporter);
        vanillaRecipeGenerator.generate(exporter);
        millStoneRecipeGenerator.generate(exporter);
        sawRecipeGenerator.generate(exporter);
        turntableRecipeGenerator.generate(exporter);
        kilnRecipeGenerator.generate(exporter);

        // Can't justify its own category yet
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(BwtItems.flourItem), RecipeCategory.FOOD, Items.BREAD, 0.35f, 200).criterion(RecipeProvider.hasItem(BwtItems.flourItem), RecipeProvider.conditionsFromItem(BwtItems.flourItem)).offerTo(exporter);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return CompletableFuture.allOf(super.run(writer), disabledVanilaRecipeGenerator.run(writer));
    }
}

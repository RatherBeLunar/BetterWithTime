package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.CornerBlock;
import com.bwt.blocks.MouldingBlock;
import com.bwt.blocks.SidingBlock;
import com.bwt.items.BwtItems;
import com.bwt.recipes.SawRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SawRecipeGenerator extends FabricRecipeProvider {
    public SawRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateSawRecipes(exporter);
    }

    protected void generateSawRecipes(RecipeExporter exporter) {
        generateWoodFamilyRecipes(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.companionCubeBlock).result(BwtBlocks.companionSlabBlock.asItem(), 2).offerTo(exporter);
        SawRecipe.JsonBuilder.dropsSelf(Blocks.VINE, exporter);
        SawRecipe.JsonBuilder.dropsSelf(Blocks.CAVE_VINES, exporter);
        SawRecipe.JsonBuilder.create(Blocks.CAVE_VINES_PLANT).result(Blocks.CAVE_VINES.asItem()).offerTo(exporter);
    }

    private void generateWoodFamilyRecipes(RecipeExporter exporter) {
        for (int i = 0; i < BwtBlocks.sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = BwtBlocks.sidingBlocks.get(i);
            MouldingBlock mouldingBlock = BwtBlocks.mouldingBlocks.get(i);
            CornerBlock cornerBlock = BwtBlocks.cornerBlocks.get(i);
            if (!sidingBlock.isWood()) {
                continue;
            }
            Block planksBlock = sidingBlock.fullBlock;
            Identifier planksId = Registries.BLOCK.getId(planksBlock);
            Identifier logId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_log"));
            Identifier woodId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_wood"));
            Identifier hyphaeId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_hyphae"));
            Identifier stemId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_stem"));
            Identifier fenceId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_fence"));
            Identifier stairsId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_stairs"));
            Identifier slabId = new Identifier(planksId.getNamespace(), planksId.getPath().replace("_planks", "_slab"));

            // Logs/Stems/Hyphae -> planks
            for (Identifier logIshId : new Identifier[]{logId, woodId, hyphaeId, stemId}) {
                Block logBlock = Registries.BLOCK.get(logIshId);
                if (!logBlock.equals(Blocks.AIR)) {
                    SawRecipe.JsonBuilder.create(logBlock).result(planksBlock.asItem(), 4).result(BwtItems.sawDustItem, 2).offerTo(exporter);
                }
                Block strippedBlock = Registries.BLOCK.get(new Identifier(logIshId.getNamespace(), "stripped_" + logIshId.getPath()));
                if (!strippedBlock.equals(Blocks.AIR)) {
                    SawRecipe.JsonBuilder.create(strippedBlock).result(planksBlock.asItem(), 4).result(BwtItems.sawDustItem, 2).offerTo(exporter);
                }
            }
            // Planks -> siding -> moulding -> corner -> gear
            SawRecipe.JsonBuilder.create(planksBlock).result(sidingBlock.asItem(), 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(sidingBlock).result(mouldingBlock.asItem(), 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(mouldingBlock).result(cornerBlock.asItem(), 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(cornerBlock).result(BwtItems.gearItem, 2).offerTo(exporter);
            // Recycling recipes
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(fenceId)).result(cornerBlock.asItem(), 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(stairsId)).result(sidingBlock.asItem()).result(mouldingBlock.asItem()).offerTo(exporter);
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(slabId)).result(mouldingBlock.asItem(), 2).offerTo(exporter);
        }
    }
}

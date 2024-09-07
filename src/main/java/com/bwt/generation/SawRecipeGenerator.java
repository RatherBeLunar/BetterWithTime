package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class SawRecipeGenerator extends FabricRecipeProvider {
    public SawRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateSawRecipes(exporter);
    }

    protected void generateSawRecipes(RecipeExporter exporter) {
        generateWoodFamilyRecipes(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.companionCubeBlock).result(BwtBlocks.companionSlabBlock, 2).offerTo(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.wickerBlock).result(BwtBlocks.wickerSlabBlock, 2).offerTo(exporter);
        SawRecipe.JsonBuilder.dropsSelf(Blocks.VINE, exporter);
        SawRecipe.JsonBuilder.dropsSelf(Blocks.CHORUS_FLOWER, exporter);
        SawRecipe.JsonBuilder.create(Blocks.LADDER).result(Items.STICK, 1).offerTo(exporter);
        SawRecipe.JsonBuilder.create(Blocks.DRIED_KELP_BLOCK).result(Items.DRIED_KELP, 6).offerTo(exporter);
        SawRecipe.JsonBuilder.create(Blocks.HAY_BLOCK).result(Items.WHEAT, 6).offerTo(exporter);
    }

    private void generateWoodFamilyRecipes(RecipeExporter exporter) {
        for (int i = 0; i < BwtBlocks.sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = BwtBlocks.sidingBlocks.get(i);
            MouldingBlock mouldingBlock = BwtBlocks.mouldingBlocks.get(i);
            CornerBlock cornerBlock = BwtBlocks.cornerBlocks.get(i);
            ColumnBlock columnBlock = BwtBlocks.columnBlocks.get(i);
            PedestalBlock pedestalBlock = BwtBlocks.pedestalBlocks.get(i);
            TableBlock tableBlock = BwtBlocks.tableBlocks.get(i);
            if (!sidingBlock.isWood()) {
                continue;
            }
            Block planksBlock = sidingBlock.fullBlock;
            Identifier planksId = Registries.BLOCK.getId(planksBlock);
            Identifier logId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_log"));
            Identifier woodId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_wood"));
            Identifier hyphaeId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_hyphae"));
            Identifier stemId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_stem"));

            Item dustItem = planksBlock == BwtBlocks.bloodWoodBlocks.planksBlock ? BwtItems.soulDustItem : BwtItems.sawDustItem;
            // Logs/Stems/Hyphae -> planks
            for (Identifier logIshId : new Identifier[]{logId, woodId, hyphaeId, stemId}) {
                Block logBlock = Registries.BLOCK.get(logIshId);
                if (!logBlock.equals(Blocks.AIR)) {
                    SawRecipe.JsonBuilder.create(logBlock).result(planksBlock, 4).result(dustItem, 2).offerTo(exporter);
                }
                Block strippedBlock = Registries.BLOCK.get(Id.of(logIshId.getNamespace(), "stripped_" + logIshId.getPath()));
                if (!strippedBlock.equals(Blocks.AIR)) {
                    SawRecipe.JsonBuilder.create(strippedBlock).result(planksBlock, 4).result(dustItem, 2).offerTo(exporter);
                }
            }
            // Planks -> siding -> moulding -> corner -> gear
            SawRecipe.JsonBuilder.create(planksBlock).result(sidingBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(sidingBlock).result(mouldingBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(mouldingBlock).result(cornerBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(cornerBlock).result(BwtItems.gearItem, 2).offerTo(exporter);
            // Recycling recipes
            Identifier fenceId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_fence"));
            Identifier fenceGateId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_fence_gate"));
            Identifier stairsId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_stairs"));
            Identifier slabId = Id.of(planksId.getNamespace(), planksId.getPath().replace("_planks", "_slab"));
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(fenceId)).result(cornerBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(fenceGateId)).result(cornerBlock).result(Items.STICK).offerTo(exporter);
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(stairsId)).result(sidingBlock).result(mouldingBlock).offerTo(exporter);
            SawRecipe.JsonBuilder.create(Registries.BLOCK.get(slabId)).result(mouldingBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(columnBlock).result(sidingBlock).result(mouldingBlock).offerTo(exporter);
            SawRecipe.JsonBuilder.create(pedestalBlock).result(mouldingBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(tableBlock).result(mouldingBlock).offerTo(exporter);
        }
    }
}

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
        SawRecipe.JsonBuilder.create(BwtBlocks.companionCubeBlock).result(BwtBlocks.companionSlabBlock, 2).markDefault().offerTo(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.companionSlabBlock).result(BwtBlocks.companionSlabBlock).offerTo(exporter);
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
            Identifier baseId = planksId.withPath(planksId.getPath().replace("_planks", ""));
            Identifier logId = baseId.withSuffixedPath("_log");
            Identifier woodId = baseId.withSuffixedPath("_wood");
            Identifier hyphaeId = baseId.withSuffixedPath("_hyphae");
            Identifier stemId = baseId.withSuffixedPath("_stem");

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
            SawRecipe.JsonBuilder.create(planksBlock).result(sidingBlock, 2).markDefault().offerTo(exporter);
            SawRecipe.JsonBuilder.create(sidingBlock).result(mouldingBlock, 2).markDefault().offerTo(exporter);
            SawRecipe.JsonBuilder.create(mouldingBlock).result(cornerBlock, 2).markDefault().offerTo(exporter);
            SawRecipe.JsonBuilder.create(cornerBlock).result(BwtItems.gearItem, 2).markDefault().offerTo(exporter);
            // Recycling recipes
            Registries.BLOCK.getOrEmpty(baseId.withSuffixedPath("_fence"))
                    .ifPresent(fence -> SawRecipe.JsonBuilder.create(fence).result(cornerBlock, 2).offerTo(exporter));
            Registries.BLOCK.getOrEmpty(baseId.withSuffixedPath("_fence_gate"))
                    .ifPresent(fenceGate -> SawRecipe.JsonBuilder.create(fenceGate).result(cornerBlock).result(Items.STICK).offerTo(exporter));
            Registries.BLOCK.getOrEmpty(baseId.withSuffixedPath("_stairs"))
                    .ifPresent(stairs -> SawRecipe.JsonBuilder.create(stairs).result(sidingBlock).result(mouldingBlock).offerTo(exporter));
            Registries.BLOCK.getOrEmpty(baseId.withSuffixedPath("_slab"))
                    .ifPresent(slab -> SawRecipe.JsonBuilder.create(slab).result(mouldingBlock, 2).offerTo(exporter));
            SawRecipe.JsonBuilder.create(columnBlock).result(sidingBlock).result(mouldingBlock).offerTo(exporter);
            SawRecipe.JsonBuilder.create(pedestalBlock).result(mouldingBlock, 2).offerTo(exporter);
            SawRecipe.JsonBuilder.create(tableBlock).result(mouldingBlock).offerTo(exporter);
        }

        // special case bamboo
        // no sawdust, only 2 planks
        SawRecipe.JsonBuilder.create(Blocks.BAMBOO_BLOCK).result(Blocks.BAMBOO_PLANKS, 2).offerTo(exporter);
        SawRecipe.JsonBuilder.create(Blocks.STRIPPED_BAMBOO_BLOCK).result(Blocks.BAMBOO_PLANKS, 2).offerTo(exporter);
    }
}

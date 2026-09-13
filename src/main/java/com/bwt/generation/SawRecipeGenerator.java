package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.recipes.saw.SawRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.concurrent.CompletableFuture;

public class SawRecipeGenerator extends FabricRecipeProvider {
    public SawRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateSawRecipes(exporter);
    }

    protected void generateSawRecipes(RecipeOutput exporter) {
        generateWoodFamilyRecipes(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.companionCubeBlock).result(BwtBlocks.companionSlabBlock, 2).markDefault().save(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.companionSlabBlock).result(BwtBlocks.companionSlabBlock).save(exporter);
        SawRecipe.JsonBuilder.create(BwtBlocks.wickerBlock).result(BwtBlocks.wickerSlabBlock, 2).save(exporter);
        SawRecipe.JsonBuilder.dropsSelf(Blocks.VINE, exporter);
        SawRecipe.JsonBuilder.dropsSelf(Blocks.CHORUS_FLOWER, exporter);
        SawRecipe.JsonBuilder.create(Blocks.LADDER).result(Items.STICK, 1).save(exporter);
        SawRecipe.JsonBuilder.create(Blocks.DRIED_KELP_BLOCK).result(Items.DRIED_KELP, 6).save(exporter);
        SawRecipe.JsonBuilder.create(Blocks.HAY_BLOCK).result(Items.WHEAT, 6).save(exporter);
    }

    private void generateWoodFamilyRecipes(RecipeOutput exporter) {
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
            ResourceLocation planksId = BuiltInRegistries.BLOCK.getKey(planksBlock);
            ResourceLocation baseId = planksId.withPath(planksId.getPath().replace("_planks", ""));
            ResourceLocation logId = baseId.withSuffix("_log");
            ResourceLocation woodId = baseId.withSuffix("_wood");
            ResourceLocation hyphaeId = baseId.withSuffix("_hyphae");
            ResourceLocation stemId = baseId.withSuffix("_stem");

            Item dustItem = planksBlock == BwtBlocks.bloodWoodBlocks.planksBlock ? BwtItems.soulDustItem : BwtItems.sawDustItem;
            // Logs/Stems/Hyphae -> planks
            for (ResourceLocation logIshId : new ResourceLocation[]{logId, woodId, hyphaeId, stemId}) {
                Block logBlock = BuiltInRegistries.BLOCK.get(logIshId);
                if (!logBlock.equals(Blocks.AIR)) {
                    SawRecipe.JsonBuilder.create(logBlock).result(planksBlock, 4).result(dustItem, 2).save(exporter);
                }
                Block strippedBlock = BuiltInRegistries.BLOCK.get(Id.of(logIshId.getNamespace(), "stripped_" + logIshId.getPath()));
                if (!strippedBlock.equals(Blocks.AIR)) {
                    SawRecipe.JsonBuilder.create(strippedBlock).result(planksBlock, 4).result(dustItem, 2).save(exporter);
                }
            }
            // Planks -> siding -> moulding -> corner -> gear
            SawRecipe.JsonBuilder.create(planksBlock).result(sidingBlock, 2).markDefault().save(exporter);
            SawRecipe.JsonBuilder.create(sidingBlock).result(mouldingBlock, 2).markDefault().save(exporter);
            SawRecipe.JsonBuilder.create(mouldingBlock).result(cornerBlock, 2).markDefault().save(exporter);
            SawRecipe.JsonBuilder.create(cornerBlock).result(BwtItems.gearItem, 2).markDefault().save(exporter);
            // Recycling recipes
            BuiltInRegistries.BLOCK.getOptional(baseId.withSuffix("_fence"))
                    .ifPresent(fence -> SawRecipe.JsonBuilder.create(fence).result(cornerBlock, 2).save(exporter));
            BuiltInRegistries.BLOCK.getOptional(baseId.withSuffix("_fence_gate"))
                    .ifPresent(fenceGate -> SawRecipe.JsonBuilder.create(fenceGate).result(cornerBlock).result(Items.STICK).save(exporter));
            BuiltInRegistries.BLOCK.getOptional(baseId.withSuffix("_stairs"))
                    .ifPresent(stairs -> SawRecipe.JsonBuilder.create(stairs).result(sidingBlock).result(mouldingBlock).save(exporter));
            BuiltInRegistries.BLOCK.getOptional(baseId.withSuffix("_slab"))
                    .ifPresent(slab -> SawRecipe.JsonBuilder.create(slab).result(mouldingBlock, 2).save(exporter));
            SawRecipe.JsonBuilder.create(columnBlock).result(sidingBlock).result(mouldingBlock).save(exporter);
            SawRecipe.JsonBuilder.create(pedestalBlock).result(mouldingBlock, 2).save(exporter);
            SawRecipe.JsonBuilder.create(tableBlock).result(mouldingBlock).save(exporter);
        }

        // special case bamboo
        // no sawdust, only 2 planks
        SawRecipe.JsonBuilder.create(Blocks.BAMBOO_BLOCK).result(Blocks.BAMBOO_PLANKS, 2).save(exporter);
        SawRecipe.JsonBuilder.create(Blocks.STRIPPED_BAMBOO_BLOCK).result(Blocks.BAMBOO_PLANKS, 2).save(exporter);
    }
}

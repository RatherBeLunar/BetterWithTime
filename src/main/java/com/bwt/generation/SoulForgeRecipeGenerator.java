package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.CornerBlock;
import com.bwt.blocks.MouldingBlock;
import com.bwt.blocks.SidingBlock;
import com.bwt.items.BwtItems;
import com.bwt.recipes.SoulForgeShapedRecipe;
import com.bwt.recipes.SoulForgeShapelessRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;

public class SoulForgeRecipeGenerator extends FabricRecipeProvider {
    public SoulForgeRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        for (int i = 0; i < BwtBlocks.sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = BwtBlocks.sidingBlocks.get(i);
            MouldingBlock mouldingBlock = BwtBlocks.mouldingBlocks.get(i);
            CornerBlock cornerBlock = BwtBlocks.cornerBlocks.get(i);
            if (sidingBlock.isWood()) {
                continue;
            }
            SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock, 8)
                    .pattern("XXXX")
                    .input('X', sidingBlock.fullBlock)
                    .group("siding")
                    .criterion(hasItem(sidingBlock.fullBlock), conditionsFromItem(sidingBlock.fullBlock))
                    .offerTo(exporter);
            SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, mouldingBlock, 8)
                    .pattern("XXXX")
                    .input('X', sidingBlock)
                    .group("moulding")
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter);
            SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, cornerBlock, 8)
                    .pattern("XXXX")
                    .input('X', mouldingBlock)
                    .group("corners")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter);

            // Stone Mini block recombining recipes
            SoulForgeShapelessRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                    .input(sidingBlock, 2)
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter, "recombine_" + Registries.BLOCK.getId(sidingBlock).getPath());
            SoulForgeShapelessRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .input(mouldingBlock, 2)
                    .group("siding")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter, "recombine_" + Registries.BLOCK.getId(mouldingBlock).getPath());
            SoulForgeShapelessRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .input(cornerBlock, 2)
                    .group("moulding")
                    .criterion(hasItem(cornerBlock), conditionsFromItem(cornerBlock))
                    .offerTo(exporter, "recombine_" + Registries.BLOCK.getId(cornerBlock).getPath());
        }
        // Refined Tools
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.refinedPickaxeItem)
                .pattern("sss")
                .pattern(" h ")
                .pattern(" h ")
                .pattern(" h ")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.refinedShovelItem)
                .pattern("s")
                .pattern("h")
                .pattern("h")
                .pattern("h")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.refinedAxeItem)
                .pattern("ss")
                .pattern("sh")
                .pattern(" h")
                .pattern(" h")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.refinedHoeItem)
                .pattern("ss")
                .pattern(" h")
                .pattern(" h")
                .pattern(" h")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.refinedSwordItem)
                .pattern("s")
                .pattern("s")
                .pattern("s")
                .pattern("h")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.mattockItem)
                .pattern("sss ")
                .pattern(" h s")
                .pattern(" h  ")
                .pattern(" h  ")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.battleAxeItem)
                .pattern("sss")
                .pattern("shs")
                .pattern(" h ")
                .pattern(" h ")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);

        // Plate armor
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.armorPlateItem)
                .pattern("SspS")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('S', BwtItems.strapItem)
                .input('p', BwtItems.paddingItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.plateHelmArmorItem)
                .pattern("ssss")
                .pattern("s  s")
                .pattern("s  s")
                .pattern(" pp ")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.chestPlateArmorItem)
                .pattern("p  p")
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.plateLeggingsArmorItem)
                .pattern("ssss")
                .pattern("pssp")
                .pattern("p  p")
                .pattern("p  p")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.plateBootsArmorItem)
                .pattern(" ss ")
                .pattern(" ss ")
                .pattern("spps")
                .input('s', BwtItems.soulforgedSteelItem)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.broadheadItem, 16)
                .pattern(" s ")
                .pattern("sss")
                .pattern(" s ")
                .pattern(" s ")
                .input('s', BwtItems.soulforgedSteelItem)
                .criterion(hasItem(BwtItems.soulforgedSteelItem), conditionsFromItem(BwtItems.soulforgedSteelItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.redstoneEyeItem)
                .pattern("lll")
                .pattern("ggg")
                .pattern(" r ")
                .input('l', Items.LAPIS_LAZULI)
                .input('g', Items.GOLD_NUGGET)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(Items.LAPIS_LAZULI), conditionsFromItem(Items.LAPIS_LAZULI))
                .offerTo(exporter);

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, BwtBlocks.detectorBlock)
                .pattern("cccc")
                .pattern("ette")
                .pattern("srrs")
                .pattern("srrs")
                .input('c', Items.COBBLESTONE)
                .input('e', BwtItems.redstoneEyeItem)
                .input('s', Items.STONE)
                .input('t', Items.REDSTONE_TORCH)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtItems.redstoneEyeItem), conditionsFromItem(BwtItems.redstoneEyeItem))
                .offerTo(exporter);

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, BwtBlocks.blockDispenserBlock)
                .pattern("mmmm")
                .pattern("muum")
                .pattern("stts")
                .pattern("srrs")
                .input('m', Items.MOSSY_COBBLESTONE)
                .input('u', BwtItems.soulUrnItem)
                .input('s', Items.STONE)
                .input('t', Items.REDSTONE_TORCH)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtItems.soulUrnItem), conditionsFromItem(BwtItems.soulUrnItem))
                .offerTo(exporter);
    }
}

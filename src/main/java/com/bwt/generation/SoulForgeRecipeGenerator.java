package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapelessRecipe;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SoulForgeRecipeGenerator extends FabricRecipeProvider {
    public SoulForgeRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    private Identifier highEfficiencyId(ItemConvertible itemConvertible) {
        return Id.of(Registries.ITEM.getId(itemConvertible.asItem()).withPrefixedPath("he_").getPath());
    }

    private void createHighEfficiencyBlockFamilyRecipe(RecipeExporter exporter, BlockFamily blockFamily, BlockFamily.Variant variant, Function<Block, CraftingRecipeJsonBuilder> builder) {
        Optional.ofNullable(blockFamily.getVariant(variant))
                .ifPresent(result -> builder.apply(result)
                        .group(blockFamily.getGroup().map(group -> group + "_" + variant.getName()).orElse(null))
                        .offerTo(exporter, highEfficiencyId(result))
                );
    }

    private void createHighEfficiencyBlockFamilyRecipes(BlockFamily blockFamily, RecipeExporter exporter) {
        Block baseBlock = blockFamily.getBaseBlock();
        Optional<SidingBlock> optionalSidingBlock = BwtBlocks.sidingBlocks.stream().filter(siding -> siding.fullBlock == baseBlock).findFirst();
        Optional<MouldingBlock> optionalMouldingBlock = BwtBlocks.mouldingBlocks.stream().filter(siding -> siding.fullBlock == baseBlock).findFirst();
        Optional<CornerBlock> optionalCornerBlock = BwtBlocks.cornerBlocks.stream().filter(siding -> siding.fullBlock == baseBlock).findFirst();

        if (optionalSidingBlock.isEmpty() || optionalMouldingBlock.isEmpty() || optionalCornerBlock.isEmpty()) {
            return;
        }
        SidingBlock sidingBlock = optionalSidingBlock.get();
        MouldingBlock mouldingBlock = optionalMouldingBlock.get();
        CornerBlock cornerBlock = optionalCornerBlock.get();

        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.DOOR,
                door -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, door, 3)
                        .input('#', Ingredient.ofItems(sidingBlock))
                        .pattern("##")
                        .pattern("##")
                        .pattern("##")
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.TRAPDOOR,
                trapdoor -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, trapdoor, 2)
                        .input('#', Ingredient.ofItems(sidingBlock))
                        .pattern("###")
                        .pattern("###")
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.PRESSURE_PLATE,
                pressurePlate -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, pressurePlate)
                        .pattern("ss")
                        .input('s', sidingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE,
                fence -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.DECORATIONS, fence, 3)
                        .pattern("sms")
                        .pattern("sms")
                        .input('s', sidingBlock)
                        .input('m', mouldingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE_GATE,
                fenceGate -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, fenceGate)
                        .pattern("msm")
                        .pattern("msm")
                        .input('s', sidingBlock)
                        .input('m', mouldingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.SIGN,
                sign -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.DECORATIONS, sign)
                        .pattern("s")
                        .pattern("m")
                        .input('s', sidingBlock)
                        .input('m', mouldingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.STAIRS,
                stair -> SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, stair)
                        .pattern("m ")
                        .pattern("mm")
                        .input('m', mouldingBlock)
                        .criterion("has_moulding", conditionsFromItem(mouldingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.BUTTON,
                button -> SoulForgeShapelessRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, button)
                        .input(cornerBlock)
                        .criterion(hasItem(cornerBlock), conditionsFromItem(cornerBlock)));
    }

    @Override
    public void generate(RecipeExporter exporter) {
        BlockFamilies.getFamilies()
                .filter(blockFamily -> !blockFamily.getGroup().orElse("").equals("wooden"))
                .forEach(blockFamily -> createHighEfficiencyBlockFamilyRecipes(blockFamily, exporter));

        for (int i = 0; i < BwtBlocks.sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = BwtBlocks.sidingBlocks.get(i);
            MouldingBlock mouldingBlock = BwtBlocks.mouldingBlocks.get(i);
            CornerBlock cornerBlock = BwtBlocks.cornerBlocks.get(i);
            ColumnBlock columnBlock = BwtBlocks.columnBlocks.get(i);
            PedestalBlock pedestalBlock = BwtBlocks.pedestalBlocks.get(i);
            TableBlock tableBlock = BwtBlocks.tableBlocks.get(i);
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
                    .offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(sidingBlock).getPath());
            SoulForgeShapelessRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .input(mouldingBlock, 2)
                    .group("siding")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(mouldingBlock).getPath());
            SoulForgeShapelessRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .input(cornerBlock, 2)
                    .group("moulding")
                    .criterion(hasItem(cornerBlock), conditionsFromItem(cornerBlock))
                    .offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(cornerBlock).getPath());

            // Decorative blocks
            SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, columnBlock)
                    .pattern("#")
                    .pattern("#")
                    .pattern("#")
                    .input('#', mouldingBlock)
                    .group("column")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter);
            SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pedestalBlock, 6)
                    .pattern(" s ")
                    .pattern("###")
                    .pattern("###")
                    .input('#', sidingBlock.fullBlock)
                    .input('s', sidingBlock)
                    .group("pedestal")
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter);
            SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, tableBlock, 4)
                    .pattern("sss")
                    .pattern(" m ")
                    .pattern(" m ")
                    .input('s', sidingBlock)
                    .input('m', mouldingBlock)
                    .group("table")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter);
        }

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_BLOCK)
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .input('s', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);

        // Netherite Tools
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE)
                .pattern("sss")
                .pattern(" h ")
                .pattern(" h ")
                .pattern(" h ")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL)
                .pattern("s")
                .pattern("h")
                .pattern("h")
                .pattern("h")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_AXE)
                .pattern("ss")
                .pattern("sh")
                .pattern(" h")
                .pattern(" h")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_HOE)
                .pattern("ss")
                .pattern(" h")
                .pattern(" h")
                .pattern(" h")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_SWORD)
                .pattern("s")
                .pattern("s")
                .pattern("s")
                .pattern("h")
                .input('s', Items.NETHERITE_INGOT)
                .input('h', BwtItems.haftItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);

        // Netherite armor
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.armorPlateItem)
                .pattern("SnpS")
                .input('n', Items.NETHERITE_INGOT)
                .input('S', BwtItems.strapItem)
                .input('p', BwtItems.paddingItem)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_HELMET)
                .pattern("ssss")
                .pattern("s  s")
                .pattern("s  s")
                .pattern(" pp ")
                .input('s', Items.NETHERITE_INGOT)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_CHESTPLATE)
                .pattern("p  p")
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .input('s', Items.NETHERITE_INGOT)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_LEGGINGS)
                .pattern("ssss")
                .pattern("pssp")
                .pattern("p  p")
                .pattern("p  p")
                .input('s', Items.NETHERITE_INGOT)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, Items.NETHERITE_BOOTS)
                .pattern(" ss ")
                .pattern(" ss ")
                .pattern("spps")
                .input('s', Items.NETHERITE_INGOT)
                .input('p', BwtItems.armorPlateItem)
                .criterion(hasItem(BwtItems.armorPlateItem), conditionsFromItem(BwtItems.armorPlateItem))
                .offerTo(exporter);

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.obsidianPressuePlateBlock)
                .pattern("oooo")
                .input('o', Items.OBSIDIAN)
                .criterion(hasItem(Items.OBSIDIAN), conditionsFromItem(Items.OBSIDIAN))
                .offerTo(exporter);
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.TOOLS, BwtItems.broadheadItem, 16)
                .pattern(" s ")
                .pattern("sss")
                .pattern(" s ")
                .pattern(" s ")
                .input('s', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
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

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.detectorBlock)
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
        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.buddyBlock)
                .pattern("sses")
                .pattern("etts")
                .pattern("stte")
                .pattern("sess")
                .input('s', Items.STONE)
                .input('e', BwtItems.redstoneEyeItem)
                .input('t', Items.REDSTONE_TORCH)
                .criterion(hasItem(BwtItems.redstoneEyeItem), conditionsFromItem(BwtItems.redstoneEyeItem))
                .offerTo(exporter);

        SoulForgeShapedRecipe.JsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.blockDispenserBlock)
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

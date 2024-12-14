package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class CraftingRecipeGenerator extends FabricRecipeProvider {
    public CraftingRecipeGenerator(FabricDataOutput generator, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(generator, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateCraftingRecipes(exporter);
        generateHighEfficiencyRecipes(exporter);
    }

    public void generateCraftingRecipes(RecipeExporter exporter) {
        generateTier1Recipes(exporter);
        generateTier2Recipes(exporter);
        generateTier3Recipes(exporter);
        generateTier4Recipes(exporter);
        generateTier5Recipes(exporter);
        generateTier6Recipes(exporter);
        generateTier7Recipes(exporter);

        generateVaseDyeingRecipes(exporter);
        generateWoolSlabRecipes(exporter);
        generateDungDyeingRecipes(exporter);
        generateCompactingRecipes(exporter);
        generateBloodWoodRecipes(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.stoneDetectorRailBlock, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .input('i', Items.IRON_INGOT)
                .input('p', Items.STONE_PRESSURE_PLATE)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(Items.STONE_PRESSURE_PLATE), conditionsFromItem(Items.STONE_PRESSURE_PLATE))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.obsidianDetectorRailBlock, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .input('i', Items.IRON_INGOT)
                .input('p', BwtBlocks.obsidianPressuePlateBlock)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtBlocks.obsidianPressuePlateBlock), conditionsFromItem(BwtBlocks.obsidianPressuePlateBlock))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BwtBlocks.vineTrapBlock)
                .pattern("vvv")
                .input('v', Items.VINE)
                .criterion(hasItem(Items.VINE), conditionsFromItem(Items.VINE))
                .offerTo(exporter);
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DIRT, BwtBlocks.dirtSlabBlock, "dirt");
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GRASS_BLOCK, BwtBlocks.grassSlabBlock, "grass");
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MYCELIUM, BwtBlocks.myceliumSlabBlock, "mycelium");
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PODZOL, BwtBlocks.podzolSlabBlock, "podzol");
    }

    private void generateBloodWoodRecipes(RecipeExporter exporter) {
        generateFamily(exporter, BwtBlocks.bloodWoodBlocks.blockFamily, FeatureSet.of(FeatureFlags.VANILLA));
        offerPlanksRecipe2(exporter, BwtBlocks.bloodWoodBlocks.planksBlock, BwtItemTags.BLOOD_WOOD_LOGS, 4);
        offerBarkBlockRecipe(exporter, BwtBlocks.bloodWoodBlocks.woodBlock, BwtBlocks.bloodWoodBlocks.logBlock);
        offerBarkBlockRecipe(exporter, BwtBlocks.bloodWoodBlocks.strippedWoodBlock, BwtBlocks.bloodWoodBlocks.strippedLogBlock);
        offerHangingSignRecipe(exporter, BwtBlocks.bloodWoodBlocks.hangingSignBlock, BwtBlocks.bloodWoodBlocks.strippedLogBlock);
    }

    private void generateCompactingRecipes(RecipeExporter exporter) {
        offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, RecipeCategory.MISC, BwtItems.soapItem, RecipeCategory.DECORATIONS, BwtBlocks.soapBlock, "soap_from_block", "soap");
        offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, RecipeCategory.MISC, BwtItems.dungItem, RecipeCategory.DECORATIONS, BwtBlocks.dungBlock, "dung_from_block", "dung");
        offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, RecipeCategory.MISC, BwtItems.concentratedHellfireItem, RecipeCategory.DECORATIONS, BwtBlocks.concentratedHellfireBlock, "concentrated_hellfire_from_block", "concentrated_hellfire");
        offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, RecipeCategory.MISC, BwtItems.paddingItem, RecipeCategory.DECORATIONS, BwtBlocks.paddingBlock, "padding_from_block", "padding");
        offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, RecipeCategory.MISC, BwtItems.ropeItem, RecipeCategory.DECORATIONS, BwtBlocks.ropeCoilBlock, "rope_from_block", "rope");
        offer2x2BlockSlabFamily(exporter, BwtBlocks.wickerPaneBlock, BwtBlocks.wickerBlock, BwtBlocks.wickerSlabBlock, "wicker");
    }

    public static void offerCompacting2x2(RecipeExporter exporter, ItemConvertible inputItem, ItemConvertible outputBlock, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapedRecipeJsonBuilder.create(category, outputBlock).input('#', inputItem).pattern("##").pattern("##").criterion(hasItem(inputItem), conditionsFromItem(inputItem)).group(group).offerTo(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getRecipeName(outputBlock)));
    }

    public static void offerUncompacting2x2(RecipeExporter exporter, ItemConvertible inputBlock, ItemConvertible outputItem, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapelessRecipeJsonBuilder.create(category, outputItem, 4).input(inputBlock).group(group).criterion(hasItem(inputBlock), conditionsFromItem(inputBlock)).offerTo(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getRecipeName(outputItem)));
    }

    public static void offer2x1SlabCreating(RecipeExporter exporter, ItemConvertible inputBlock, ItemConvertible outputSlab, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapedRecipeJsonBuilder.create(category, outputSlab, 4).input('#', inputBlock).pattern("##").criterion(hasItem(inputBlock), conditionsFromItem(inputBlock)).group(group).offerTo(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getRecipeName(outputSlab)));
    }

    public static void offer2x1SlabRecombining(RecipeExporter exporter, ItemConvertible inputSlab, ItemConvertible outputBlock, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapedRecipeJsonBuilder.create(category, outputBlock).input('#', inputSlab).pattern("#").pattern("#").criterion(hasItem(inputSlab), conditionsFromItem(inputSlab)).group(group).offerTo(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getRecipeName(outputBlock)));
    }

    public static void offer2x1SlabUncompacting(RecipeExporter exporter, ItemConvertible inputSlab, ItemConvertible outputItem, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapelessRecipeJsonBuilder.create(category, outputItem, 2).input(inputSlab).group(group).criterion(hasItem(inputSlab), conditionsFromItem(inputSlab)).offerTo(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getRecipeName(outputItem)));
    }

    public static void offer2x1SlabRecipes(RecipeExporter exporter, RecipeCategory category, ItemConvertible block, ItemConvertible slab, String itemGroup) {
        offer2x1SlabCreating(exporter, block, slab, category, itemGroup + "_slab", itemGroup + "_slab_from_block");
        offer2x1SlabRecombining(exporter, slab, block, category, itemGroup + "_block", itemGroup + "_block_from_slab");
    }

    public static void offer2x2BlockSlabFamily(RecipeExporter exporter, ItemConvertible baseItem, ItemConvertible block, ItemConvertible slab, String itemGroup) {
        offerCompacting2x2(exporter, baseItem, block, RecipeCategory.DECORATIONS, itemGroup + "_block", null);
        offerUncompacting2x2(exporter, block, baseItem, RecipeCategory.MISC, itemGroup, itemGroup + "_from_block");
        offer2x1SlabRecipes(exporter, RecipeCategory.DECORATIONS, block, slab, itemGroup);
        offer2x1SlabUncompacting(exporter, slab, baseItem, RecipeCategory.MISC, itemGroup, itemGroup + "_from_slab");
    }

    private void generateDungDyeingRecipes(RecipeExporter exporter) {
        DyeItem dung = BwtItems.dungItem;

        // This is a little unnecessary to declare separately, but it helps keep track of what we're doing
        VaseBlock brownVase = BwtBlocks.vaseBlocks.get(BwtItems.dungItem.getColor());
        Block brownBed = Blocks.BROWN_BED;
        Block brownWool = Blocks.BROWN_WOOL;
        Block brownCarpet = Blocks.BROWN_CARPET;
        Block brownTerracotta = Blocks.BROWN_TERRACOTTA;
        Block brownConcretePowder = Blocks.BROWN_CONCRETE_POWDER;
        Block brownStainedGlass = Blocks.BROWN_STAINED_GLASS;
        Block brownStainedGlassPane = Blocks.BROWN_STAINED_GLASS_PANE;
        Block brownCandle = Blocks.BROWN_CANDLE;

        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, brownVase)
                .input(dung)
                .input(Ingredient.ofStacks(
                        DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).filter(dyeable -> !dyeable.equals(brownVase)).map(ItemStack::new)
                ))
                .group("vases")
                .criterion("has_needed_dye", RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, "bwt:dye_" + RecipeProvider.getItemPath(brownVase) + "_from_dung");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, brownCandle)
                .input(Blocks.CANDLE)
                .input(dung)
                .group("dyed_candle")
                .criterion(RecipeProvider.hasItem(dung), RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, CraftingRecipeJsonBuilder.getItemId(Blocks.BROWN_CANDLE) + "_from_dung");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, brownBed)
                .input(dung)
                .input(Ingredient.ofStacks(Stream.of(Items.BLACK_BED, Items.BLUE_BED, Items.CYAN_BED, Items.GRAY_BED, Items.GREEN_BED, Items.LIGHT_BLUE_BED, Items.LIGHT_GRAY_BED, Items.LIME_BED, Items.MAGENTA_BED, Items.ORANGE_BED, Items.PINK_BED, Items.PURPLE_BED, Items.RED_BED, Items.YELLOW_BED, Items.WHITE_BED).map(ItemStack::new)))
                .group("bed")
                .criterion(RecipeProvider.hasItem(dung), RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, "bwt:dye_" + RecipeProvider.getItemPath(brownBed) + "_from_dung");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, brownWool)
                .input(dung)
                .input(Ingredient.ofStacks(Stream.of(Items.BLACK_WOOL, Items.BLUE_WOOL, Items.CYAN_WOOL, Items.GRAY_WOOL, Items.GREEN_WOOL, Items.LIGHT_BLUE_WOOL, Items.LIGHT_GRAY_WOOL, Items.LIME_WOOL, Items.MAGENTA_WOOL, Items.ORANGE_WOOL, Items.PINK_WOOL, Items.PURPLE_WOOL, Items.RED_WOOL, Items.YELLOW_WOOL, Items.WHITE_WOOL).map(ItemStack::new)))
                .group("wool")
                .criterion(RecipeProvider.hasItem(dung), RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, "bwt:dye_" + RecipeProvider.getItemPath(brownWool) + "_from_dung");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, brownCarpet)
                .input(dung)
                .input(Ingredient.ofStacks(Stream.of(Items.BLACK_CARPET, Items.BLUE_CARPET, Items.CYAN_CARPET, Items.GRAY_CARPET, Items.GREEN_CARPET, Items.LIGHT_BLUE_CARPET, Items.LIGHT_GRAY_CARPET, Items.LIME_CARPET, Items.MAGENTA_CARPET, Items.ORANGE_CARPET, Items.PINK_CARPET, Items.PURPLE_CARPET, Items.RED_CARPET, Items.YELLOW_CARPET, Items.WHITE_CARPET).map(ItemStack::new)))
                .group("carpet")
                .criterion(RecipeProvider.hasItem(dung), RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, "bwt:dye_" + RecipeProvider.getItemPath(brownCarpet) + "_from_dung");
        offerTerracottaDyeingRecipe(exporter, brownTerracotta, dung);
        offerConcretePowderDyeingRecipe(exporter, brownConcretePowder, dung);
        offerStainedGlassDyeingRecipe(exporter, brownStainedGlass, dung);
        offerStainedGlassPaneDyeingRecipe(exporter, brownStainedGlassPane, dung);
    }

    private void generateVaseDyeingRecipes(RecipeExporter exporter) {
        List<Item> dyes = List.copyOf(DyeUtils.DYE_COLORS_ORDERED.stream().map(DyeItem::byColor).toList());
        List<Item> vases = DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).map(VaseBlock::asItem).toList();
        offerDyeableRecipes(exporter, dyes, vases, "vases");
    }

    private void generateWoolSlabRecipes(RecipeExporter exporter) {
        List<Item> dyes = List.copyOf(DyeUtils.DYE_COLORS_ORDERED.stream().map(DyeItem::byColor).toList());
        List<Item> woolSlabs = DyeUtils.streamColorItemsSorted(BwtBlocks.woolSlabBlocks).map(SlabBlock::asItem).toList();
        offerDyeableRecipes(exporter, dyes, woolSlabs, "wool_slabs");
        BwtBlocks.woolSlabBlocks.forEach((dyeColor, woolSlab) -> {
            Item woolBlockItem = DyeUtils.WOOL_COLORS.get(dyeColor).asItem();
            createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, woolSlab, Ingredient.ofItems(woolBlockItem)).criterion(hasItem(woolBlockItem), conditionsFromItem(woolBlockItem)).group("wool_slabs").offerTo(exporter);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, woolBlockItem, 1).input(woolSlab, 2).criterion(hasItem(woolSlab), conditionsFromItem(woolSlab)).group("wool").offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(woolSlab).getPath());
        });
    }

    private void generateTier1Recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.gearItem, 2)
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .input('s', Items.STICK)
                .input('p', ItemTags.PLANKS)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.handCrankBlock)
                .pattern("  s")
                .pattern(" s ")
                .pattern("cgc")
                .input('s', Items.STICK)
                .input('c', ItemTags.STONE_CRAFTING_MATERIALS)
                .input('g', BwtItems.gearItem)
                .criterion(hasItem(BwtItems.gearItem), conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.millStoneBlock)
                .pattern("sss")
                .pattern("sss")
                .pattern("sgs")
                .input('s', Items.STONE)
                .input('g', BwtItems.gearItem)
                .criterion(hasItem(BwtItems.gearItem), conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .pattern("ibi")
                .pattern("iwi")
                .pattern("iii")
                .input('i', Items.IRON_INGOT)
                .input('b', Items.BONE)
                .input('w', Items.WATER_BUCKET)
                .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .input(Blocks.CAULDRON)
                .input(Items.BONE)
                .input(Items.WATER_BUCKET)
                .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                .offerTo(exporter, Id.of("cauldron_from_vanilla"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.lightBlockBlock)
                .pattern(" p ")
                .pattern("pfp")
                .pattern(" r ")
                .input('p', ConventionalItemTags.GLASS_PANES)
                .input('f', BwtItems.filamentItem)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtItems.filamentItem), conditionsFromItem(BwtItems.filamentItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.fabricItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("fff")
                .input('f', BwtItems.hempFiberItem)
                .criterion(hasItem(BwtItems.hempFiberItem), conditionsFromItem(BwtItems.hempFiberItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("ppp")
                .input('f', BwtItems.fabricItem)
                .input('p', ItemTags.PLANKS)
                .criterion(hasItem(BwtItems.fabricItem), conditionsFromItem(BwtItems.fabricItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.windmillItem)
                .pattern(" s ")
                .pattern("s s")
                .pattern(" s ")
                .input('s', BwtItems.sailItem)
                .criterion(hasItem(BwtItems.sailItem), conditionsFromItem(BwtItems.sailItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.gearBoxBlock)
                .pattern("pgp")
                .pattern("grg")
                .pattern("pgp")
                .input('p', ItemTags.PLANKS)
                .input('g', BwtItems.gearItem)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtItems.gearItem), conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("fff")
                .pattern("fff")
                .input('f', BwtItems.hempFiberItem)
                .criterion(hasItem(BwtItems.hempFiberItem), conditionsFromItem(BwtItems.hempFiberItem))
                .group("rope")
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("ff")
                .pattern("ff")
                .pattern("ff")
                .input('f', BwtItems.hempFiberItem)
                .criterion(hasItem(BwtItems.hempFiberItem), conditionsFromItem(BwtItems.hempFiberItem))
                .group("rope")
                .offerTo(exporter, "bwt:rope_vertical");
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.axleBlock)
                .pattern("prp")
                .input('p', ItemTags.PLANKS)
                .input('r', BwtItems.ropeItem)
                .criterion(hasItem(BwtItems.ropeItem), conditionsFromItem(BwtItems.ropeItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.anchorBlock)
                .pattern(" i ")
                .pattern("sss")
                .input('i', Items.IRON_INGOT)
                .input('s', Items.STONE)
                .criterion(hasItem(Items.STONE), conditionsFromItem(Items.STONE))
                .offerTo(exporter);
    }

    private void generateTier2Recipes(RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.strapItem, 8)
                .input(BwtItems.tannedLeatherItem)
                .criterion(hasItem(BwtItems.tannedLeatherItem), conditionsFromItem(BwtItems.tannedLeatherItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.beltItem)
                .pattern(" s ")
                .pattern("s s")
                .pattern(" s ")
                .input('s', BwtItems.strapItem)
                .criterion(hasItem(BwtItems.strapItem), conditionsFromItem(BwtItems.strapItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("pgp")
                .input('i', Items.IRON_INGOT)
                .input('g', BwtItems.gearItem)
                .input('p', ItemTags.PLANKS)
                .input('b', BwtItems.beltItem)
                .criterion(hasItem(BwtItems.beltItem), conditionsFromItem(BwtItems.beltItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.grateBlock)
                .pattern("ss")
                .pattern("ss")
                .input('s', Items.STICK)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.wickerPaneBlock)
                .pattern("ss")
                .pattern("ss")
                .input('s', Items.SUGAR_CANE)
                .criterion(hasItem(Items.SUGAR_CANE), conditionsFromItem(Items.SUGAR_CANE))
                .group("wicker")
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.slatsBlock)
                .pattern("mm")
                .pattern("mm")
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion("has_wooden_moulding", conditionsFromTag(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.pulleyBlock)
                .pattern("pip")
                .pattern("grg")
                .pattern("pip")
                .input('p', ItemTags.PLANKS)
                .input('i', Items.IRON_INGOT)
                .input('g', BwtItems.gearItem)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtItems.gearItem), conditionsFromItem(BwtItems.gearItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.platformBlock)
                .pattern("pwp")
                .pattern(" p ")
                .pattern("pwp")
                .input('p', ItemTags.PLANKS)
                .input('w', BwtBlocks.wickerPaneBlock)
                .criterion(hasItem(BwtBlocks.wickerPaneBlock), conditionsFromItem(BwtBlocks.wickerPaneBlock))
                .offerTo(exporter);
    }

    private void generateTier3Recipes(RecipeExporter exporter) {
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
            // Wooden Mini block recombining recipes
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                    .input(sidingBlock, 2)
                    .group("planks")
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(sidingBlock).getPath());
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .input(mouldingBlock, 2)
                    .group("siding")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(mouldingBlock).getPath());
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .input(cornerBlock, 2)
                    .group("moulding")
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter, "bwt:recombine_" + Registries.BLOCK.getId(cornerBlock).getPath());
            // Decorative blocks
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, columnBlock)
                    .pattern("#")
                    .pattern("#")
                    .pattern("#")
                    .input('#', mouldingBlock)
                    .group("column")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pedestalBlock, 6)
                    .pattern(" s ")
                    .pattern("###")
                    .pattern("###")
                    .input('#', sidingBlock.fullBlock)
                    .input('s', sidingBlock)
                    .group("pedestal")
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, tableBlock, 4)
                    .pattern("sss")
                    .pattern(" m ")
                    .pattern(" m ")
                    .input('s', sidingBlock)
                    .input('m', mouldingBlock)
                    .group("table")
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter);
        }

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.hopperBlock)
                .pattern("s s")
                .pattern("gpg")
                .pattern(" c ")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('g', BwtItems.gearItem)
                .input('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .input('c', BwtItemTags.WOODEN_CORNER_BLOCKS)
                .criterion("has_wooden_corner", conditionsFromTag(BwtItemTags.WOODEN_CORNER_BLOCKS))
                .offerTo(exporter);
    }

    private void generateTier4Recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.hibachiBlock)
                .pattern("hhh")
                .pattern("sfs")
                .pattern("srs")
                .input('h', BwtItems.concentratedHellfireItem)
                .input('s', Items.STONE)
                .input('f', BwtItems.filamentItem)
                .input('r', Items.REDSTONE)
                .criterion(hasItem(BwtItems.filamentItem), conditionsFromItem(BwtItems.filamentItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.bellowsBlock)
                .pattern("sss")
                .pattern("lll")
                .pattern("gbg")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('l', BwtItems.tannedLeatherItem)
                .input('g', BwtItems.gearItem)
                .input('b', BwtItems.beltItem)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter);
    }

    private void generateTier5Recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.woodBladeItem)
                .pattern("s  ")
                .pattern("sgs")
                .pattern("s  ")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('g', BwtItems.glueItem)
                .criterion(hasItem(BwtItems.glueItem), conditionsFromItem(BwtItems.glueItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.waterWheelItem)
                .pattern("bbb")
                .pattern("b b")
                .pattern("bbb")
                .input('b', BwtItems.woodBladeItem)
                .criterion(hasItem(BwtItems.woodBladeItem), conditionsFromItem(BwtItems.woodBladeItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.turntableBlock)
                .pattern("www")
                .pattern("srs")
                .pattern("sgs")
                .input('w', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('s', Items.STONE)
                .input('r', Items.REDSTONE)
                .input('g', BwtItems.gearItem)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.dynamiteItem)
                .pattern("ph")
                .pattern("pt")
                .pattern("ps")
                .input('p', Items.PAPER)
                .input('h', BwtItems.hellfireDustItem)
                .input('t', BwtItems.tallowItem)
                .input('s', BwtItemTags.SAW_DUSTS)
                .criterion("has_tallow", conditionsFromItem(BwtItems.tallowItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.miningChargeBlock)
                .pattern("rgr")
                .pattern("ddd")
                .pattern("ddd")
                .input('r', BwtItems.ropeItem)
                .input('g', BwtItems.glueItem)
                .input('d', BwtItems.dynamiteItem)
                .criterion("has_dynamite", conditionsFromItem(BwtItems.dynamiteItem))
                .offerTo(exporter, "bwt:mining_charge_with_glue");
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.miningChargeBlock)
                .pattern("rsr")
                .pattern("ddd")
                .pattern("ddd")
                .input('r', BwtItems.ropeItem)
                .input('s', Items.SLIME_BALL)
                .input('d', BwtItems.dynamiteItem)
                .criterion("has_dynamite", conditionsFromItem(BwtItems.dynamiteItem))
                .offerTo(exporter, "bwt:mining_charge_with_slime");
    }

    private void generateTier6Recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BwtBlocks.soilPlanterBlock)
                .pattern("d")
                .pattern("b")
                .pattern("p")
                .input('d', Items.DIRT)
                .input('b', Items.BONE_MEAL)
                .input('p', BwtBlocks.planterBlock)
                .criterion(hasItem(BwtBlocks.planterBlock), conditionsFromItem(BwtBlocks.planterBlock))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BwtBlocks.soulSandPlanterBlock)
                .pattern("s")
                .pattern("p")
                .input('s', Items.SOUL_SAND)
                .input('p', BwtBlocks.planterBlock)
                .criterion(hasItem(BwtBlocks.planterBlock), conditionsFromItem(BwtBlocks.planterBlock))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BwtBlocks.grassPlanterBlock)
                .pattern("g")
                .pattern("b")
                .pattern("p")
                .input('g', Items.GRASS_BLOCK)
                .input('b', Items.BONE_MEAL)
                .input('p', BwtBlocks.planterBlock)
                .criterion(hasItem(BwtBlocks.planterBlock), conditionsFromItem(BwtBlocks.planterBlock))
                .offerTo(exporter);
    }

    private void generateTier7Recipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.haftItem)
                .pattern("s")
                .pattern("g")
                .pattern("m")
                .input('s', BwtItems.strapItem)
                .input('g', BwtItems.glueItem)
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion(hasItem(BwtItems.glueItem), conditionsFromItem(BwtItems.glueItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.paddingItem)
                .pattern(" F ")
                .pattern("fff")
                .pattern(" F ")
                .input('F', BwtItems.fabricItem)
                .input('f', Items.FEATHER)
                .criterion(hasItem(BwtItems.fabricItem), conditionsFromItem(BwtItems.fabricItem))
                .group("padding")
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.broadheadArrowItem, 4)
                .pattern("b")
                .pattern("m")
                .pattern("f")
                .input('b', BwtItems.broadheadItem)
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .input('f', Items.FEATHER)
                .criterion(hasItem(BwtItems.broadheadItem), conditionsFromItem(BwtItems.broadheadItem))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.compositeBowItem)
                .pattern(" mb")
                .pattern("mbs")
                .pattern(" mb")
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .input('b', Items.BONE)
                .input('s', Items.STRING)
                .criterion("has_wooden_moulding", conditionsFromTag(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.soulForgeBlock)
                .pattern("nnn")
                .pattern(" n ")
                .pattern("nnn")
                .input('n', Items.NETHERITE_INGOT)
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter);
    }

    private Identifier highEfficiencyId(ItemConvertible itemConvertible) {
        return Id.of(Registries.ITEM.getId(itemConvertible.asItem()).withPrefixedPath("he_").getPath());
    }

    private void generateHighEfficiencyRecipes(RecipeExporter exporter) {
        Optional<SidingBlock> stoneSiding = BwtBlocks.sidingBlocks.stream().filter(sidingBlock -> sidingBlock.fullBlock == Blocks.STONE).findAny();

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("mmm")
                .input('f', BwtItems.fabricItem)
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion(hasItem(BwtItems.fabricItem), conditionsFromItem(BwtItems.fabricItem))
                .offerTo(exporter, highEfficiencyId(BwtItems.sailItem));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("sgs")
                .input('i', Items.IRON_INGOT)
                .input('g', BwtItems.gearItem)
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('b', BwtItems.beltItem)
                .criterion(hasItem(BwtItems.beltItem), conditionsFromItem(BwtItems.beltItem))
                .offerTo(exporter, highEfficiencyId(BwtBlocks.sawBlock));
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.gearBoxBlock)
                .pattern("sgs")
                .pattern("grg")
                .pattern("sgs")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('g', BwtItems.gearItem)
                .input('r', Items.REDSTONE)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(BwtBlocks.gearBoxBlock));
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.PISTON)
                .pattern("sss")
                .pattern("cic")
                .pattern("crc")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('i', Items.IRON_INGOT)
                .input('r', Items.REDSTONE)
                .input('c', Items.COBBLESTONE)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Items.PISTON));
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.BOOKSHELF)
                .pattern("sss")
                .pattern("bbb")
                .pattern("sss")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('b', Items.BOOK)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Items.BOOKSHELF));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.CHEST)
                .pattern("sss")
                .pattern("s s")
                .pattern("sss")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Blocks.CHEST));
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK)
                .pattern("sss")
                .pattern("srs")
                .pattern("sss")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('r', Items.REDSTONE)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Blocks.NOTE_BLOCK));
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.JUKEBOX)
                .pattern("sss")
                .pattern("sds")
                .pattern("sss")
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('d', Items.DIAMOND)
                .criterion("has_wooden_siding", conditionsFromTag(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Blocks.JUKEBOX));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.LADDER, 3)
                .pattern("m m")
                .pattern("mmm")
                .pattern("m m")
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion("has_wooden_moulding", conditionsFromTag(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Blocks.LADDER));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STICK)
                .group("sticks")
                .pattern("m")
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion("has_wooden_moulding", conditionsFromTag(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Items.STICK));
        stoneSiding.ifPresent(sidingBlock -> ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.REPEATER)
                .pattern("trt")
                .pattern("sss")
                .input('t', Items.REDSTONE_TORCH)
                .input('r', Items.REDSTONE)
                .input('s', sidingBlock)
                .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                .offerTo(exporter, highEfficiencyId(Blocks.REPEATER)));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BOOK)
                .input(BwtItems.tannedLeatherItem)
                .input(Items.PAPER, 6)
                .criterion(hasItem(BwtItems.tannedLeatherItem), conditionsFromItem(BwtItems.tannedLeatherItem))
                .offerTo(exporter, highEfficiencyId(Items.BOOK));
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.ITEM_FRAME, 2)
                .pattern("mmm")
                .pattern("mtm")
                .pattern("mmm")
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .input('t', BwtItems.tannedLeatherItem)
                .criterion("has_wooden_moulding", conditionsFromTag(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .offerTo(exporter, highEfficiencyId(Items.ITEM_FRAME));
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.axleBlock)
                .pattern("prp")
                .input('p', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .input('r', BwtItems.ropeItem)
                .criterion(hasItem(BwtItems.ropeItem), conditionsFromItem(BwtItems.ropeItem))
                .offerTo(exporter, highEfficiencyId(BwtBlocks.axleBlock));
        BlockFamilies.getFamilies()
                .filter(blockFamily -> blockFamily.getGroup().orElse("").equals("wooden"))
                .forEach(blockFamily -> createHighEfficiencyBlockFamilyRecipes(blockFamily, exporter));
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
                door -> createDoorRecipe(door, Ingredient.ofItems(sidingBlock))
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.TRAPDOOR,
                trapdoor -> createTrapdoorRecipe(trapdoor, Ingredient.ofItems(sidingBlock))
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.PRESSURE_PLATE,
                pressurePlate -> ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, pressurePlate)
                        .pattern("ss")
                        .input('s', sidingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE,
                fence -> ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, fence, 3)
                        .pattern("sms")
                        .pattern("sms")
                        .input('s', sidingBlock)
                        .input('m', mouldingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE_GATE,
                fenceGate -> ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, fenceGate)
                        .pattern("msm")
                        .pattern("msm")
                        .input('s', sidingBlock)
                        .input('m', mouldingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.SIGN,
                sign -> ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, sign)
                        .pattern("s")
                        .pattern("m")
                        .input('s', sidingBlock)
                        .input('m', mouldingBlock)
                        .criterion("has_siding", conditionsFromItem(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.STAIRS,
                stair -> ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, stair)
                        .pattern("m ")
                        .pattern("mm")
                        .input('m', mouldingBlock)
                        .criterion("has_moulding", conditionsFromItem(mouldingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.BUTTON,
                button -> ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, button)
                        .input(cornerBlock)
                        .criterion(hasItem(cornerBlock), conditionsFromItem(cornerBlock)));
    }

}

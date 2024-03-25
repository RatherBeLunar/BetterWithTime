package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.DyeUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;
import java.util.stream.Stream;

public class CraftingRecipeGenerator extends FabricRecipeProvider {
    public CraftingRecipeGenerator(FabricDataOutput generator) {
        super(generator);
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
        generateDungDyeingRecipes(exporter);

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
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtBlocks.obsidianPressuePlateBlock)
                .pattern("oo")
                .input('o', Items.OBSIDIAN)
                .criterion(hasItem(Items.OBSIDIAN), conditionsFromItem(Items.OBSIDIAN))
                .offerTo(exporter);
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
                .offerTo(exporter, "dye_" + RecipeProvider.getItemPath(brownVase) + "_from_dung");
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
                .offerTo(exporter, "dye_" + RecipeProvider.getItemPath(brownBed) + "_from_dung");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, brownWool)
                .input(dung)
                .input(Ingredient.ofStacks(Stream.of(Items.BLACK_WOOL, Items.BLUE_WOOL, Items.CYAN_WOOL, Items.GRAY_WOOL, Items.GREEN_WOOL, Items.LIGHT_BLUE_WOOL, Items.LIGHT_GRAY_WOOL, Items.LIME_WOOL, Items.MAGENTA_WOOL, Items.ORANGE_WOOL, Items.PINK_WOOL, Items.PURPLE_WOOL, Items.RED_WOOL, Items.YELLOW_WOOL, Items.WHITE_WOOL).map(ItemStack::new)))
                .group("wool")
                .criterion(RecipeProvider.hasItem(dung), RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, "dye_" + RecipeProvider.getItemPath(brownWool) + "_from_dung");
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, brownCarpet)
                .input(dung)
                .input(Ingredient.ofStacks(Stream.of(Items.BLACK_CARPET, Items.BLUE_CARPET, Items.CYAN_CARPET, Items.GRAY_CARPET, Items.GREEN_CARPET, Items.LIGHT_BLUE_CARPET, Items.LIGHT_GRAY_CARPET, Items.LIME_CARPET, Items.MAGENTA_CARPET, Items.ORANGE_CARPET, Items.PINK_CARPET, Items.PURPLE_CARPET, Items.RED_CARPET, Items.YELLOW_CARPET, Items.WHITE_CARPET).map(ItemStack::new)))
                .group("carpet")
                .criterion(RecipeProvider.hasItem(dung), RecipeProvider.conditionsFromItem(dung))
                .offerTo(exporter, "dye_" + RecipeProvider.getItemPath(brownCarpet) + "_from_dung");
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
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("ff")
                .pattern("ff")
                .pattern("ff")
                .input('f', BwtItems.hempFiberItem)
                .criterion(hasItem(BwtItems.hempFiberItem), conditionsFromItem(BwtItems.hempFiberItem))
                .offerTo(exporter, "rope_vertical");
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
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.wickerBlock)
                .pattern("ss")
                .pattern("ss")
                .input('s', Items.SUGAR_CANE)
                .criterion(hasItem(Items.SUGAR_CANE), conditionsFromItem(Items.SUGAR_CANE))
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
                .input('w', BwtBlocks.wickerBlock)
                .criterion(hasItem(BwtBlocks.wickerBlock), conditionsFromItem(BwtBlocks.wickerBlock))
                .offerTo(exporter);
    }

    private void generateTier3Recipes(RecipeExporter exporter) {
        // Mini block recombining recipes
        for (int i = 0; i < BwtBlocks.sidingBlocks.size(); i++) {
            SidingBlock sidingBlock = BwtBlocks.sidingBlocks.get(i);
            MouldingBlock mouldingBlock = BwtBlocks.mouldingBlocks.get(i);
            CornerBlock cornerBlock = BwtBlocks.cornerBlocks.get(i);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                    .input(sidingBlock, 2)
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter, "recombine_" + Registries.BLOCK.getId(sidingBlock).getPath());
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .input(mouldingBlock, 2)
                    .criterion(hasItem(mouldingBlock), conditionsFromItem(mouldingBlock))
                    .offerTo(exporter, "recombine_" + Registries.BLOCK.getId(mouldingBlock).getPath());
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .input(cornerBlock, 2)
                    .criterion(hasItem(sidingBlock), conditionsFromItem(sidingBlock))
                    .offerTo(exporter, "recombine_" + Registries.BLOCK.getId(cornerBlock).getPath());
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
                .offerTo(exporter);
    }

    private void generateHighEfficiencyRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("mmm")
                .input('f', BwtItems.fabricItem)
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion(hasItem(BwtItems.fabricItem), conditionsFromItem(BwtItems.fabricItem))
                .offerTo(exporter, "he_sail");
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("sgs")
                .input('i', Items.IRON_INGOT)
                .input('g', BwtItems.gearItem)
                .input('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .input('b', BwtItems.beltItem)
                .criterion(hasItem(BwtItems.beltItem), conditionsFromItem(BwtItems.beltItem))
                .offerTo(exporter, "he_saw");
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STICK)
                .pattern("m")
                .input('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .criterion("has_wooden_moulding", conditionsFromTag(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .offerTo(exporter, "he_stick");
    }


}

package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class CraftingRecipeGenerator extends FabricRecipeProvider {
    public CraftingRecipeGenerator(FabricDataOutput generator, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(generator, registryLookup);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateCraftingRecipes(exporter);
        generateHighEfficiencyRecipes(exporter);
    }

    public void generateCraftingRecipes(RecipeOutput exporter) {
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

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.stoneDetectorRailBlock, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('p', Items.STONE_PRESSURE_PLATE)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(Items.STONE_PRESSURE_PLATE), has(Items.STONE_PRESSURE_PLATE))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.obsidianDetectorRailBlock, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('p', BwtBlocks.obsidianPressurePlateBlock)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(BwtBlocks.obsidianPressurePlateBlock), has(BwtBlocks.obsidianPressurePlateBlock))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BwtBlocks.vineTrapBlock)
                .pattern("vvv")
                .define('v', Items.VINE)
                .unlockedBy(getHasName(Items.VINE), has(Items.VINE))
                .save(exporter);
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DIRT, BwtBlocks.dirtSlabBlock, "dirt");
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GRASS_BLOCK, BwtBlocks.grassSlabBlock, "grass");
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MYCELIUM, BwtBlocks.myceliumSlabBlock, "mycelium");
        offer2x1SlabRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PODZOL, BwtBlocks.podzolSlabBlock, "podzol");
    }

    private void generateBloodWoodRecipes(RecipeOutput exporter) {
        generateRecipes(exporter, BwtBlocks.bloodWoodBlocks.blockFamily, FeatureFlagSet.of(FeatureFlags.VANILLA));
        planksFromLog(exporter, BwtBlocks.bloodWoodBlocks.planksBlock, BwtItemTags.BLOOD_WOOD_LOGS, 4);
        woodFromLogs(exporter, BwtBlocks.bloodWoodBlocks.woodBlock, BwtBlocks.bloodWoodBlocks.logBlock);
        woodFromLogs(exporter, BwtBlocks.bloodWoodBlocks.strippedWoodBlock, BwtBlocks.bloodWoodBlocks.strippedLogBlock);
    }

    private void generateCompactingRecipes(RecipeOutput exporter) {
        nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, BwtItems.soapItem, RecipeCategory.DECORATIONS, BwtBlocks.soapBlock, "soap_from_block", "soap");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, BwtItems.dungItem, RecipeCategory.DECORATIONS, BwtBlocks.dungBlock, "dung_from_block", "dung");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, BwtItems.concentratedHellfireItem, RecipeCategory.DECORATIONS, BwtBlocks.concentratedHellfireBlock, "concentrated_hellfire_from_block", "concentrated_hellfire");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, BwtItems.paddingItem, RecipeCategory.DECORATIONS, BwtBlocks.paddingBlock, "padding_from_block", "padding");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(exporter, RecipeCategory.MISC, BwtItems.ropeItem, RecipeCategory.DECORATIONS, BwtBlocks.ropeCoilBlock, "rope_from_block", "rope");
        offer2x2BlockSlabFamily(exporter, BwtBlocks.wickerPaneBlock, BwtBlocks.wickerBlock, BwtBlocks.wickerSlabBlock, "wicker");
    }

    public static void offerCompacting2x2(RecipeOutput exporter, ItemLike inputItem, ItemLike outputBlock, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapedRecipeBuilder.shaped(category, outputBlock).define('#', inputItem).pattern("##").pattern("##").unlockedBy(getHasName(inputItem), has(inputItem)).group(group).save(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getSimpleRecipeName(outputBlock)));
    }

    public static void offerUncompacting2x2(RecipeOutput exporter, ItemLike inputBlock, ItemLike outputItem, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, 4).requires(inputBlock).group(group).unlockedBy(getHasName(inputBlock), has(inputBlock)).save(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getSimpleRecipeName(outputItem)));
    }

    public static void offer2x1SlabCreating(RecipeOutput exporter, ItemLike inputBlock, ItemLike outputSlab, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapedRecipeBuilder.shaped(category, outputSlab, 4).define('#', inputBlock).pattern("##").unlockedBy(getHasName(inputBlock), has(inputBlock)).group(group).save(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getSimpleRecipeName(outputSlab)));
    }

    public static void offer2x1SlabRecombining(RecipeOutput exporter, ItemLike inputSlab, ItemLike outputBlock, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapedRecipeBuilder.shaped(category, outputBlock).define('#', inputSlab).pattern("#").pattern("#").unlockedBy(getHasName(inputSlab), has(inputSlab)).group(group).save(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getSimpleRecipeName(outputBlock)));
    }

    public static void offer2x1SlabUncompacting(RecipeOutput exporter, ItemLike inputSlab, ItemLike outputItem, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, 2).requires(inputSlab).group(group).unlockedBy(getHasName(inputSlab), has(inputSlab)).save(exporter, recipeId != null ? Id.of(recipeId) : Id.of(RecipeProvider.getSimpleRecipeName(outputItem)));
    }

    public static void offer2x1SlabRecipes(RecipeOutput exporter, RecipeCategory category, ItemLike block, ItemLike slab, String itemGroup) {
        offer2x1SlabCreating(exporter, block, slab, category, itemGroup + "_slab", itemGroup + "_slab_from_block");
        offer2x1SlabRecombining(exporter, slab, block, category, itemGroup + "_block", itemGroup + "_block_from_slab");
    }

    public static void offer2x2BlockSlabFamily(RecipeOutput exporter, ItemLike baseItem, ItemLike block, ItemLike slab, String itemGroup) {
        offerCompacting2x2(exporter, baseItem, block, RecipeCategory.DECORATIONS, itemGroup + "_block", null);
        offerUncompacting2x2(exporter, block, baseItem, RecipeCategory.MISC, itemGroup, itemGroup + "_from_block");
        offer2x1SlabRecipes(exporter, RecipeCategory.DECORATIONS, block, slab, itemGroup);
        offer2x1SlabUncompacting(exporter, slab, baseItem, RecipeCategory.MISC, itemGroup, itemGroup + "_from_slab");
    }

    private void generateDungDyeingRecipes(RecipeOutput exporter) {
        DyeItem dung = BwtItems.dungItem;

        // This is a little unnecessary to declare separately, but it helps keep track of what we're doing
        VaseBlock brownVase = BwtBlocks.vaseBlocks.get(BwtItems.dungItem.getDyeColor());
        Block brownBed = Blocks.BROWN_BED;
        Block brownWool = Blocks.BROWN_WOOL;
        Block brownCarpet = Blocks.BROWN_CARPET;
        Block brownTerracotta = Blocks.BROWN_TERRACOTTA;
        Block brownConcretePowder = Blocks.BROWN_CONCRETE_POWDER;
        Block brownStainedGlass = Blocks.BROWN_STAINED_GLASS;
        Block brownStainedGlassPane = Blocks.BROWN_STAINED_GLASS_PANE;
        Block brownCandle = Blocks.BROWN_CANDLE;

        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, brownVase)
                .requires(dung)
                .requires(Ingredient.of(
                        DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).filter(dyeable -> !dyeable.equals(brownVase)).map(ItemStack::new)
                ))
                .group("vases")
                .unlockedBy("has_needed_dye", RecipeProvider.has(dung))
                .save(exporter, Id.of("dye_" + RecipeProvider.getItemName(brownVase) + "_from_dung"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, brownCandle)
                .requires(Blocks.CANDLE)
                .requires(dung)
                .group("dyed_candle")
                .unlockedBy(RecipeProvider.getHasName(dung), RecipeProvider.has(dung))
                .save(exporter, RecipeBuilder.getDefaultRecipeId(Blocks.BROWN_CANDLE) + "_from_dung");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, brownBed)
                .requires(dung)
                .requires(Ingredient.of(Stream.of(Items.BLACK_BED, Items.BLUE_BED, Items.CYAN_BED, Items.GRAY_BED, Items.GREEN_BED, Items.LIGHT_BLUE_BED, Items.LIGHT_GRAY_BED, Items.LIME_BED, Items.MAGENTA_BED, Items.ORANGE_BED, Items.PINK_BED, Items.PURPLE_BED, Items.RED_BED, Items.YELLOW_BED, Items.WHITE_BED).map(ItemStack::new)))
                .group("bed")
                .unlockedBy(RecipeProvider.getHasName(dung), RecipeProvider.has(dung))
                .save(exporter, Id.of("dye_" + RecipeProvider.getItemName(brownBed) + "_from_dung"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, brownWool)
                .requires(dung)
                .requires(Ingredient.of(Stream.of(Items.BLACK_WOOL, Items.BLUE_WOOL, Items.CYAN_WOOL, Items.GRAY_WOOL, Items.GREEN_WOOL, Items.LIGHT_BLUE_WOOL, Items.LIGHT_GRAY_WOOL, Items.LIME_WOOL, Items.MAGENTA_WOOL, Items.ORANGE_WOOL, Items.PINK_WOOL, Items.PURPLE_WOOL, Items.RED_WOOL, Items.YELLOW_WOOL, Items.WHITE_WOOL).map(ItemStack::new)))
                .group("wool")
                .unlockedBy(RecipeProvider.getHasName(dung), RecipeProvider.has(dung))
                .save(exporter, Id.of("dye_" + RecipeProvider.getItemName(brownWool) + "_from_dung"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, brownCarpet)
                .requires(dung)
                .requires(Ingredient.of(Stream.of(Items.BLACK_CARPET, Items.BLUE_CARPET, Items.CYAN_CARPET, Items.GRAY_CARPET, Items.GREEN_CARPET, Items.LIGHT_BLUE_CARPET, Items.LIGHT_GRAY_CARPET, Items.LIME_CARPET, Items.MAGENTA_CARPET, Items.ORANGE_CARPET, Items.PINK_CARPET, Items.PURPLE_CARPET, Items.RED_CARPET, Items.YELLOW_CARPET, Items.WHITE_CARPET).map(ItemStack::new)))
                .group("carpet")
                .unlockedBy(RecipeProvider.getHasName(dung), RecipeProvider.has(dung))
                .save(exporter, Id.of("dye_" + RecipeProvider.getItemName(brownCarpet) + "_from_dung"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, brownTerracotta, 8)
                .define('#', Blocks.TERRACOTTA)
                .define('X', dung)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_terracotta")
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .unlockedBy(getHasName(dung), has(dung))
                .save(exporter, Id.of("dye_" + getItemName(brownTerracotta) + "_from_dung"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, brownConcretePowder, 8)
                .requires(dung)
                .requires(Blocks.SAND, 4)
                .requires(Blocks.GRAVEL, 4)
                .group("concrete_powder")
                .unlockedBy("has_sand", has(Blocks.SAND))
                .unlockedBy("has_gravel", has(Blocks.GRAVEL))
                .unlockedBy(getHasName(dung), has(dung))
                .save(exporter, Id.of("dye_" + getItemName(brownConcretePowder) + "_from_dung"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, brownStainedGlass, 8)
                .define('#', Blocks.GLASS)
                .define('X', dung)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_glass")
                .unlockedBy("has_glass", has(Blocks.GLASS))
                .unlockedBy(getHasName(dung), has(dung))
                .save(exporter, Id.of("dye_" + getItemName(brownStainedGlass) + "_from_dung"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, brownStainedGlassPane, 8)
                .define('#', Blocks.GLASS_PANE)
                .define('$', dung)
                .pattern("###")
                .pattern("#$#")
                .pattern("###")
                .group("stained_glass_pane")
                .unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE))
                .unlockedBy(getHasName(dung), has(dung))
                .save(exporter, Id.of("dye_" + getItemName(brownStainedGlassPane) + "_from_dung"));
    }

    private void generateVaseDyeingRecipes(RecipeOutput exporter) {
        List<Item> dyes = List.copyOf(DyeUtils.DYE_COLORS_ORDERED.stream().map(DyeItem::byColor).toList());
        List<Item> vases = DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).map(VaseBlock::asItem).toList();
        colorBlockWithDye(exporter, dyes, vases, "vases");
    }

    private void generateWoolSlabRecipes(RecipeOutput exporter) {
        List<Item> dyes = List.copyOf(DyeUtils.DYE_COLORS_ORDERED.stream().map(DyeItem::byColor).toList());
        List<Item> woolSlabs = DyeUtils.streamColorItemsSorted(BwtBlocks.woolSlabBlocks).map(SlabBlock::asItem).toList();
        colorBlockWithDye(exporter, dyes, woolSlabs, "wool_slabs");
        BwtBlocks.woolSlabBlocks.forEach((dyeColor, woolSlab) -> {
            Item woolBlockItem = DyeUtils.WOOL_COLORS.get(dyeColor).asItem();
            slabBuilder(RecipeCategory.BUILDING_BLOCKS, woolSlab, Ingredient.of(woolBlockItem)).unlockedBy(getHasName(woolBlockItem), has(woolBlockItem)).group("wool_slabs").save(exporter);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, woolBlockItem, 1).requires(woolSlab, 2).unlockedBy(getHasName(woolSlab), has(woolSlab)).group("wool").save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(woolSlab).getPath()));
        });
    }

    private void generateTier1Recipes(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.gearItem, 2)
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Items.STICK)
                .define('p', ItemTags.PLANKS)
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.handCrankBlock)
                .pattern("  s")
                .pattern(" s ")
                .pattern("cgc")
                .define('s', Items.STICK)
                .define('c', ItemTags.STONE_CRAFTING_MATERIALS)
                .define('g', BwtItems.gearItem)
                .unlockedBy(getHasName(BwtItems.gearItem), has(BwtItems.gearItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.millStoneBlock)
                .pattern("sss")
                .pattern("sss")
                .pattern("sgs")
                .define('s', Items.STONE)
                .define('g', BwtItems.gearItem)
                .unlockedBy(getHasName(BwtItems.gearItem), has(BwtItems.gearItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .pattern("ibi")
                .pattern("iwi")
                .pattern("iii")
                .define('i', Items.IRON_INGOT)
                .define('b', Items.BONE)
                .define('w', Items.WATER_BUCKET)
                .unlockedBy(getHasName(Items.BONE), has(Items.BONE))
                .save(exporter);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .requires(Blocks.CAULDRON)
                .requires(Items.BONE)
                .requires(Items.WATER_BUCKET)
                .unlockedBy(getHasName(Items.BONE), has(Items.BONE))
                .save(exporter, Id.of("cauldron_from_vanilla"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.lightBlockBlock)
                .pattern(" p ")
                .pattern("pfp")
                .pattern(" r ")
                .define('p', ConventionalItemTags.GLASS_PANES)
                .define('f', BwtItems.filamentItem)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(BwtItems.filamentItem), has(BwtItems.filamentItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.fabricItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("fff")
                .define('f', BwtItems.hempFiberItem)
                .unlockedBy(getHasName(BwtItems.hempFiberItem), has(BwtItems.hempFiberItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("ppp")
                .define('f', BwtItems.fabricItem)
                .define('p', ItemTags.PLANKS)
                .unlockedBy(getHasName(BwtItems.fabricItem), has(BwtItems.fabricItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.windmillItem)
                .pattern(" s ")
                .pattern("s s")
                .pattern(" s ")
                .define('s', BwtItems.sailItem)
                .unlockedBy(getHasName(BwtItems.sailItem), has(BwtItems.sailItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.gearBoxBlock)
                .pattern("pgp")
                .pattern("g g")
                .pattern("pgp")
                .define('p', ItemTags.PLANKS)
                .define('g', BwtItems.gearItem)
                .group("gear_box")
                .unlockedBy(getHasName(BwtItems.gearItem), has(BwtItems.gearItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.redstoneClutchBlock)
                .pattern("pgp")
                .pattern("grg")
                .pattern("pgp")
                .define('p', ItemTags.PLANKS)
                .define('g', BwtItems.gearItem)
                .define('r', Items.REDSTONE)
                .group("redstone_clutch")
                .unlockedBy(getHasName(BwtItems.gearItem), has(BwtItems.gearItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("fff")
                .pattern("fff")
                .define('f', BwtItems.hempFiberItem)
                .unlockedBy(getHasName(BwtItems.hempFiberItem), has(BwtItems.hempFiberItem))
                .group("rope")
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("ff")
                .pattern("ff")
                .pattern("ff")
                .define('f', BwtItems.hempFiberItem)
                .unlockedBy(getHasName(BwtItems.hempFiberItem), has(BwtItems.hempFiberItem))
                .group("rope")
                .save(exporter, Id.of("rope_vertical"));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.axleBlock)
                .pattern("prp")
                .define('p', ItemTags.PLANKS)
                .define('r', BwtItems.ropeItem)
                .unlockedBy(getHasName(BwtItems.ropeItem), has(BwtItems.ropeItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.anchorBlock)
                .pattern(" i ")
                .pattern("sss")
                .define('i', Items.IRON_INGOT)
                .define('s', Items.SMOOTH_STONE)
                .unlockedBy(getHasName(Items.SMOOTH_STONE), has(Items.SMOOTH_STONE))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
                .pattern("X")
                .pattern("S")
                .define('X', BwtItems.nethercoalItem)
                .define('S', Items.STICK)
                .unlockedBy("has_nether_coal", has(BwtItems.nethercoalItem))
                .save(exporter, Id.of("torch_from_nether_coal"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_TORCH, 4)
                .pattern("X")
                .pattern("#")
                .pattern("S")
                .define('X', BwtItems.nethercoalItem)
                .define('#', Items.STICK)
                .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .unlockedBy("has_nether_coal", has(BwtItems.nethercoalItem))
                .save(exporter, Id.of("soul_torch_from_nether_coal"));
    }

    private void generateTier2Recipes(RecipeOutput exporter) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BwtItems.strapItem, 8)
                .requires(BwtItems.tannedLeatherItem)
                .unlockedBy(getHasName(BwtItems.tannedLeatherItem), has(BwtItems.tannedLeatherItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.beltItem)
                .pattern(" s ")
                .pattern("s s")
                .pattern(" s ")
                .define('s', BwtItems.strapItem)
                .unlockedBy(getHasName(BwtItems.strapItem), has(BwtItems.strapItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("pgp")
                .define('i', Items.IRON_INGOT)
                .define('g', BwtItems.gearItem)
                .define('p', ItemTags.PLANKS)
                .define('b', BwtItems.beltItem)
                .unlockedBy(getHasName(BwtItems.beltItem), has(BwtItems.beltItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.grateBlock)
                .pattern("ss")
                .pattern("ss")
                .define('s', Items.STICK)
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.wickerPaneBlock)
                .pattern("ss")
                .pattern("ss")
                .define('s', Items.SUGAR_CANE)
                .unlockedBy(getHasName(Items.SUGAR_CANE), has(Items.SUGAR_CANE))
                .group("wicker")
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.slatsBlock)
                .pattern("mm")
                .pattern("mm")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy("has_wooden_moulding", has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.pulleyBlock)
                .pattern("pip")
                .pattern("grg")
                .pattern("pip")
                .define('p', ItemTags.PLANKS)
                .define('i', Items.IRON_INGOT)
                .define('g', BwtItems.gearItem)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(BwtItems.gearItem), has(BwtItems.gearItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.platformBlock)
                .pattern("pwp")
                .pattern(" p ")
                .pattern("pwp")
                .define('p', ItemTags.PLANKS)
                .define('w', BwtBlocks.wickerPaneBlock)
                .unlockedBy(getHasName(BwtBlocks.wickerPaneBlock), has(BwtBlocks.wickerPaneBlock))
                .save(exporter);
    }

    private void generateTier3Recipes(RecipeOutput exporter) {
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
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                    .requires(sidingBlock, 2)
                    .group("planks")
                    .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                    .save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(sidingBlock).getPath()));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .requires(mouldingBlock, 2)
                    .group("siding")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(mouldingBlock).getPath()));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .requires(cornerBlock, 2)
                    .group("moulding")
                    .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                    .save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(cornerBlock).getPath()));
            // Decorative blocks
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, columnBlock)
                    .pattern("#")
                    .pattern("#")
                    .pattern("#")
                    .define('#', mouldingBlock)
                    .group("column")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter);
            EmiDefaultsGenerator.addDefaultRecipe(columnBlock);

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pedestalBlock, 6)
                    .pattern(" s ")
                    .pattern("###")
                    .pattern("###")
                    .define('#', sidingBlock.fullBlock)
                    .define('s', sidingBlock)
                    .group("pedestal")
                    .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                    .save(exporter);
            EmiDefaultsGenerator.addDefaultRecipe(pedestalBlock);

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, tableBlock, 4)
                    .pattern("sss")
                    .pattern(" m ")
                    .pattern(" m ")
                    .define('s', sidingBlock)
                    .define('m', mouldingBlock)
                    .group("table")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter);
            EmiDefaultsGenerator.addDefaultRecipe(tableBlock);
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.hopperBlock)
                .pattern("s s")
                .pattern("gpg")
                .pattern(" c ")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.gearItem)
                .define('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .define('c', BwtItemTags.WOODEN_CORNER_BLOCKS)
                .unlockedBy("has_wooden_corner", has(BwtItemTags.WOODEN_CORNER_BLOCKS))
                .save(exporter, "mech_hopper");
        EmiDefaultsGenerator.addDefaultRecipe(BwtBlocks.hopperBlock);
    }

    private void generateTier4Recipes(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.hibachiBlock)
                .pattern("hhh")
                .pattern("sfs")
                .pattern("srs")
                .define('h', BwtItems.concentratedHellfireItem)
                .define('s', Items.STONE)
                .define('f', BwtItems.filamentItem)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(BwtItems.filamentItem), has(BwtItems.filamentItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.bellowsBlock)
                .pattern("sss")
                .pattern("lll")
                .pattern("gbg")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('l', BwtItems.tannedLeatherItem)
                .define('g', BwtItems.gearItem)
                .define('b', BwtItems.beltItem)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter);
    }

    private void generateTier5Recipes(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON)
                .pattern("G")
                .pattern("P")
                .define('G', BwtItems.glueItem)
                .define('P', Blocks.PISTON)
                .unlockedBy(getHasName(BwtItems.glueItem), has(BwtItems.glueItem))
                .save(exporter, "glued_sticky_piston");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.woodBladeItem)
                .pattern("s  ")
                .pattern("sgs")
                .pattern("s  ")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.glueItem)
                .unlockedBy(getHasName(BwtItems.glueItem), has(BwtItems.glueItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.waterWheelItem)
                .pattern("bbb")
                .pattern("b b")
                .pattern("bbb")
                .define('b', BwtItems.woodBladeItem)
                .unlockedBy(getHasName(BwtItems.woodBladeItem), has(BwtItems.woodBladeItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.turntableBlock)
                .pattern("www")
                .pattern("srs")
                .pattern("sgs")
                .define('w', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('s', Items.STONE)
                .define('r', Items.REDSTONE)
                .define('g', BwtItems.gearItem)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.dynamiteItem)
                .pattern("ph")
                .pattern("pt")
                .pattern("ps")
                .define('p', Items.PAPER)
                .define('h', BwtItems.hellfireDustItem)
                .define('t', BwtItems.tallowItem)
                .define('s', BwtItemTags.SAW_DUSTS)
                .unlockedBy("has_tallow", has(BwtItems.tallowItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.miningChargeBlock)
                .pattern("rgr")
                .pattern("ddd")
                .pattern("ddd")
                .define('r', BwtItems.ropeItem)
                .define('g', BwtItems.glueItem)
                .define('d', BwtItems.dynamiteItem)
                .unlockedBy("has_dynamite", has(BwtItems.dynamiteItem))
                .save(exporter, Id.of("mining_charge_with_glue"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.miningChargeBlock)
                .pattern("rsr")
                .pattern("ddd")
                .pattern("ddd")
                .define('r', BwtItems.ropeItem)
                .define('s', Items.SLIME_BALL)
                .define('d', BwtItems.dynamiteItem)
                .unlockedBy("has_dynamite", has(BwtItems.dynamiteItem))
                .save(exporter, Id.of("mining_charge_with_slime"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.screwPumpBlock)
                .pattern("gGg")
                .pattern("sSs")
                .pattern("sXs")
                .define('g', BwtItems.glueItem)
                .define('G', BwtBlocks.grateBlock)
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('S', BwtItems.screwItem)
                .define('X', BwtItems.gearItem)
                .unlockedBy(getHasName(BwtItems.screwItem), has(BwtItems.screwItem))
                .save(exporter);
    }

    private void generateTier6Recipes(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BwtBlocks.soilPlanterBlock)
                .pattern("d")
                .pattern("b")
                .pattern("p")
                .define('d', Items.DIRT)
                .define('b', Items.BONE_MEAL)
                .define('p', BwtBlocks.planterBlock)
                .unlockedBy(getHasName(BwtBlocks.planterBlock), has(BwtBlocks.planterBlock))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BwtBlocks.soulSandPlanterBlock)
                .pattern("s")
                .pattern("p")
                .define('s', Items.SOUL_SAND)
                .define('p', BwtBlocks.planterBlock)
                .unlockedBy(getHasName(BwtBlocks.planterBlock), has(BwtBlocks.planterBlock))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BwtBlocks.grassPlanterBlock)
                .pattern("g")
                .pattern("b")
                .pattern("p")
                .define('g', Items.GRASS_BLOCK)
                .define('b', Items.BONE_MEAL)
                .define('p', BwtBlocks.planterBlock)
                .unlockedBy(getHasName(BwtBlocks.planterBlock), has(BwtBlocks.planterBlock))
                .save(exporter);
    }

    private void generateTier7Recipes(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.haftItem)
                .pattern("s")
                .pattern("g")
                .pattern("m")
                .define('s', BwtItems.strapItem)
                .define('g', BwtItems.glueItem)
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy(getHasName(BwtItems.glueItem), has(BwtItems.glueItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.paddingItem)
                .pattern(" F ")
                .pattern("fff")
                .pattern(" F ")
                .define('F', BwtItems.fabricItem)
                .define('f', Items.FEATHER)
                .unlockedBy(getHasName(BwtItems.fabricItem), has(BwtItems.fabricItem))
                .group("padding")
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.broadheadArrowItem, 4)
                .pattern("b")
                .pattern("m")
                .pattern("f")
                .define('b', BwtItems.broadheadItem)
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('f', Items.FEATHER)
                .unlockedBy(getHasName(BwtItems.broadheadItem), has(BwtItems.broadheadItem))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.compositeBowItem)
                .pattern(" mb")
                .pattern("mbs")
                .pattern(" mb")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('b', Items.BONE)
                .define('s', Items.STRING)
                .unlockedBy("has_wooden_moulding", has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.soulForgeBlock)
                .pattern("nnn")
                .pattern(" n ")
                .pattern("nnn")
                .define('n', Items.NETHERITE_INGOT)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
    }

    private ResourceLocation highEfficiencyId(ItemLike itemConvertible) {
        return Id.of(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).withPrefix("he_").getPath());
    }

    private void generateHighEfficiencyRecipes(RecipeOutput exporter) {
        Optional<SidingBlock> stoneSiding = BwtBlocks.sidingBlocks.stream().filter(sidingBlock -> sidingBlock.fullBlock == Blocks.STONE).findAny();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("mmm")
                .define('f', BwtItems.fabricItem)
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy(getHasName(BwtItems.fabricItem), has(BwtItems.fabricItem))
                .save(exporter, highEfficiencyId(BwtItems.sailItem));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("sgs")
                .define('i', Items.IRON_INGOT)
                .define('g', BwtItems.gearItem)
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('b', BwtItems.beltItem)
                .unlockedBy(getHasName(BwtItems.beltItem), has(BwtItems.beltItem))
                .save(exporter, highEfficiencyId(BwtBlocks.sawBlock));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.gearBoxBlock)
                .pattern("sgs")
                .pattern("g g")
                .pattern("sgs")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.gearItem)
                .group("gear_box")
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(BwtBlocks.gearBoxBlock));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.redstoneClutchBlock)
                .pattern("sgs")
                .pattern("grg")
                .pattern("sgs")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.gearItem)
                .define('r', Items.REDSTONE)
                .group("redstone_clutch")
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(BwtBlocks.redstoneClutchBlock));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PISTON)
                .pattern("sss")
                .pattern("cic")
                .pattern("crc")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('c', Items.COBBLESTONE)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(Items.PISTON));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BOOKSHELF)
                .pattern("sss")
                .pattern("bbb")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('b', Items.BOOK)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(Items.BOOKSHELF));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.CHEST)
                .pattern("sss")
                .pattern("s s")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(Blocks.CHEST));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK)
                .pattern("sss")
                .pattern("srs")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(Blocks.NOTE_BLOCK));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX)
                .pattern("sss")
                .pattern("sds")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('d', Items.DIAMOND)
                .unlockedBy("has_wooden_siding", has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(exporter, highEfficiencyId(Blocks.JUKEBOX));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.LADDER, 3)
                .pattern("m m")
                .pattern("mmm")
                .pattern("m m")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy("has_wooden_moulding", has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(exporter, highEfficiencyId(Blocks.LADDER));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.STICK)
                .group("sticks")
                .pattern("m")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy("has_wooden_moulding", has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(exporter, highEfficiencyId(Items.STICK));
        stoneSiding.ifPresent(sidingBlock -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.REPEATER)
                .pattern("trt")
                .pattern("sss")
                .define('t', Items.REDSTONE_TORCH)
                .define('r', Items.REDSTONE)
                .define('s', sidingBlock)
                .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                .save(exporter, highEfficiencyId(Blocks.REPEATER)));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BOOK)
                .requires(BwtItems.tannedLeatherItem)
                .requires(Items.PAPER, 6)
                .unlockedBy(getHasName(BwtItems.tannedLeatherItem), has(BwtItems.tannedLeatherItem))
                .save(exporter, highEfficiencyId(Items.BOOK));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.ITEM_FRAME, 2)
                .pattern("mmm")
                .pattern("mtm")
                .pattern("mmm")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('t', BwtItems.tannedLeatherItem)
                .unlockedBy("has_wooden_moulding", has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(exporter, highEfficiencyId(Items.ITEM_FRAME));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.axleBlock)
                .pattern("prp")
                .define('p', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('r', BwtItems.ropeItem)
                .unlockedBy(getHasName(BwtItems.ropeItem), has(BwtItems.ropeItem))
                .save(exporter, highEfficiencyId(BwtBlocks.axleBlock));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.BOWL, 4)
                .define('c', BwtItemTags.WOODEN_CORNER_BLOCKS)
                .pattern("c c")
                .pattern(" c ")
                .unlockedBy("has_wooden_corner", has(BwtItemTags.WOODEN_CORNER_BLOCKS))
                .save(exporter);
        BlockFamilies.getAllFamilies()
                .filter(blockFamily -> blockFamily.getRecipeGroupPrefix().orElse("").equals("wooden"))
                .forEach(blockFamily -> createHighEfficiencyBlockFamilyRecipes(blockFamily, exporter));
    }

    private void createHighEfficiencyBlockFamilyRecipe(RecipeOutput exporter, BlockFamily blockFamily, BlockFamily.Variant variant, Function<Block, RecipeBuilder> builder) {
        Optional.ofNullable(blockFamily.get(variant))
                .ifPresent(result -> builder.apply(result)
                        .group(blockFamily.getRecipeGroupPrefix().map(group -> group + "_" + variant.getRecipeGroup()).orElse(null))
                        .save(exporter, highEfficiencyId(result))
                );
    }

    private void createHighEfficiencyBlockFamilyRecipes(BlockFamily blockFamily, RecipeOutput exporter) {
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
                door -> doorBuilder(door, Ingredient.of(sidingBlock))
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.TRAPDOOR,
                trapdoor -> trapdoorBuilder(trapdoor, Ingredient.of(sidingBlock))
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.PRESSURE_PLATE,
                pressurePlate -> ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, pressurePlate)
                        .requires(sidingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE,
                fence -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, fence, 3)
                        .pattern("sms")
                        .pattern("sms")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE_GATE,
                fenceGate -> ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, fenceGate)
                        .pattern("msm")
                        .pattern("msm")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.SIGN,
                sign -> ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, sign)
                        .pattern("s")
                        .pattern("m")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.STAIRS,
                stair -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stair)
                        .pattern("m ")
                        .pattern("mm")
                        .define('m', mouldingBlock)
                        .unlockedBy("has_moulding", has(mouldingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.BUTTON,
                button -> ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, button)
                        .requires(cornerBlock)
                        .unlockedBy(getHasName(cornerBlock), has(cornerBlock)));
    }

}

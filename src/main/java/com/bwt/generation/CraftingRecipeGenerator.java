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
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
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
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new RecipeProvider(registryLookup, output) {
            @Override
            public void buildRecipes() {
                generateCraftingRecipes(this, output);
                generateHighEfficiencyRecipes(this, output);
            }
        };
    }

    public void generateCraftingRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
        generateTier1Recipes(recipeProvider, output);
        generateTier2Recipes(recipeProvider, output);
        generateTier3Recipes(recipeProvider, output);
        generateTier4Recipes(recipeProvider, output);
        generateTier5Recipes(recipeProvider, output);
        generateTier6Recipes(recipeProvider, output);
        generateTier7Recipes(recipeProvider, output);

        generateVaseDyeingRecipes(recipeProvider);
        generateWoolSlabRecipes(recipeProvider, output);
        generateDungDyeingRecipes(recipeProvider, output);
        generateCompactingRecipes(recipeProvider, output);
        generateBloodWoodRecipes(recipeProvider, output);

        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.stoneDetectorRailBlock, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('p', Items.STONE_PRESSURE_PLATE)
                .define('r', Items.REDSTONE)
                .unlockedBy(RecipeProvider.getHasName(Items.STONE_PRESSURE_PLATE), recipeProvider.has(Items.STONE_PRESSURE_PLATE))
                .save(output);
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.obsidianDetectorRailBlock, 6)
                .pattern("i i")
                .pattern("ipi")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('p', BwtBlocks.obsidianPressurePlateBlock)
                .define('r', Items.REDSTONE)
                .unlockedBy(RecipeProvider.getHasName(BwtBlocks.obsidianPressurePlateBlock), recipeProvider.has(BwtBlocks.obsidianPressurePlateBlock))
                .save(output);
        recipeProvider.shaped(RecipeCategory.DECORATIONS, BwtBlocks.vineTrapBlock)
                .pattern("vvv")
                .define('v', Items.VINE)
                .unlockedBy(RecipeProvider.getHasName(Items.VINE), recipeProvider.has(Items.VINE))
                .save(output);
        offer2x1SlabRecipes(recipeProvider, output, RecipeCategory.BUILDING_BLOCKS, Blocks.DIRT, BwtBlocks.dirtSlabBlock, "dirt");
        offer2x1SlabRecipes(recipeProvider, output, RecipeCategory.BUILDING_BLOCKS, Blocks.GRASS_BLOCK, BwtBlocks.grassSlabBlock, "grass");
        offer2x1SlabRecipes(recipeProvider, output, RecipeCategory.BUILDING_BLOCKS, Blocks.MYCELIUM, BwtBlocks.myceliumSlabBlock, "mycelium");
        offer2x1SlabRecipes(recipeProvider, output, RecipeCategory.BUILDING_BLOCKS, Blocks.PODZOL, BwtBlocks.podzolSlabBlock, "podzol");
    }

    private void generateBloodWoodRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.generateRecipes(BwtBlocks.bloodWoodBlocks.blockFamily, FeatureFlagSet.of(FeatureFlags.VANILLA));
        recipeProvider.planksFromLog(BwtBlocks.bloodWoodBlocks.planksBlock, BwtItemTags.BLOOD_WOOD_LOGS, 4);
        recipeProvider.woodFromLogs(BwtBlocks.bloodWoodBlocks.woodBlock, BwtBlocks.bloodWoodBlocks.logBlock);
        recipeProvider.woodFromLogs(BwtBlocks.bloodWoodBlocks.strippedWoodBlock, BwtBlocks.bloodWoodBlocks.strippedLogBlock);
    }

    private void generateCompactingRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, BwtItems.soapItem, RecipeCategory.DECORATIONS, BwtBlocks.soapBlock, "soap_from_block", "soap");
        recipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, BwtItems.dungItem, RecipeCategory.DECORATIONS, BwtBlocks.dungBlock, "dung_from_block", "dung");
        recipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, BwtItems.concentratedHellfireItem, RecipeCategory.DECORATIONS, BwtBlocks.concentratedHellfireBlock, "concentrated_hellfire_from_block", "concentrated_hellfire");
        recipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, BwtItems.paddingItem, RecipeCategory.DECORATIONS, BwtBlocks.paddingBlock, "padding_from_block", "padding");
        recipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeCategory.MISC, BwtItems.ropeItem, RecipeCategory.DECORATIONS, BwtBlocks.ropeCoilBlock, "rope_from_block", "rope");
        offer2x2BlockSlabFamily(recipeProvider, output, BwtBlocks.wickerPaneBlock, BwtBlocks.wickerBlock, BwtBlocks.wickerSlabBlock, "wicker");
    }

    public static void offerCompacting2x2(RecipeProvider recipeProvider, RecipeOutput output, ItemLike inputItem, ItemLike outputBlock, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        recipeProvider
                .shaped(category, outputBlock)
                .define('#', inputItem)
                .pattern("##")
                .pattern("##")
                .unlockedBy(RecipeProvider.getHasName(inputItem), recipeProvider.has(inputItem))
                .group(group)
                .save(output, keyOf(recipeId != null ? recipeId : RecipeProvider.getSimpleRecipeName(outputBlock)));
    }

    public static void offerUncompacting2x2(RecipeProvider recipeProvider, RecipeOutput output, ItemLike inputBlock, ItemLike outputItem, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        recipeProvider
                .shapeless(category, outputItem, 4)
                .requires(inputBlock)
                .group(group)
                .unlockedBy(RecipeProvider.getHasName(inputBlock), recipeProvider.has(inputBlock))
                .save(output, keyOf(recipeId != null ? recipeId : RecipeProvider.getSimpleRecipeName(outputItem)));
    }

    public static void offer2x1SlabCreating(RecipeProvider recipeProvider, RecipeOutput output, ItemLike inputBlock, ItemLike outputSlab, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        recipeProvider
                .shaped(category, outputSlab, 4)
                .define('#', inputBlock)
                .pattern("##")
                .unlockedBy(RecipeProvider.getHasName(inputBlock), recipeProvider.has(inputBlock))
                .group(group)
                .save(output, keyOf(recipeId != null ? recipeId : RecipeProvider.getSimpleRecipeName(outputSlab)));
    }

    public static void offer2x1SlabRecombining(RecipeProvider recipeProvider, RecipeOutput output, ItemLike inputSlab, ItemLike outputBlock, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        recipeProvider
                .shaped(category, outputBlock)
                .define('#', inputSlab)
                .pattern("#")
                .pattern("#")
                .unlockedBy(RecipeProvider.getHasName(inputSlab), recipeProvider.has(inputSlab))
                .group(group)
                .save(output, keyOf(recipeId != null ? recipeId : RecipeProvider.getSimpleRecipeName(outputBlock)));
    }

    public static void offer2x1SlabUncompacting(RecipeProvider recipeProvider, RecipeOutput output, ItemLike inputSlab, ItemLike outputItem, RecipeCategory category, @Nullable String group, @Nullable String recipeId) {
        recipeProvider
                .shapeless(category, outputItem, 2)
                .requires(inputSlab)
                .group(group)
                .unlockedBy(RecipeProvider.getHasName(inputSlab), recipeProvider.has(inputSlab))
                .save(output, keyOf(recipeId != null ? recipeId : RecipeProvider.getSimpleRecipeName(outputItem)));
    }

    public static void offer2x1SlabRecipes(RecipeProvider recipeProvider, RecipeOutput output, RecipeCategory category, ItemLike block, ItemLike slab, String itemGroup) {
        offer2x1SlabCreating(recipeProvider, output, block, slab, category, itemGroup + "_slab", itemGroup + "_slab_from_block");
        offer2x1SlabRecombining(recipeProvider, output, slab, block, category, itemGroup + "_block", itemGroup + "_block_from_slab");
    }

    public static void offer2x2BlockSlabFamily(RecipeProvider recipeProvider, RecipeOutput output, ItemLike baseItem, ItemLike block, ItemLike slab, String itemGroup) {
        offerCompacting2x2(recipeProvider, output, baseItem, block, RecipeCategory.DECORATIONS, itemGroup + "_block", null);
        offerUncompacting2x2(recipeProvider, output, block, baseItem, RecipeCategory.MISC, itemGroup, itemGroup + "_from_block");
        offer2x1SlabRecipes(recipeProvider, output, RecipeCategory.DECORATIONS, block, slab, itemGroup);
        offer2x1SlabUncompacting(recipeProvider, output, slab, baseItem, RecipeCategory.MISC, itemGroup, itemGroup + "_from_slab");
    }

    private void generateDungDyeingRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
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

        recipeProvider.shapeless(RecipeCategory.DECORATIONS, brownVase)
                .requires(dung)
                .requires(Ingredient.of(
                        DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).filter(dyeable -> !dyeable.equals(brownVase))
                ))
                .group("vases")
                .unlockedBy("has_needed_dye", recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownVase) + "_from_dung"));
        recipeProvider.shapeless(RecipeCategory.DECORATIONS, brownCandle)
                .requires(Blocks.CANDLE)
                .requires(dung)
                .group("dyed_candle")
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, RecipeBuilder.getDefaultRecipeId(Blocks.BROWN_CANDLE) + "_from_dung");
        recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, brownBed)
                .requires(dung)
                .requires(Ingredient.of(Stream.of(Items.BLACK_BED, Items.BLUE_BED, Items.CYAN_BED, Items.GRAY_BED, Items.GREEN_BED, Items.LIGHT_BLUE_BED, Items.LIGHT_GRAY_BED, Items.LIME_BED, Items.MAGENTA_BED, Items.ORANGE_BED, Items.PINK_BED, Items.PURPLE_BED, Items.RED_BED, Items.YELLOW_BED, Items.WHITE_BED)))
                .group("bed")
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownBed) + "_from_dung"));
        recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, brownWool)
                .requires(dung)
                .requires(Ingredient.of(Stream.of(Items.BLACK_WOOL, Items.BLUE_WOOL, Items.CYAN_WOOL, Items.GRAY_WOOL, Items.GREEN_WOOL, Items.LIGHT_BLUE_WOOL, Items.LIGHT_GRAY_WOOL, Items.LIME_WOOL, Items.MAGENTA_WOOL, Items.ORANGE_WOOL, Items.PINK_WOOL, Items.PURPLE_WOOL, Items.RED_WOOL, Items.YELLOW_WOOL, Items.WHITE_WOOL)))
                .group("wool")
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownWool) + "_from_dung"));
        recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, brownCarpet)
                .requires(dung)
                .requires(Ingredient.of(Stream.of(Items.BLACK_CARPET, Items.BLUE_CARPET, Items.CYAN_CARPET, Items.GRAY_CARPET, Items.GREEN_CARPET, Items.LIGHT_BLUE_CARPET, Items.LIGHT_GRAY_CARPET, Items.LIME_CARPET, Items.MAGENTA_CARPET, Items.ORANGE_CARPET, Items.PINK_CARPET, Items.PURPLE_CARPET, Items.RED_CARPET, Items.YELLOW_CARPET, Items.WHITE_CARPET)))
                .group("carpet")
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownCarpet) + "_from_dung"));
        recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, brownTerracotta, 8)
                .define('#', Blocks.TERRACOTTA)
                .define('X', dung)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_terracotta")
                .unlockedBy("has_terracotta", recipeProvider.has(Blocks.TERRACOTTA))
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownTerracotta) + "_from_dung"));
        recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, brownConcretePowder, 8)
                .requires(dung)
                .requires(Blocks.SAND, 4)
                .requires(Blocks.GRAVEL, 4)
                .group("concrete_powder")
                .unlockedBy("has_sand", recipeProvider.has(Blocks.SAND))
                .unlockedBy("has_gravel", recipeProvider.has(Blocks.GRAVEL))
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownConcretePowder) + "_from_dung"));
        recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, brownStainedGlass, 8)
                .define('#', Blocks.GLASS)
                .define('X', dung)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_glass")
                .unlockedBy("has_glass", recipeProvider.has(Blocks.GLASS))
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownStainedGlass) + "_from_dung"));
        recipeProvider.shaped(RecipeCategory.DECORATIONS, brownStainedGlassPane, 8)
                .define('#', Blocks.GLASS_PANE)
                .define('$', dung)
                .pattern("###")
                .pattern("#$#")
                .pattern("###")
                .group("stained_glass_pane")
                .unlockedBy("has_glass_pane", recipeProvider.has(Blocks.GLASS_PANE))
                .unlockedBy(RecipeProvider.getHasName(dung), recipeProvider.has(dung))
                .save(output, keyOf("dye_" + RecipeProvider.getItemName(brownStainedGlassPane) + "_from_dung"));
    }

    private void generateVaseDyeingRecipes(RecipeProvider recipeProvider) {
        List<Item> dyes = List.copyOf(DyeUtils.DYE_COLORS_ORDERED.stream().map(DyeItem::byColor).toList());
        List<Item> vases = DyeUtils.streamColorItemsSorted(BwtBlocks.vaseBlocks).map(VaseBlock::asItem).toList();
        recipeProvider.colorBlockWithDye(dyes, vases, "vases");
    }

    private void generateWoolSlabRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
        List<Item> dyes = List.copyOf(DyeUtils.DYE_COLORS_ORDERED.stream().map(DyeItem::byColor).toList());
        List<Item> woolSlabs = DyeUtils.streamColorItemsSorted(BwtBlocks.woolSlabBlocks).map(SlabBlock::asItem).toList();
        recipeProvider.colorBlockWithDye(dyes, woolSlabs, "wool_slabs");
        BwtBlocks.woolSlabBlocks.forEach((dyeColor, woolSlab) -> {
            Item woolBlockItem = DyeUtils.WOOL_COLORS.get(dyeColor).asItem();
            recipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, woolSlab, Ingredient.of(woolBlockItem)).unlockedBy(RecipeProvider.getHasName(woolBlockItem), recipeProvider.has(woolBlockItem)).group("wool_slabs").save(output);
            recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, woolBlockItem, 1).requires(woolSlab, 2).unlockedBy(RecipeProvider.getHasName(woolSlab), recipeProvider.has(woolSlab)).group("wool").save(output, keyOf("recombine_" + BuiltInRegistries.BLOCK.getKey(woolSlab).getPath()));
        });
    }

    private void generateTier1Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.gearItem, 2)
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Items.STICK)
                .define('p', ItemTags.PLANKS)
                .unlockedBy(RecipeProvider.getHasName(Items.STICK), recipeProvider.has(Items.STICK))
                .unlockedBy("has_planks", recipeProvider.has(ItemTags.PLANKS))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.handCrankBlock)
                .pattern("  s")
                .pattern(" s ")
                .pattern("cgc")
                .define('s', Items.STICK)
                .define('c', ItemTags.STONE_CRAFTING_MATERIALS)
                .define('g', BwtItems.gearItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.gearItem), recipeProvider.has(BwtItems.gearItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.millStoneBlock)
                .pattern("sss")
                .pattern("sss")
                .pattern("sgs")
                .define('s', Items.STONE)
                .define('g', BwtItems.gearItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.gearItem), recipeProvider.has(BwtItems.gearItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .pattern("ibi")
                .pattern("iwi")
                .pattern("iii")
                .define('i', Items.IRON_INGOT)
                .define('b', Items.BONE)
                .define('w', Items.WATER_BUCKET)
                .unlockedBy(RecipeProvider.getHasName(Items.BONE), recipeProvider.has(Items.BONE))
                .save(output);
        recipeProvider.shapeless(RecipeCategory.MISC, BwtBlocks.cauldronBlock)
                .requires(Blocks.CAULDRON)
                .requires(Items.BONE)
                .requires(Items.WATER_BUCKET)
                .unlockedBy(RecipeProvider.getHasName(Items.BONE), recipeProvider.has(Items.BONE))
                .save(output, keyOf("cauldron_from_vanilla"));
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.lightBlockBlock)
                .pattern(" p ")
                .pattern("pfp")
                .pattern(" r ")
                .define('p', ConventionalItemTags.GLASS_PANES)
                .define('f', BwtItems.filamentItem)
                .define('r', Items.REDSTONE)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.filamentItem), recipeProvider.has(BwtItems.filamentItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.fabricItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("fff")
                .define('f', BwtItems.hempFiberItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.hempFiberItem), recipeProvider.has(BwtItems.hempFiberItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("ppp")
                .define('f', BwtItems.fabricItem)
                .define('p', ItemTags.PLANKS)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.fabricItem), recipeProvider.has(BwtItems.fabricItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.windmillItem)
                .pattern(" s ")
                .pattern("s s")
                .pattern(" s ")
                .define('s', BwtItems.sailItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.sailItem), recipeProvider.has(BwtItems.sailItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.gearBoxBlock)
                .pattern("pgp")
                .pattern("g g")
                .pattern("pgp")
                .define('p', ItemTags.PLANKS)
                .define('g', BwtItems.gearItem)
                .group("gear_box")
                .unlockedBy(RecipeProvider.getHasName(BwtItems.gearItem), recipeProvider.has(BwtItems.gearItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.redstoneClutchBlock)
                .pattern("pgp")
                .pattern("grg")
                .pattern("pgp")
                .define('p', ItemTags.PLANKS)
                .define('g', BwtItems.gearItem)
                .define('r', Items.REDSTONE)
                .group("redstone_clutch")
                .unlockedBy(RecipeProvider.getHasName(BwtItems.gearItem), recipeProvider.has(BwtItems.gearItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("fff")
                .pattern("fff")
                .define('f', BwtItems.hempFiberItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.hempFiberItem), recipeProvider.has(BwtItems.hempFiberItem))
                .group("rope")
                .save(output);
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtItems.ropeItem)
                .pattern("ff")
                .pattern("ff")
                .pattern("ff")
                .define('f', BwtItems.hempFiberItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.hempFiberItem), recipeProvider.has(BwtItems.hempFiberItem))
                .group("rope")
                .save(output, keyOf("rope_vertical"));
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.axleBlock)
                .pattern("prp")
                .define('p', ItemTags.PLANKS)
                .define('r', BwtItems.ropeItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.ropeItem), recipeProvider.has(BwtItems.ropeItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.anchorBlock)
                .pattern(" i ")
                .pattern("sss")
                .define('i', Items.IRON_INGOT)
                .define('s', Items.SMOOTH_STONE)
                .unlockedBy(RecipeProvider.getHasName(Items.SMOOTH_STONE), recipeProvider.has(Items.SMOOTH_STONE))
                .save(output);
        recipeProvider.shaped(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
                .pattern("X")
                .pattern("S")
                .define('X', BwtItems.nethercoalItem)
                .define('S', Items.STICK)
                .unlockedBy("has_nether_coal", recipeProvider.has(BwtItems.nethercoalItem))
                .save(output, keyOf("torch_from_nether_coal"));
        recipeProvider.shaped(RecipeCategory.DECORATIONS, Blocks.SOUL_TORCH, 4)
                .pattern("X")
                .pattern("#")
                .pattern("S")
                .define('X', BwtItems.nethercoalItem)
                .define('#', Items.STICK)
                .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
                .unlockedBy("has_nether_coal", recipeProvider.has(BwtItems.nethercoalItem))
                .save(output, keyOf("soul_torch_from_nether_coal"));
    }

    private void generateTier2Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.shapeless(RecipeCategory.MISC, BwtItems.strapItem, 8)
                .requires(BwtItems.tannedLeatherItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.tannedLeatherItem), recipeProvider.has(BwtItems.tannedLeatherItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.beltItem)
                .pattern(" s ")
                .pattern("s s")
                .pattern(" s ")
                .define('s', BwtItems.strapItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.strapItem), recipeProvider.has(BwtItems.strapItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("pgp")
                .define('i', Items.IRON_INGOT)
                .define('g', BwtItems.gearItem)
                .define('p', ItemTags.PLANKS)
                .define('b', BwtItems.beltItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.beltItem), recipeProvider.has(BwtItems.beltItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.grateBlock)
                .pattern("ss")
                .pattern("ss")
                .define('s', Items.STICK)
                .unlockedBy(RecipeProvider.getHasName(Items.STICK), recipeProvider.has(Items.STICK))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.wickerPaneBlock)
                .pattern("ss")
                .pattern("ss")
                .define('s', Items.SUGAR_CANE)
                .unlockedBy(RecipeProvider.getHasName(Items.SUGAR_CANE), recipeProvider.has(Items.SUGAR_CANE))
                .group("wicker")
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.slatsBlock)
                .pattern("mm")
                .pattern("mm")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy("has_wooden_moulding", recipeProvider.has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.pulleyBlock)
                .pattern("pip")
                .pattern("grg")
                .pattern("pip")
                .define('p', ItemTags.PLANKS)
                .define('i', Items.IRON_INGOT)
                .define('g', BwtItems.gearItem)
                .define('r', Items.REDSTONE)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.gearItem), recipeProvider.has(BwtItems.gearItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.platformBlock)
                .pattern("pwp")
                .pattern(" p ")
                .pattern("pwp")
                .define('p', ItemTags.PLANKS)
                .define('w', BwtBlocks.wickerPaneBlock)
                .unlockedBy(RecipeProvider.getHasName(BwtBlocks.wickerPaneBlock), recipeProvider.has(BwtBlocks.wickerPaneBlock))
                .save(output);
    }

    private void generateTier3Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
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
            recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                    .requires(sidingBlock, 2)
                    .group("planks")
                    .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                    .save(output, keyOf("recombine_" + BuiltInRegistries.BLOCK.getKey(sidingBlock).getPath()));
            recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .requires(mouldingBlock, 2)
                    .group("siding")
                    .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                    .save(output, keyOf("recombine_" + BuiltInRegistries.BLOCK.getKey(mouldingBlock).getPath()));
            recipeProvider.shapeless(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .requires(cornerBlock, 2)
                    .group("moulding")
                    .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                    .save(output, keyOf("recombine_" + BuiltInRegistries.BLOCK.getKey(cornerBlock).getPath()));
            // Decorative blocks
            recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, columnBlock, 3)
                    .pattern("#")
                    .pattern("#")
                    .pattern("#")
                    .define('#', mouldingBlock)
                    .group("column")
                    .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                    .save(output);

            recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, pedestalBlock, 6)
                    .pattern(" s ")
                    .pattern("###")
                    .pattern("###")
                    .define('#', sidingBlock.fullBlock)
                    .define('s', sidingBlock)
                    .group("pedestal")
                    .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                    .save(output);

            recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, tableBlock, 4)
                    .pattern("sss")
                    .pattern(" m ")
                    .pattern(" m ")
                    .define('s', sidingBlock)
                    .define('m', mouldingBlock)
                    .group("table")
                    .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                    .save(output);
        }

        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.hopperBlock)
                .pattern("s s")
                .pattern("gpg")
                .pattern(" c ")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.gearItem)
                .define('p', ItemTags.WOODEN_PRESSURE_PLATES)
                .define('c', BwtItemTags.WOODEN_CORNER_BLOCKS)
                .unlockedBy("has_wooden_corner", recipeProvider.has(BwtItemTags.WOODEN_CORNER_BLOCKS))
                .save(output, "mech_hopper");
    }

    private void generateTier4Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.hibachiBlock)
                .pattern("hhh")
                .pattern("sfs")
                .pattern("srs")
                .define('h', BwtItems.concentratedHellfireItem)
                .define('s', Items.STONE)
                .define('f', BwtItems.filamentItem)
                .define('r', Items.REDSTONE)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.filamentItem), recipeProvider.has(BwtItems.filamentItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.bellowsBlock)
                .pattern("sss")
                .pattern("lll")
                .pattern("gbg")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('l', BwtItems.tannedLeatherItem)
                .define('g', BwtItems.gearItem)
                .define('b', BwtItems.beltItem)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output);
    }

    private void generateTier5Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.shaped(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON)
                .pattern("G")
                .pattern("P")
                .define('G', BwtItems.glueItem)
                .define('P', Blocks.PISTON)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.glueItem), recipeProvider.has(BwtItems.glueItem))
                .save(output, "glued_sticky_piston");
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.woodBladeItem)
                .pattern("s  ")
                .pattern("sgs")
                .pattern("s  ")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.glueItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.glueItem), recipeProvider.has(BwtItems.glueItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.waterWheelItem)
                .pattern("bbb")
                .pattern("b b")
                .pattern("bbb")
                .define('b', BwtItems.woodBladeItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.woodBladeItem), recipeProvider.has(BwtItems.woodBladeItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.turntableBlock)
                .pattern("www")
                .pattern("srs")
                .pattern("sgs")
                .define('w', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('s', Items.STONE)
                .define('r', Items.REDSTONE)
                .define('g', BwtItems.gearItem)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.dynamiteItem)
                .pattern("ph")
                .pattern("pt")
                .pattern("ps")
                .define('p', Items.PAPER)
                .define('h', BwtItems.hellfireDustItem)
                .define('t', BwtItems.tallowItem)
                .define('s', BwtItemTags.SAW_DUSTS)
                .unlockedBy("has_tallow", recipeProvider.has(BwtItems.tallowItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.miningChargeBlock)
                .pattern("rgr")
                .pattern("ddd")
                .pattern("ddd")
                .define('r', BwtItems.ropeItem)
                .define('g', BwtItems.glueItem)
                .define('d', BwtItems.dynamiteItem)
                .unlockedBy("has_dynamite", recipeProvider.has(BwtItems.dynamiteItem))
                .save(output, keyOf("mining_charge_with_glue"));
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.miningChargeBlock)
                .pattern("rsr")
                .pattern("ddd")
                .pattern("ddd")
                .define('r', BwtItems.ropeItem)
                .define('s', Items.SLIME_BALL)
                .define('d', BwtItems.dynamiteItem)
                .unlockedBy("has_dynamite", recipeProvider.has(BwtItems.dynamiteItem))
                .save(output, keyOf("mining_charge_with_slime"));
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.screwPumpBlock)
                .pattern("gGg")
                .pattern("sSs")
                .pattern("sXs")
                .define('g', BwtItems.glueItem)
                .define('G', BwtBlocks.grateBlock)
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('S', BwtItems.screwItem)
                .define('X', BwtItems.gearItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.screwItem), recipeProvider.has(BwtItems.screwItem))
                .save(output);
    }

    private void generateTier6Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.shaped(RecipeCategory.DECORATIONS, BwtBlocks.soilPlanterBlock)
                .pattern("d")
                .pattern("b")
                .pattern("p")
                .define('d', Items.DIRT)
                .define('b', Items.BONE_MEAL)
                .define('p', BwtBlocks.planterBlock)
                .unlockedBy(RecipeProvider.getHasName(BwtBlocks.planterBlock), recipeProvider.has(BwtBlocks.planterBlock))
                .save(output);
        recipeProvider.shaped(RecipeCategory.DECORATIONS, BwtBlocks.soulSandPlanterBlock)
                .pattern("s")
                .pattern("p")
                .define('s', Items.SOUL_SAND)
                .define('p', BwtBlocks.planterBlock)
                .unlockedBy(RecipeProvider.getHasName(BwtBlocks.planterBlock), recipeProvider.has(BwtBlocks.planterBlock))
                .save(output);
        recipeProvider.shaped(RecipeCategory.DECORATIONS, BwtBlocks.grassPlanterBlock)
                .pattern("g")
                .pattern("b")
                .pattern("p")
                .define('g', Items.GRASS_BLOCK)
                .define('b', Items.BONE_MEAL)
                .define('p', BwtBlocks.planterBlock)
                .unlockedBy(RecipeProvider.getHasName(BwtBlocks.planterBlock), recipeProvider.has(BwtBlocks.planterBlock))
                .save(output);
    }

    private void generateTier7Recipes(RecipeProvider recipeProvider, RecipeOutput output) {
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.haftItem)
                .pattern("s")
                .pattern("g")
                .pattern("m")
                .define('s', BwtItems.strapItem)
                .define('g', BwtItems.glueItem)
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.glueItem), recipeProvider.has(BwtItems.glueItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.paddingItem)
                .pattern(" F ")
                .pattern("fff")
                .pattern(" F ")
                .define('F', BwtItems.fabricItem)
                .define('f', Items.FEATHER)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.fabricItem), recipeProvider.has(BwtItems.fabricItem))
                .group("padding")
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.broadheadArrowItem, 4)
                .pattern("b")
                .pattern("m")
                .pattern("f")
                .define('b', BwtItems.broadheadItem)
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('f', Items.FEATHER)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.broadheadItem), recipeProvider.has(BwtItems.broadheadItem))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.compositeBowItem)
                .pattern(" mb")
                .pattern("mbs")
                .pattern(" mb")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('b', Items.BONE)
                .define('s', Items.STRING)
                .unlockedBy("has_wooden_moulding", recipeProvider.has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(output);
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.soulForgeBlock)
                .pattern("nnn")
                .pattern(" n ")
                .pattern("nnn")
                .define('n', Items.NETHERITE_INGOT)
                .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                .save(output);
    }

    private ResourceKey<Recipe<?>> highEfficiencyId(ItemLike itemConvertible) {
        return keyOf(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).withPrefix("he_").getPath());
    }

    private void generateHighEfficiencyRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
        Optional<SidingBlock> stoneSiding = BwtBlocks.sidingBlocks.stream().filter(sidingBlock -> sidingBlock.fullBlock == Blocks.STONE).findAny();

        recipeProvider.shaped(RecipeCategory.MISC, BwtItems.sailItem)
                .pattern("fff")
                .pattern("fff")
                .pattern("mmm")
                .define('f', BwtItems.fabricItem)
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.fabricItem), recipeProvider.has(BwtItems.fabricItem))
                .save(output, highEfficiencyId(BwtItems.sailItem));
        recipeProvider.shaped(RecipeCategory.MISC, BwtBlocks.sawBlock)
                .pattern("iii")
                .pattern("gbg")
                .pattern("sgs")
                .define('i', Items.IRON_INGOT)
                .define('g', BwtItems.gearItem)
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('b', BwtItems.beltItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.beltItem), recipeProvider.has(BwtItems.beltItem))
                .save(output, highEfficiencyId(BwtBlocks.sawBlock));
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.gearBoxBlock)
                .pattern("sgs")
                .pattern("g g")
                .pattern("sgs")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.gearItem)
                .group("gear_box")
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(BwtBlocks.gearBoxBlock));
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.redstoneClutchBlock)
                .pattern("sgs")
                .pattern("grg")
                .pattern("sgs")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('g', BwtItems.gearItem)
                .define('r', Items.REDSTONE)
                .group("redstone_clutch")
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(BwtBlocks.redstoneClutchBlock));
        recipeProvider.shaped(RecipeCategory.REDSTONE, Blocks.PISTON)
                .pattern("sss")
                .pattern("cic")
                .pattern("crc")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('c', Items.COBBLESTONE)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(Items.PISTON));
        recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BOOKSHELF)
                .pattern("sss")
                .pattern("bbb")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('b', Items.BOOK)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(Items.BOOKSHELF));
        recipeProvider.shaped(RecipeCategory.MISC, Blocks.CHEST)
                .pattern("sss")
                .pattern("s s")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(Blocks.CHEST));
        recipeProvider.shaped(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK)
                .pattern("sss")
                .pattern("srs")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(Blocks.NOTE_BLOCK));
        recipeProvider.shaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX)
                .pattern("sss")
                .pattern("sds")
                .pattern("sss")
                .define('s', BwtItemTags.WOODEN_SIDING_BLOCKS)
                .define('d', Items.DIAMOND)
                .unlockedBy("has_wooden_siding", recipeProvider.has(BwtItemTags.WOODEN_SIDING_BLOCKS))
                .save(output, highEfficiencyId(Blocks.JUKEBOX));
        recipeProvider.shaped(RecipeCategory.MISC, Blocks.LADDER, 3)
                .pattern("m m")
                .pattern("mmm")
                .pattern("m m")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy("has_wooden_moulding", recipeProvider.has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(output, highEfficiencyId(Blocks.LADDER));
        recipeProvider.shaped(RecipeCategory.MISC, Items.STICK)
                .group("sticks")
                .pattern("m")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .unlockedBy("has_wooden_moulding", recipeProvider.has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(output, highEfficiencyId(Items.STICK));
        stoneSiding.ifPresent(sidingBlock -> recipeProvider.shaped(RecipeCategory.MISC, Blocks.REPEATER)
                .pattern("trt")
                .pattern("sss")
                .define('t', Items.REDSTONE_TORCH)
                .define('r', Items.REDSTONE)
                .define('s', sidingBlock)
                .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                .save(output, highEfficiencyId(Blocks.REPEATER)));
        recipeProvider.shapeless(RecipeCategory.MISC, Items.BOOK)
                .requires(BwtItems.tannedLeatherItem)
                .requires(Items.PAPER, 6)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.tannedLeatherItem), recipeProvider.has(BwtItems.tannedLeatherItem))
                .save(output, highEfficiencyId(Items.BOOK));
        recipeProvider.shaped(RecipeCategory.DECORATIONS, Items.ITEM_FRAME, 2)
                .pattern("mmm")
                .pattern("mtm")
                .pattern("mmm")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('t', BwtItems.tannedLeatherItem)
                .unlockedBy("has_wooden_moulding", recipeProvider.has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(output, highEfficiencyId(Items.ITEM_FRAME));
        recipeProvider.shaped(RecipeCategory.REDSTONE, BwtBlocks.axleBlock)
                .pattern("prp")
                .define('p', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('r', BwtItems.ropeItem)
                .unlockedBy(RecipeProvider.getHasName(BwtItems.ropeItem), recipeProvider.has(BwtItems.ropeItem))
                .save(output, highEfficiencyId(BwtBlocks.axleBlock));
        recipeProvider.shaped(RecipeCategory.MISC, Items.BOWL, 4)
                .define('c', BwtItemTags.WOODEN_CORNER_BLOCKS)
                .pattern("c c")
                .pattern(" c ")
                .unlockedBy("has_wooden_corner", recipeProvider.has(BwtItemTags.WOODEN_CORNER_BLOCKS))
                .save(output);
        BlockFamilies.getAllFamilies()
                .filter(blockFamily -> blockFamily.getRecipeGroupPrefix().orElse("").equals("wooden"))
                .forEach(blockFamily -> createHighEfficiencyBlockFamilyRecipes(recipeProvider, output, blockFamily));
    }

    private void createHighEfficiencyBlockFamilyRecipe(RecipeOutput output, BlockFamily blockFamily, BlockFamily.Variant variant, Function<Block, RecipeBuilder> builder) {
        // Do not listen to the IDE saying this should be Optional.of(). It can definitely return null
        Optional.ofNullable(blockFamily.get(variant))
                .ifPresent(result -> builder.apply(result)
                        .group(blockFamily.getRecipeGroupPrefix().map(group -> group + "_" + variant.getRecipeGroup()).orElse(null))
                        .save(output, highEfficiencyId(result))
                );
    }

    private void createHighEfficiencyBlockFamilyRecipes(RecipeProvider recipeProvider, RecipeOutput output, BlockFamily blockFamily) {
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

        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.DOOR,
                door -> recipeProvider.doorBuilder(door, Ingredient.of(sidingBlock))
                        .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.TRAPDOOR,
                trapdoor -> recipeProvider.trapdoorBuilder(trapdoor, Ingredient.of(sidingBlock))
                        .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.PRESSURE_PLATE,
                pressurePlate -> recipeProvider.shapeless(RecipeCategory.REDSTONE, pressurePlate)
                        .requires(sidingBlock)
                        .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.FENCE,
                fence -> recipeProvider.shaped(RecipeCategory.DECORATIONS, fence, 3)
                        .pattern("sms")
                        .pattern("sms")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.FENCE_GATE,
                fenceGate -> recipeProvider.shaped(RecipeCategory.REDSTONE, fenceGate)
                        .pattern("msm")
                        .pattern("msm")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.SIGN,
                sign -> recipeProvider.shaped(RecipeCategory.DECORATIONS, sign)
                        .pattern("s")
                        .pattern("m")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.STAIRS,
                stair -> recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, stair)
                        .pattern("m ")
                        .pattern("mm")
                        .define('m', mouldingBlock)
                        .unlockedBy("has_moulding", recipeProvider.has(mouldingBlock)));
        createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.BUTTON,
                button -> recipeProvider.shapeless(RecipeCategory.REDSTONE, button)
                        .requires(cornerBlock)
                        .unlockedBy(RecipeProvider.getHasName(cornerBlock), recipeProvider.has(cornerBlock)));
    }
    
    public static ResourceKey<Recipe<?>> keyOf(ResourceLocation resourceLocation) {
        return ResourceKey.create(Registries.RECIPE, resourceLocation);
    }

    public static ResourceKey<Recipe<?>> keyOf(String id) {
        return keyOf(Id.of(id));
    }

    @Override
    public String getName() {
        return "CraftingRecipeGenerator";
    }
}

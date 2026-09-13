package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapelessRecipe;
import com.bwt.tags.BwtItemTags;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SoulForgeRecipeGenerator extends FabricRecipeProvider {
    public SoulForgeRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    private ResourceLocation highEfficiencyId(ItemLike itemConvertible) {
        return Id.of(BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).withPrefix("he_").getPath());
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
                door -> SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, door, 3)
                        .define('#', Ingredient.of(sidingBlock))
                        .pattern("##")
                        .pattern("##")
                        .pattern("##")
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.TRAPDOOR,
                trapdoor -> SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, trapdoor, 2)
                        .define('#', Ingredient.of(sidingBlock))
                        .pattern("###")
                        .pattern("###")
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.PRESSURE_PLATE,
                pressurePlate -> SoulForgeShapelessRecipe.JsonBuilder.shapeless(RecipeCategory.REDSTONE, pressurePlate)
                        .requires(sidingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE,
                fence -> SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.DECORATIONS, fence, 3)
                        .pattern("sms")
                        .pattern("sms")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.FENCE_GATE,
                fenceGate -> SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, fenceGate)
                        .pattern("msm")
                        .pattern("msm")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.SIGN,
                sign -> SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.DECORATIONS, sign)
                        .pattern("s")
                        .pattern("m")
                        .define('s', sidingBlock)
                        .define('m', mouldingBlock)
                        .unlockedBy("has_siding", has(sidingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.STAIRS,
                stair -> SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stair)
                        .pattern("m ")
                        .pattern("mm")
                        .define('m', mouldingBlock)
                        .unlockedBy("has_moulding", has(mouldingBlock)));
        createHighEfficiencyBlockFamilyRecipe(exporter, blockFamily, BlockFamily.Variant.BUTTON,
                button -> SoulForgeShapelessRecipe.JsonBuilder.shapeless(RecipeCategory.REDSTONE, button)
                        .requires(cornerBlock)
                        .unlockedBy(getHasName(cornerBlock), has(cornerBlock)));
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        BlockFamilies.getAllFamilies()
                .filter(blockFamily -> !blockFamily.getRecipeGroupPrefix().orElse("").equals("wooden"))
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
            SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, sidingBlock, 8)
                    .markDefault()
                    .pattern("XXXX")
                    .define('X', sidingBlock.fullBlock)
                    .group("siding")
                    .unlockedBy(getHasName(sidingBlock.fullBlock), has(sidingBlock.fullBlock))
                    .save(exporter);
            SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, mouldingBlock, 8)
                    .markDefault()
                    .pattern("XXXX")
                    .define('X', sidingBlock)
                    .group("moulding")
                    .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                    .save(exporter);
            SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, cornerBlock, 8)
                    .markDefault()
                    .pattern("XXXX")
                    .define('X', mouldingBlock)
                    .group("corners")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter);

            // Stone Mini block recombining recipes
            SoulForgeShapelessRecipe.JsonBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                    .requires(sidingBlock, 2)
                    .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                    .save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(sidingBlock).getPath()));
            SoulForgeShapelessRecipe.JsonBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                    .requires(mouldingBlock, 2)
                    .group("siding")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(mouldingBlock).getPath()));
            SoulForgeShapelessRecipe.JsonBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                    .requires(cornerBlock, 2)
                    .group("moulding")
                    .unlockedBy(getHasName(cornerBlock), has(cornerBlock))
                    .save(exporter, Id.of("recombine_" + BuiltInRegistries.BLOCK.getKey(cornerBlock).getPath()));

            // Decorative blocks
            SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, columnBlock)
                    .markDefault()
                    .pattern("#")
                    .pattern("#")
                    .pattern("#")
                    .define('#', mouldingBlock)
                    .group("column")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter);
            SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pedestalBlock, 6)
                    .markDefault()
                    .pattern(" s ")
                    .pattern("###")
                    .pattern("###")
                    .define('#', sidingBlock.fullBlock)
                    .define('s', sidingBlock)
                    .group("pedestal")
                    .unlockedBy(getHasName(sidingBlock), has(sidingBlock))
                    .save(exporter);
            SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, tableBlock, 4)
                    .markDefault()
                    .pattern("sss")
                    .pattern(" m ")
                    .pattern(" m ")
                    .define('s', sidingBlock)
                    .define('m', mouldingBlock)
                    .group("table")
                    .unlockedBy(getHasName(mouldingBlock), has(mouldingBlock))
                    .save(exporter);
        }
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.DECORATIONS, BwtItems.canvasItem)
                .markDefault()
                .pattern("mmmm")
                .pattern("mffm")
                .pattern("mffm")
                .pattern("mmmm")
                .define('m', BwtItemTags.WOODEN_MOULDING_BLOCKS)
                .define('f', BwtItems.fabricItem)
                .unlockedBy("has_wooden_moulding", has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_BLOCK)
                .markDefault()
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .define('s', Items.NETHERITE_INGOT)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);

        // Netherite Tools
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, BwtItems.netheriteMattockItem)
                .markDefault()
                .pattern("sss ")
                .pattern(" h s")
                .pattern(" h  ")
                .pattern(" h  ")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, BwtItems.netheriteBattleAxeItem)
                .markDefault()
                .pattern("sss")
                .pattern("shs")
                .pattern(" h ")
                .pattern(" h ")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE)
                .markDefault()
                .pattern("sss")
                .pattern(" h ")
                .pattern(" h ")
                .pattern(" h ")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL)
                .markDefault()
                .pattern("s")
                .pattern("h")
                .pattern("h")
                .pattern("h")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_AXE)
                .markDefault()
                .pattern("ss")
                .pattern("sh")
                .pattern(" h")
                .pattern(" h")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_HOE)
                .markDefault()
                .pattern("ss")
                .pattern(" h")
                .pattern(" h")
                .pattern(" h")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_SWORD)
                .markDefault()
                .pattern("s")
                .pattern("s")
                .pattern("s")
                .pattern("h")
                .define('s', Items.NETHERITE_INGOT)
                .define('h', BwtItems.haftItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);

        // Netherite armor
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, BwtItems.armorPlateItem)
                .markDefault()
                .pattern("SnpS")
                .define('n', Items.NETHERITE_INGOT)
                .define('S', BwtItems.strapItem)
                .define('p', BwtItems.paddingItem)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_HELMET)
                .markDefault()
                .pattern("ssss")
                .pattern("s  s")
                .pattern("s  s")
                .pattern(" pp ")
                .define('s', Items.NETHERITE_INGOT)
                .define('p', BwtItems.armorPlateItem)
                .unlockedBy(getHasName(BwtItems.armorPlateItem), has(BwtItems.armorPlateItem))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_CHESTPLATE)
                .markDefault()
                .pattern("p  p")
                .pattern("ssss")
                .pattern("ssss")
                .pattern("ssss")
                .define('s', Items.NETHERITE_INGOT)
                .define('p', BwtItems.armorPlateItem)
                .unlockedBy(getHasName(BwtItems.armorPlateItem), has(BwtItems.armorPlateItem))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_LEGGINGS)
                .markDefault()
                .pattern("ssss")
                .pattern("pssp")
                .pattern("p  p")
                .pattern("p  p")
                .define('s', Items.NETHERITE_INGOT)
                .define('p', BwtItems.armorPlateItem)
                .unlockedBy(getHasName(BwtItems.armorPlateItem), has(BwtItems.armorPlateItem))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, Items.NETHERITE_BOOTS)
                .markDefault()
                .pattern(" ss ")
                .pattern(" ss ")
                .pattern("spps")
                .define('s', Items.NETHERITE_INGOT)
                .define('p', BwtItems.armorPlateItem)
                .unlockedBy(getHasName(BwtItems.armorPlateItem), has(BwtItems.armorPlateItem))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.obsidianPressurePlateBlock)
                .markDefault()
                .pattern("oooo")
                .define('o', Items.OBSIDIAN)
                .unlockedBy(getHasName(Items.OBSIDIAN), has(Items.OBSIDIAN))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, BwtItems.broadheadItem, 16)
                .markDefault()
                .pattern(" s ")
                .pattern("sss")
                .pattern(" s ")
                .pattern(" s ")
                .define('s', Items.NETHERITE_INGOT)
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.TOOLS, BwtItems.redstoneEyeItem)
                .markDefault()
                .pattern("lll")
                .pattern("ggg")
                .pattern(" r ")
                .define('l', Items.LAPIS_LAZULI)
                .define('g', Items.GOLD_NUGGET)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Items.LAPIS_LAZULI))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.detectorBlock)
                .markDefault()
                .pattern("cccc")
                .pattern("ette")
                .pattern("srrs")
                .pattern("srrs")
                .define('c', Items.COBBLESTONE)
                .define('e', BwtItems.redstoneEyeItem)
                .define('s', Items.STONE)
                .define('t', Items.REDSTONE_TORCH)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(BwtItems.redstoneEyeItem), has(BwtItems.redstoneEyeItem))
                .save(exporter);
        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.buddyBlock)
                .markDefault()
                .pattern("sses")
                .pattern("etts")
                .pattern("stte")
                .pattern("sess")
                .define('s', Items.STONE)
                .define('e', BwtItems.redstoneEyeItem)
                .define('t', Items.REDSTONE_TORCH)
                .unlockedBy(getHasName(BwtItems.redstoneEyeItem), has(BwtItems.redstoneEyeItem))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.blockDispenserBlock)
                .markDefault()
                .pattern("mmmm")
                .pattern("muum")
                .pattern("stts")
                .pattern("srrs")
                .define('m', Items.MOSSY_COBBLESTONE)
                .define('u', BwtItems.soulUrnItem)
                .define('s', Items.STONE)
                .define('t', Items.REDSTONE_TORCH)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(BwtItems.soulUrnItem), has(BwtItems.soulUrnItem))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.REDSTONE, BwtBlocks.lensBlock)
                .markDefault()
                .pattern("gddg")
                .pattern("g  g")
                .pattern("g  g")
                .pattern("gppg")
                .define('g', Items.GOLD_INGOT)
                .define('d', Items.DIAMOND)
                .define('p', Items.GLASS_PANE)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(exporter);

        SoulForgeShapedRecipe.JsonBuilder.shaped(RecipeCategory.MISC, BwtItems.screwItem)
                .markDefault()
                .pattern("ii  ")
                .pattern(" ii ")
                .pattern("ii  ")
                .pattern(" ii ")
                .define('i', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_NUGGET), has(Items.IRON_NUGGET))
                .save(exporter);
    }
}

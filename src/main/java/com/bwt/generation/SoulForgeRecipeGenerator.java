package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.items.BwtItems;
import com.bwt.recipes.soul_forge.SoulForgeRecipeBuilder;
import com.bwt.recipes.soul_forge.SoulForgeShapedRecipe;
import com.bwt.recipes.soul_forge.SoulForgeShapelessRecipe;
import com.bwt.tags.BwtItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SoulForgeRecipeGenerator extends FabricRecipeProvider {
    public SoulForgeRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        return new SharedRecipeProvider(provider, output) {
            @Override
            public void buildRecipes() {
                buildSoulForgeRecipes(this, output);
            }

            private void createHighEfficiencyBlockFamilyRecipe(RecipeOutput output, BlockFamily blockFamily, BlockFamily.Variant variant, Function<Block, SoulForgeRecipeBuilder> builder) {
                Optional.of(blockFamily.get(variant))
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
                        door -> sfShaped(RecipeCategory.REDSTONE, door, 3)
                                .define('#', Ingredient.of(sidingBlock))
                                .pattern("##")
                                .pattern("##")
                                .pattern("##")
                                .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.TRAPDOOR,
                        trapdoor -> sfShaped(RecipeCategory.REDSTONE, trapdoor, 2)
                                .define('#', Ingredient.of(sidingBlock))
                                .pattern("###")
                                .pattern("###")
                                .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.PRESSURE_PLATE,
                        pressurePlate -> sfShapeless(RecipeCategory.REDSTONE, pressurePlate)
                                .requires(sidingBlock)
                                .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.FENCE,
                        fence -> sfShaped(RecipeCategory.DECORATIONS, fence, 3)
                                .pattern("sms")
                                .pattern("sms")
                                .define('s', sidingBlock)
                                .define('m', mouldingBlock)
                                .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.FENCE_GATE,
                        fenceGate -> sfShaped(RecipeCategory.REDSTONE, fenceGate)
                                .pattern("msm")
                                .pattern("msm")
                                .define('s', sidingBlock)
                                .define('m', mouldingBlock)
                                .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.SIGN,
                        sign -> sfShaped(RecipeCategory.DECORATIONS, sign)
                                .pattern("s")
                                .pattern("m")
                                .define('s', sidingBlock)
                                .define('m', mouldingBlock)
                                .unlockedBy("has_siding", recipeProvider.has(sidingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.STAIRS,
                        stair -> sfShaped(RecipeCategory.BUILDING_BLOCKS, stair)
                                .pattern("m ")
                                .pattern("mm")
                                .define('m', mouldingBlock)
                                .unlockedBy("has_moulding", recipeProvider.has(mouldingBlock)));
                createHighEfficiencyBlockFamilyRecipe(output, blockFamily, BlockFamily.Variant.BUTTON,
                        button -> sfShapeless(RecipeCategory.REDSTONE, button)
                                .requires(cornerBlock)
                                .unlockedBy(RecipeProvider.getHasName(cornerBlock), recipeProvider.has(cornerBlock)));
            }

            public void buildSoulForgeRecipes(RecipeProvider recipeProvider, RecipeOutput output) {
                BlockFamilies.getAllFamilies()
                        .filter(blockFamily -> !blockFamily.getRecipeGroupPrefix().orElse("").equals("wooden"))
                        .forEach(blockFamily -> createHighEfficiencyBlockFamilyRecipes(recipeProvider, output, blockFamily));

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
                    sfShaped(RecipeCategory.BUILDING_BLOCKS, sidingBlock, 8)
                            .pattern("XXXX")
                            .define('X', sidingBlock.fullBlock)
                            .group("siding")
                            .unlockedBy(RecipeProvider.getHasName(sidingBlock.fullBlock), recipeProvider.has(sidingBlock.fullBlock))
                            .save(output);
                    sfShaped(RecipeCategory.BUILDING_BLOCKS, mouldingBlock, 8)
                            .pattern("XXXX")
                            .define('X', sidingBlock)
                            .group("moulding")
                            .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                            .save(output);
                    sfShaped(RecipeCategory.BUILDING_BLOCKS, cornerBlock, 8)
                            .pattern("XXXX")
                            .define('X', mouldingBlock)
                            .group("corners")
                            .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                            .save(output);

                    // Stone Mini block recombining recipes
                    sfShapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock.fullBlock)
                            .requires(sidingBlock, 2)
                            .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                            .save(output, "recombine_" + BuiltInRegistries.BLOCK.getKey(sidingBlock).getPath());
                    sfShapeless(RecipeCategory.BUILDING_BLOCKS, sidingBlock)
                            .requires(mouldingBlock, 2)
                            .group("siding")
                            .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                            .save(output, "recombine_" + BuiltInRegistries.BLOCK.getKey(mouldingBlock).getPath());
                    sfShapeless(RecipeCategory.BUILDING_BLOCKS, mouldingBlock)
                            .requires(cornerBlock, 2)
                            .group("moulding")
                            .unlockedBy(RecipeProvider.getHasName(cornerBlock), recipeProvider.has(cornerBlock))
                            .save(output, "recombine_" + BuiltInRegistries.BLOCK.getKey(cornerBlock).getPath());

                    // Decorative blocks
                    sfShaped(RecipeCategory.BUILDING_BLOCKS, columnBlock, 3)
                            .pattern("#")
                            .pattern("#")
                            .pattern("#")
                            .define('#', mouldingBlock)
                            .group("column")
                            .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                            .save(output);
                    sfShaped(RecipeCategory.BUILDING_BLOCKS, pedestalBlock, 6)
                            .pattern(" s ")
                            .pattern("###")
                            .pattern("###")
                            .define('#', sidingBlock.fullBlock)
                            .define('s', sidingBlock)
                            .group("pedestal")
                            .unlockedBy(RecipeProvider.getHasName(sidingBlock), recipeProvider.has(sidingBlock))
                            .save(output);
                    sfShaped(RecipeCategory.BUILDING_BLOCKS, tableBlock, 4)
                            .pattern("sss")
                            .pattern(" m ")
                            .pattern(" m ")
                            .define('s', sidingBlock)
                            .define('m', mouldingBlock)
                            .group("table")
                            .unlockedBy(RecipeProvider.getHasName(mouldingBlock), recipeProvider.has(mouldingBlock))
                            .save(output);
                }
                sfShaped(RecipeCategory.DECORATIONS, BwtItems.canvasItem)
                        .pattern("mmmm")
                        .pattern("mffm")
                        .pattern("mffm")
                        .pattern("mmmm")
                        .define('m', ingredientOf(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                        .define('f', BwtItems.fabricItem)
                        .unlockedBy("has_wooden_moulding", recipeProvider.has(BwtItemTags.WOODEN_MOULDING_BLOCKS))
                        .save(output);

                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_BLOCK)
                        .pattern("ssss")
                        .pattern("ssss")
                        .pattern("ssss")
                        .pattern("ssss")
                        .define('s', Items.NETHERITE_INGOT)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);

                // Netherite Tools
                sfShaped(RecipeCategory.TOOLS, BwtItems.netheriteMattockItem)
                        .pattern("sss ")
                        .pattern(" h s")
                        .pattern(" h  ")
                        .pattern(" h  ")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, BwtItems.netheriteBattleAxeItem)
                        .pattern("sss")
                        .pattern("shs")
                        .pattern(" h ")
                        .pattern(" h ")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE)
                        .pattern("sss")
                        .pattern(" h ")
                        .pattern(" h ")
                        .pattern(" h ")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL)
                        .pattern("s")
                        .pattern("h")
                        .pattern("h")
                        .pattern("h")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_AXE)
                        .pattern("ss")
                        .pattern("sh")
                        .pattern(" h")
                        .pattern(" h")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_HOE)
                        .pattern("ss")
                        .pattern(" h")
                        .pattern(" h")
                        .pattern(" h")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_SWORD)
                        .pattern("s")
                        .pattern("s")
                        .pattern("s")
                        .pattern("h")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('h', BwtItems.haftItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);

                // Netherite armor
                sfShaped(RecipeCategory.TOOLS, BwtItems.armorPlateItem)
                        .pattern("SnpS")
                        .define('n', Items.NETHERITE_INGOT)
                        .define('S', BwtItems.strapItem)
                        .define('p', BwtItems.paddingItem)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_HELMET)
                        .pattern("ssss")
                        .pattern("s  s")
                        .pattern("s  s")
                        .pattern(" pp ")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('p', BwtItems.armorPlateItem)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.armorPlateItem), recipeProvider.has(BwtItems.armorPlateItem))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_CHESTPLATE)
                        .pattern("p  p")
                        .pattern("ssss")
                        .pattern("ssss")
                        .pattern("ssss")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('p', BwtItems.armorPlateItem)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.armorPlateItem), recipeProvider.has(BwtItems.armorPlateItem))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_LEGGINGS)
                        .pattern("ssss")
                        .pattern("pssp")
                        .pattern("p  p")
                        .pattern("p  p")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('p', BwtItems.armorPlateItem)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.armorPlateItem), recipeProvider.has(BwtItems.armorPlateItem))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, Items.NETHERITE_BOOTS)
                        .pattern(" ss ")
                        .pattern(" ss ")
                        .pattern("spps")
                        .define('s', Items.NETHERITE_INGOT)
                        .define('p', BwtItems.armorPlateItem)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.armorPlateItem), recipeProvider.has(BwtItems.armorPlateItem))
                        .save(output);

                sfShaped(RecipeCategory.REDSTONE, BwtBlocks.obsidianPressurePlateBlock)
                        .pattern("oooo")
                        .define('o', Items.OBSIDIAN)
                        .unlockedBy(RecipeProvider.getHasName(Items.OBSIDIAN), recipeProvider.has(Items.OBSIDIAN))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, BwtItems.broadheadItem, 16)
                        .pattern(" s ")
                        .pattern("sss")
                        .pattern(" s ")
                        .pattern(" s ")
                        .define('s', Items.NETHERITE_INGOT)
                        .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT), recipeProvider.has(Items.NETHERITE_INGOT))
                        .save(output);
                sfShaped(RecipeCategory.TOOLS, BwtItems.redstoneEyeItem)
                        .pattern("lll")
                        .pattern("ggg")
                        .pattern(" r ")
                        .define('l', Items.LAPIS_LAZULI)
                        .define('g', Items.GOLD_NUGGET)
                        .define('r', Items.REDSTONE)
                        .unlockedBy(RecipeProvider.getHasName(Items.LAPIS_LAZULI), recipeProvider.has(Items.LAPIS_LAZULI))
                        .save(output);

                sfShaped(RecipeCategory.REDSTONE, BwtBlocks.detectorBlock)
                        .pattern("cccc")
                        .pattern("ette")
                        .pattern("srrs")
                        .pattern("srrs")
                        .define('c', Items.COBBLESTONE)
                        .define('e', BwtItems.redstoneEyeItem)
                        .define('s', Items.STONE)
                        .define('t', Items.REDSTONE_TORCH)
                        .define('r', Items.REDSTONE)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.redstoneEyeItem), recipeProvider.has(BwtItems.redstoneEyeItem))
                        .save(output);
                sfShaped(RecipeCategory.REDSTONE, BwtBlocks.buddyBlock)
                        .pattern("sses")
                        .pattern("etts")
                        .pattern("stte")
                        .pattern("sess")
                        .define('s', Items.STONE)
                        .define('e', BwtItems.redstoneEyeItem)
                        .define('t', Items.REDSTONE_TORCH)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.redstoneEyeItem), recipeProvider.has(BwtItems.redstoneEyeItem))
                        .save(output);

                sfShaped(RecipeCategory.REDSTONE, BwtBlocks.blockDispenserBlock)
                        .pattern("mmmm")
                        .pattern("muum")
                        .pattern("stts")
                        .pattern("srrs")
                        .define('m', Items.MOSSY_COBBLESTONE)
                        .define('u', BwtItems.soulUrnItem)
                        .define('s', Items.STONE)
                        .define('t', Items.REDSTONE_TORCH)
                        .define('r', Items.REDSTONE)
                        .unlockedBy(RecipeProvider.getHasName(BwtItems.soulUrnItem), recipeProvider.has(BwtItems.soulUrnItem))
                        .save(output);

                sfShaped(RecipeCategory.REDSTONE, BwtBlocks.lensBlock)
                        .pattern("gddg")
                        .pattern("g  g")
                        .pattern("g  g")
                        .pattern("gppg")
                        .define('g', Items.GOLD_INGOT)
                        .define('d', Items.DIAMOND)
                        .define('p', Items.GLASS_PANE)
                        .unlockedBy(RecipeProvider.getHasName(Items.DIAMOND), recipeProvider.has(Items.DIAMOND))
                        .save(output);

                sfShaped(RecipeCategory.MISC, BwtItems.screwItem)
                        .pattern("ii  ")
                        .pattern(" ii ")
                        .pattern("ii  ")
                        .pattern(" ii ")
                        .define('i', Items.IRON_INGOT)
                        .unlockedBy(RecipeProvider.getHasName(Items.IRON_NUGGET), recipeProvider.has(Items.IRON_NUGGET))
                        .save(output);
            }

            SoulForgeShapedRecipe.JsonBuilder sfShaped(RecipeCategory recipeCategory, ItemLike itemLike, int i) {
                return SoulForgeShapedRecipe.JsonBuilder.shaped(items, recipeCategory, itemLike, i);
            }

            SoulForgeShapedRecipe.JsonBuilder sfShaped(RecipeCategory recipeCategory, ItemLike itemLike) {
                return SoulForgeShapedRecipe.JsonBuilder.shaped(items, recipeCategory, itemLike, 1);
            }

            SoulForgeShapelessRecipe.JsonBuilder sfShapeless(RecipeCategory recipeCategory, ItemLike itemLike, int i) {
                return SoulForgeShapelessRecipe.JsonBuilder.shapeless(items, recipeCategory, itemLike, i);
            }

            SoulForgeShapelessRecipe.JsonBuilder sfShapeless(RecipeCategory recipeCategory, ItemLike itemLike) {
                return SoulForgeShapelessRecipe.JsonBuilder.shapeless(items, recipeCategory, itemLike, 1);
            }

            private String highEfficiencyId(ItemLike itemConvertible) {
                return BuiltInRegistries.ITEM.getKey(itemConvertible.asItem()).withPrefix("he_").getPath();
            }
        };
    }


    @Override
    public String getName() {
        return "SoulForgeRecipeGenerator";
    }
}

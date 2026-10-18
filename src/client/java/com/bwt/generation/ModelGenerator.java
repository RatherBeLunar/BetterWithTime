package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.dirt_slab.DirtSlabBlock;
import com.bwt.blocks.lens.LensBeamBlock;
import com.bwt.blocks.turntable.TurntableBlock;
import com.bwt.blocks.unfired_pottery.UnfiredPotteryBlock;
import com.bwt.items.BwtItems;
import com.bwt.rendering.unfired_decorated_pot.UnfiredDecoratedPotSpecialRenderer;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput generator) {
        super(generator);
    }
    public static final PropertyDispatch<VariantMutator> NORTH_DEFAULT_HORIZONTAL_ROTATION = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
            .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
            .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
            .select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
            .select(Direction.NORTH, BlockModelGenerators.NOP);
    public static final PropertyDispatch<VariantMutator> NORTH_DEFAULT_ROTATION = PropertyDispatch.modify(BlockStateProperties.FACING)
            .select(Direction.DOWN, BlockModelGenerators.X_ROT_90)
            .select(Direction.UP, BlockModelGenerators.X_ROT_270)
            .select(Direction.NORTH, BlockModelGenerators.NOP)
            .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
            .select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
            .select(Direction.EAST, BlockModelGenerators.Y_ROT_90);


    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        generateDirtAndGrassSlab(blockStateModelGenerator);
        generateCompanionBlocks(blockStateModelGenerator);
        generateBloodWoodBlocks(blockStateModelGenerator);
        generateStokedFireBlock(blockStateModelGenerator);
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.sawBlock, ModelLocationUtils.getModelLocation(BwtBlocks.sawBlock));
        BwtBlocks.sidingBlocks.forEach(sidingBlock -> generateSidingBlock(blockStateModelGenerator, sidingBlock));
        BwtBlocks.mouldingBlocks.forEach(mouldingBlock -> generateMouldingBlock(blockStateModelGenerator, mouldingBlock));
        BwtBlocks.cornerBlocks.forEach(cornerBlock -> generateCornerBlock(blockStateModelGenerator, cornerBlock));
        BwtBlocks.columnBlocks.forEach(columnBlock -> generateColumnBlock(blockStateModelGenerator, columnBlock));
        BwtBlocks.pedestalBlocks.forEach(pedestalBlock -> generatePedestalBlock(blockStateModelGenerator, pedestalBlock));
        BwtBlocks.tableBlocks.forEach(tableBlock -> generateTableBlock(blockStateModelGenerator, tableBlock));
        BwtBlocks.vaseBlocks.values().forEach(vaseBlock -> generateVaseBlock(blockStateModelGenerator, vaseBlock));
        BwtBlocks.woolSlabBlocks.forEach((dyeColor, woolSlab) -> generateWoolSlab(blockStateModelGenerator, dyeColor, woolSlab));
        blockStateModelGenerator.createActiveRail(BwtBlocks.stoneDetectorRailBlock);
        blockStateModelGenerator.createActiveRail(BwtBlocks.obsidianDetectorRailBlock);
        blockStateModelGenerator.registerSimpleFlatItemModel(BwtBlocks.slatsBlock);
        blockStateModelGenerator.registerSimpleFlatItemModel(BwtBlocks.grateBlock);
        blockStateModelGenerator.registerSimpleFlatItemModel(BwtBlocks.wickerPaneBlock);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.cauldronBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.cauldronBlock))
                ).with(PropertyDispatch.modify(AbstractCookingPotBlock.TIP_DIRECTION)
                        .select(Direction.UP, BlockModelGenerators.NOP)
                        .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
                        .select(Direction.SOUTH, BlockModelGenerators.X_ROT_270)
                        .select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.EAST, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_270))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.crucibleBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.crucibleBlock))
                ).with(PropertyDispatch.modify(AbstractCookingPotBlock.TIP_DIRECTION)
                        .select(Direction.UP, BlockModelGenerators.NOP)
                        .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
                        .select(Direction.SOUTH, BlockModelGenerators.X_ROT_270)
                        .select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.EAST, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_270))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.pulleyBlock,
                        plainVariant(
                                ModelTemplates.CUBE_BOTTOM_TOP.create(
                                        BwtBlocks.pulleyBlock,
                                        TexturedModel.CUBE_TOP_BOTTOM.get(BwtBlocks.pulleyBlock).getMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(BwtBlocks.pulleyBlock, "_side")),
                                        blockStateModelGenerator.modelOutput
                                )
                        )
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(BwtBlocks.turntableBlock)
                .with(PropertyDispatch.initial(TurntableBlock.TICK_SETTING)
                        .select(0, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_0")))
                        .select(1, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_1")))
                        .select(2, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_2")))
                        .select(3, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_3")))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.platformBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.platformBlock))
                )
        );
        ResourceLocation bellowsId = ModelLocationUtils.getModelLocation(BwtBlocks.bellowsBlock);
        ResourceLocation bellowsCompressedId = bellowsId.withSuffix("_compressed");
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(BwtBlocks.bellowsBlock)
                .with(PropertyDispatch.initial(BellowsBlock.MECH_POWERED)
                        .select(true, plainVariant(bellowsCompressedId))
                        .select(false, plainVariant(bellowsId))
                )
                .with(NORTH_DEFAULT_HORIZONTAL_ROTATION)
        );
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.soulForgeBlock)
                .with(NORTH_DEFAULT_HORIZONTAL_ROTATION)
        );
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.screwPumpBlock)
                .with(NORTH_DEFAULT_HORIZONTAL_ROTATION)
        );

        blockStateModelGenerator.generateSimpleSpecialItemModel(BwtBlocks.unfiredDecoratedPotBlockWithSherds, new UnfiredDecoratedPotSpecialRenderer.Unbaked());
        for (UnfiredPotteryBlock unfiredPotteryBlock : new UnfiredPotteryBlock[]{BwtBlocks.unfiredDecoratedPotBlock, BwtBlocks.unfiredDecoratedPotBlockWithSherds, BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredFlowerPotBlock}) {
            blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(unfiredPotteryBlock)
                    .with(PropertyDispatch.initial(UnfiredPotteryBlock.COOKING)
                            .select(false, plainVariant(ModelLocationUtils.getModelLocation(unfiredPotteryBlock)))
                            .select(true, plainVariant(ModelLocationUtils.getModelLocation(unfiredPotteryBlock).withSuffix("_cooking")))
                    )
            );
        }
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(BwtBlocks.urnBlock)
                .with(PropertyDispatch.initial(UrnBlock.CONNECTED_UP)
                        .select(false, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.urnBlock)))
                        .select(true, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.urnBlock).withSuffix("_connected_up")))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.planterBlock));
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.soilPlanterBlock));
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.soulSandPlanterBlock));
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.grassPlanterBlock));
        blockStateModelGenerator.registerSimpleTintedItemModel(
                BwtBlocks.grassPlanterBlock,
                ModelLocationUtils.getModelLocation(BwtBlocks.grassPlanterBlock),
                new GrassColorSource()
        );
        ResourceLocation buddyBlockModelId = TexturedModel.createDefault(block -> TextureMapping.orientableCubeOnlyTop(block).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side")), ModelTemplates.CUBE_ORIENTABLE)
                .create(BwtBlocks.buddyBlock, blockStateModelGenerator.modelOutput);
        ResourceLocation buddyBlockPoweredModelId = TexturedModel.createDefault(block -> new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side_powered")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front_powered")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side_powered")), ModelTemplates.CUBE_ORIENTABLE)
                .createWithSuffix(BwtBlocks.buddyBlock, "_powered", blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.buddyBlock)
                        .with(createBooleanModelDispatch(BuddyBlock.POWERED, buddyBlockPoweredModelId, buddyBlockModelId))
                        .with(NORTH_DEFAULT_ROTATION)
        );
        TexturedModel.createDefault(block -> TextureMapping.orientableCubeOnlyTop(block).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side")), ModelTemplates.CUBE_ORIENTABLE)
                .create(BwtBlocks.soapBlock, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.soapBlock, ModelLocationUtils.getModelLocation(BwtBlocks.soapBlock));
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.ropeCoilBlock, TexturedModel.COLUMN);
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.paddingBlock, TexturedModel.CUBE);
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.wickerBlock, TexturedModel.CUBE);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(
                BwtBlocks.dungBlock,
                BlockModelGenerators.createRotatedVariants(
                        plainModel(
                                TexturedModel.GLAZED_TERRACOTTA.create(
                                        BwtBlocks.dungBlock,
                                        blockStateModelGenerator.modelOutput
                                )
                        )
                )
        ));
        blockStateModelGenerator.blockStateOutput.accept(createSlab(
                BwtBlocks.wickerSlabBlock,
                plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.wickerSlabBlock)),
                plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.wickerSlabBlock, "_top")),
                plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.wickerBlock))
        ));
        generateMiningChargeBlock(blockStateModelGenerator);
        TexturedModel.createDefault(TextureMapping::commandBlock, ModelTemplates.COMMAND_BLOCK)
                .create(BwtBlocks.lensBlock, blockStateModelGenerator.modelOutput);
        generateDebugLensBeam(blockStateModelGenerator);
        blockStateModelGenerator.blockStateOutput.accept(createSimpleBlock(BwtBlocks.lensBeamGlassBlock, ModelLocationUtils.getModelLocation(Blocks.GLASS)));
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.aqueductBlock,
                        plainVariant(
                                ModelTemplates.CUBE_BOTTOM_TOP.create(
                                        BwtBlocks.aqueductBlock,
                                        TexturedModel.CUBE_TOP_BOTTOM.get(BwtBlocks.aqueductBlock).getMapping(),
                                        blockStateModelGenerator.modelOutput
                                )
                        )
                )
        );

        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.aqueductBlock, ModelLocationUtils.getModelLocation(BwtBlocks.aqueductBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.anchorBlock, ModelLocationUtils.getModelLocation(BwtBlocks.anchorBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.axleBlock, ModelLocationUtils.getModelLocation(BwtBlocks.axleBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.creativePowerSouceBlock, ModelLocationUtils.getModelLocation(BwtBlocks.creativePowerSouceBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.blockDispenserBlock, ModelLocationUtils.getModelLocation(BwtBlocks.blockDispenserBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.cauldronBlock, ModelLocationUtils.getModelLocation(BwtBlocks.cauldronBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.concentratedHellfireBlock, ModelLocationUtils.getModelLocation(BwtBlocks.concentratedHellfireBlock, "_0"));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.crucibleBlock, ModelLocationUtils.getModelLocation(BwtBlocks.crucibleBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.detectorBlock, ModelLocationUtils.getModelLocation(BwtBlocks.detectorBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.gearBoxBlock, ModelLocationUtils.getModelLocation(BwtBlocks.gearBoxBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.screwPumpBlock, ModelLocationUtils.getModelLocation(BwtBlocks.screwPumpBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.redstoneClutchBlock, ModelLocationUtils.getModelLocation(BwtBlocks.redstoneClutchBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.handCrankBlock, ModelLocationUtils.getModelLocation(BwtBlocks.handCrankBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.hibachiBlock, ModelLocationUtils.getModelLocation(BwtBlocks.hibachiBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.hopperBlock, ModelLocationUtils.getModelLocation(BwtBlocks.hopperBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.lensBlock, ModelLocationUtils.getModelLocation(BwtBlocks.lensBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.lightBlockBlock, ModelLocationUtils.getModelLocation(BwtBlocks.lightBlockBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.millStoneBlock, ModelLocationUtils.getModelLocation(BwtBlocks.millStoneBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.obsidianPressurePlateBlock, ModelLocationUtils.getModelLocation(BwtBlocks.obsidianPressurePlateBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.pulleyBlock, ModelLocationUtils.getModelLocation(BwtBlocks.pulleyBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.turntableBlock, ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_0"));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.bellowsBlock, ModelLocationUtils.getModelLocation(BwtBlocks.bellowsBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.unfiredDecoratedPotBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredDecoratedPotBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.unfiredCrucibleBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredCrucibleBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.unfiredPlanterBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredPlanterBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.unfiredVaseBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredVaseBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.unfiredUrnBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredUrnBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.unfiredFlowerPotBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredFlowerPotBlock));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.vineTrapBlock, ModelLocationUtils.getModelLocation(BwtBlocks.vineTrapBlock));
        blockStateModelGenerator.registerSimpleFlatItemModel(BwtBlocks.urnBlock.asItem());
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(BwtItems.armorPlateItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.beltItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.breedingHarnessItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.broadheadItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.broadheadArrowItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.canvasItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.coalDustItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateBow(BwtItems.compositeBowItem);
        itemModelGenerator.createFlatItemModel(BwtItems.compositeBowItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.concentratedHellfireItem, ModelTemplates.FLAT_ITEM);
//        itemModelGenerator.itemModelOutput.accept(
//                BwtBlocks.concentratedHellfireBlock.asItem(),
//                ItemModelUtils.plainModel(
//                        ModelTemplates.FLAT_ITEM.create(
//                                ModelLocationUtils.getModelLocation(BwtBlocks.concentratedHellfireBlock.asItem()),
//                                TextureMapping.layer0(TextureMapping.getBlockTexture(BwtBlocks.concentratedHellfireBlock).withSuffix("_0")),
//                                itemModelGenerator.modelOutput
//                        )
//                )
//        );
        itemModelGenerator.generateFlatItem(BwtItems.cementBucketItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.cookedWolfChopItem, Items.COOKED_PORKCHOP, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.donutItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.dungItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.dynamiteItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.fabricItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.filamentItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.flourItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.foulFoodItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.friedEggItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.gearItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.groundNetherrackItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.glueItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.haftItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.hempItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.hempFiberItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.hempSeedsItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.hellfireDustItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.kibbleItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.nethercoalItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtBlocks.netherGroth.asItem(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.paddingItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.poachedEggItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.potashItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.rawEggItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.redstoneEyeItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.netheriteMattockItem, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.netheriteBattleAxeItem, ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.ropeItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.rottedArrowItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.sawDustItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.sailItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.scouredLeatherItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.screwItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.soapItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.soulDustItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.soulUrnItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.strapItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.tallowItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.tannedLeatherItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.waterWheelItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.windmillItem, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.wolfChopItem, Items.PORKCHOP, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(BwtItems.woodBladeItem, ModelTemplates.FLAT_ITEM);
    }

    private void generateBloodWoodBlocks(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.woodProvider(BwtBlocks.bloodWoodBlocks.logBlock).logWithHorizontal(BwtBlocks.bloodWoodBlocks.logBlock).wood(BwtBlocks.bloodWoodBlocks.woodBlock);
        blockStateModelGenerator.woodProvider(BwtBlocks.bloodWoodBlocks.strippedLogBlock).logWithHorizontal(BwtBlocks.bloodWoodBlocks.strippedLogBlock).wood(BwtBlocks.bloodWoodBlocks.strippedWoodBlock);
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.bloodWoodBlocks.leavesBlock, TexturedModel.LEAVES);
        blockStateModelGenerator.createPlantWithDefaultItem(BwtBlocks.bloodWoodBlocks.saplingBlock, BwtBlocks.bloodWoodBlocks.pottedSaplingBlock, BlockModelGenerators.PlantType.NOT_TINTED);
        blockStateModelGenerator.family(BwtBlocks.bloodWoodBlocks.blockFamily.getBaseBlock()).generateFor(BwtBlocks.bloodWoodBlocks.blockFamily);
    }

    private void generateCompanionBlocks(BlockModelGenerators blockStateModelGenerator) {
        ResourceLocation companionCubeModelId = TexturedModel.ORIENTABLE_ONLY_TOP.create(BwtBlocks.companionCubeBlock, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.companionCubeBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.companionCubeBlock))
                ).with(NORTH_DEFAULT_ROTATION)
        );
        ResourceLocation companionSlabBottom = TexturedModel.createDefault(
                block -> new TextureMapping()
                        .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(BwtBlocks.companionCubeBlock, "_top"))
                        .put(TextureSlot.TOP, TextureMapping.getBlockTexture(BwtBlocks.companionSlabBlock, "_top"))
                        .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BwtBlocks.companionCubeBlock, "_side"))
                , ModelTemplates.SLAB_BOTTOM
        ).create(BwtBlocks.companionSlabBlock, blockStateModelGenerator.modelOutput);
        ResourceLocation companionSlabTop = TexturedModel.createDefault(
                block -> new TextureMapping()
                        .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(BwtBlocks.companionSlabBlock, "_top"))
                        .put(TextureSlot.TOP, TextureMapping.getBlockTexture(BwtBlocks.companionCubeBlock, "_top"))
                        .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BwtBlocks.companionCubeBlock, "_side"))
                , ModelTemplates.SLAB_TOP
        ).create(BwtBlocks.companionSlabBlock, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(createSlab(BwtBlocks.companionSlabBlock, companionSlabBottom, companionSlabTop, companionCubeModelId));
        blockStateModelGenerator.registerSimpleItemModel(BwtBlocks.companionSlabBlock, companionSlabBottom);
    }

    private void generateStokedFireBlock(BlockModelGenerators blockStateModelGenerator) {
        ModelTemplate tallFireFloorTemplate = new ModelTemplate(Optional.of(Id.of("block/template_tall_fire_floor")), Optional.empty(), TextureSlot.FIRE);
        ModelTemplate shortFireFloorTemplate = new ModelTemplate(Optional.of(Id.of("block/template_short_fire_floor")), Optional.empty(), TextureSlot.FIRE);
        ModelTemplate tallFireSideTemplate = new ModelTemplate(Optional.of(Id.of("block/template_tall_fire_side")), Optional.empty(), TextureSlot.FIRE);
        ModelTemplate shortFireSideTemplate = new ModelTemplate(Optional.of(Id.of("block/template_short_fire_side")), Optional.empty(), TextureSlot.FIRE);
        ModelTemplate tallFireSideAltTemplate = new ModelTemplate(Optional.of(Id.of("block/template_tall_fire_side_alt")), Optional.empty(), TextureSlot.FIRE);
        ModelTemplate shortFireSideAltTemplate = new ModelTemplate(Optional.of(Id.of("block/template_short_fire_side_alt")), Optional.empty(), TextureSlot.FIRE);
        TextureMapping short0 = new TextureMapping().put(TextureSlot.FIRE, TextureMapping.getBlockTexture(BwtBlocks.stokedFireBlock, "_short_0"));
        TextureMapping short1 = new TextureMapping().put(TextureSlot.FIRE, TextureMapping.getBlockTexture(BwtBlocks.stokedFireBlock, "_short_1"));
        MultiVariant tallFloorIdentifiers = variants(
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_floor0"), TextureMapping.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_floor1"), TextureMapping.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput)
        );
        MultiVariant shortFloorIdentifiers = variants(
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_floor0"), short0, blockStateModelGenerator.modelOutput),
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_floor1"), short1, blockStateModelGenerator.modelOutput)
        );
        MultiVariant tallSideIdentifiers = variants(
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side0"), TextureMapping.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side1"), TextureMapping.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side_alt0"), TextureMapping.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side_alt1"), TextureMapping.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput)
        );
        MultiVariant shortSideIdentifiers = variants(
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side0"), short0, blockStateModelGenerator.modelOutput),
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side1"), short1, blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side_alt0"), short0, blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side_alt1"), short1, blockStateModelGenerator.modelOutput)
        );
        ConditionBuilder whenShort = new ConditionBuilder().term(StokedFireBlock.TWO_HIGH, false);
        ConditionBuilder whenTall = new ConditionBuilder().term(StokedFireBlock.TWO_HIGH, true);
        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(BwtBlocks.stokedFireBlock)
                .with(whenShort, shortFloorIdentifiers)
                .with(whenTall, tallFloorIdentifiers)
                .with(whenShort, shortSideIdentifiers)
                .with(whenTall, tallSideIdentifiers)
                .with(whenShort, shortSideIdentifiers.with(BlockModelGenerators.Y_ROT_90))
                .with(whenTall, tallSideIdentifiers.with(BlockModelGenerators.Y_ROT_90))
                .with(whenShort, shortSideIdentifiers.with(BlockModelGenerators.Y_ROT_180))
                .with(whenTall, tallSideIdentifiers.with(BlockModelGenerators.Y_ROT_180))
                .with(whenShort, shortSideIdentifiers.with(BlockModelGenerators.Y_ROT_270))
                .with(whenTall, tallSideIdentifiers.with(BlockModelGenerators.Y_ROT_270))
        );
    }

    private void generateMiningChargeBlock(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.miningChargeBlock, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.miningChargeBlock)))
                        .with(PropertyDispatch
                                .modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                                .select(AttachFace.CEILING, Direction.NORTH, BlockModelGenerators.X_ROT_180.then(BlockModelGenerators.Y_ROT_180))
                                .select(AttachFace.CEILING, Direction.EAST, BlockModelGenerators.X_ROT_180.then(BlockModelGenerators.Y_ROT_270))
                                .select(AttachFace.CEILING, Direction.SOUTH, BlockModelGenerators.X_ROT_180)
                                .select(AttachFace.CEILING, Direction.WEST, BlockModelGenerators.X_ROT_180.then(BlockModelGenerators.Y_ROT_90))
                                .select(AttachFace.FLOOR, Direction.NORTH, BlockModelGenerators.NOP)
                                .select(AttachFace.FLOOR, Direction.EAST, BlockModelGenerators.Y_ROT_90)
                                .select(AttachFace.FLOOR, Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                                .select(AttachFace.FLOOR, Direction.WEST, BlockModelGenerators.Y_ROT_270)
                                .select(AttachFace.WALL, Direction.NORTH, BlockModelGenerators.X_ROT_90)
                                .select(AttachFace.WALL, Direction.EAST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
                                .select(AttachFace.WALL, Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
                                .select(AttachFace.WALL, Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                        )
        );
    }

    public static void generateDebugLensBeam(BlockModelGenerators blockStateModelGenerator) {
        LensBeamBlock beam = BwtBlocks.lensBeamBlock;
        ResourceLocation identifier = ModelLocationUtils.getModelLocation(beam);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiPartGenerator
                        .multiPart(beam)
                        .with(
                                new ConditionBuilder().term(LensBeamBlock.NORTH, true),
                                plainVariant(identifier)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.EAST, true),
                                plainVariant(identifier).with(BlockModelGenerators.Y_ROT_90)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.SOUTH, true),
                                plainVariant(identifier).with(BlockModelGenerators.Y_ROT_180)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.WEST, true),
                                plainVariant(identifier).with(BlockModelGenerators.Y_ROT_270)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.DOWN, true),
                                plainVariant(identifier).with(BlockModelGenerators.X_ROT_90)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.UP, true),
                                plainVariant(identifier).with(BlockModelGenerators.X_ROT_270)
                        )
        );
    }

    public static void generateScrewPump(BlockModelGenerators blockStateModelGenerator) {
        ScrewPumpBlock screwPump = BwtBlocks.screwPumpBlock;
        ResourceLocation identifier = ModelLocationUtils.getModelLocation(screwPump);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiPartGenerator
                        .multiPart(screwPump)
                        .with(
                                new ConditionBuilder().term(LensBeamBlock.NORTH, true),
                                plainVariant(identifier)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.EAST, true),
                                plainVariant(identifier).with(BlockModelGenerators.Y_ROT_90)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.SOUTH, true),
                                plainVariant(identifier).with(BlockModelGenerators.Y_ROT_180)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.WEST, true),
                                plainVariant(identifier).with(BlockModelGenerators.Y_ROT_270)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.DOWN, true),
                                plainVariant(identifier).with(BlockModelGenerators.X_ROT_90)
                        ).with(
                                new ConditionBuilder().term(LensBeamBlock.UP, true),
                                plainVariant(identifier).with(BlockModelGenerators.X_ROT_270)
                        )
        );
    }

    public static void generatePaneBlock(BlockModelGenerators blockStateModelGenerator, Block pane) {
        MultiVariant multiVariant = plainVariant(ModelLocationUtils.getModelLocation(pane, "_post_ends"));
        MultiVariant multiVariant2 = plainVariant(ModelLocationUtils.getModelLocation(pane, "_post"));
        MultiVariant multiVariant3 = plainVariant(ModelLocationUtils.getModelLocation(pane, "_cap"));
        MultiVariant multiVariant4 = plainVariant(ModelLocationUtils.getModelLocation(pane, "_cap_alt"));
        MultiVariant multiVariant5 = plainVariant(ModelLocationUtils.getModelLocation(pane, "_side"));
        MultiVariant multiVariant6 = plainVariant(ModelLocationUtils.getModelLocation(pane, "_side_alt"));
        blockStateModelGenerator.blockStateOutput.accept(
                MultiPartGenerator
                        .multiPart(pane)
                        .with(multiVariant)
                        .with(
                                new ConditionBuilder().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false),
                                multiVariant2
                        )
                        .with(
                                new ConditionBuilder().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false),
                                multiVariant3
                        )
                        .with(
                                new ConditionBuilder().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false),
                                multiVariant3.with(BlockModelGenerators.Y_ROT_90)
                        )
                        .with(
                                new ConditionBuilder().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false),
                                multiVariant4
                        )
                        .with(
                                new ConditionBuilder().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true),
                                multiVariant4.with(BlockModelGenerators.Y_ROT_90)
                        )
                        .with(new ConditionBuilder().term(BlockStateProperties.NORTH, true), multiVariant5)
                        .with(new ConditionBuilder().term(BlockStateProperties.EAST, true), multiVariant5.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, true), multiVariant6)
                        .with(new ConditionBuilder().term(BlockStateProperties.WEST, true), multiVariant6.with(BlockModelGenerators.Y_ROT_90))
        );
        blockStateModelGenerator.registerSimpleFlatItemModel(pane);
    }

    public static void generateSidingBlock(BlockModelGenerators blockStateModelGenerator, SidingBlock sidingBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(sidingBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/siding")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(sidingBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        sidingBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(sidingBlock))
                                .with(BlockModelGenerators.UV_LOCK)
                ).with(NORTH_DEFAULT_ROTATION)
        );
        blockStateModelGenerator.registerSimpleItemModel(sidingBlock, ModelLocationUtils.getModelLocation(sidingBlock));
    }

    public static void generateMouldingBlock(BlockModelGenerators blockStateModelGenerator, MouldingBlock mouldingBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(mouldingBlock.fullBlock);
        ModelTemplate horizontalModel = new ModelTemplate(Optional.of(Id.of("block/moulding")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ModelTemplate verticalModel = new ModelTemplate(Optional.of(Id.of("block/moulding_vertical")), Optional.of("_vertical"), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ResourceLocation horizontalId = horizontalModel.create(mouldingBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        ResourceLocation verticalId = verticalModel.create(mouldingBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(mouldingBlock).with(createMouldingOrientationMap(horizontalId, verticalId)));
        blockStateModelGenerator.registerSimpleItemModel(mouldingBlock, horizontalId);
    }

    public static void generateCornerBlock(BlockModelGenerators blockStateModelGenerator, CornerBlock cornerBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(cornerBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/corner")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(cornerBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        cornerBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(cornerBlock))
                                .with(BlockModelGenerators.UV_LOCK)
                ).with(createCornerOrientationMap())
        );
        blockStateModelGenerator.registerSimpleItemModel(cornerBlock, ModelLocationUtils.getModelLocation(cornerBlock));
    }

    public static void generateColumnBlock(BlockModelGenerators blockStateModelGenerator, ColumnBlock columnBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(columnBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/column")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(columnBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(columnBlock, plainVariant(ModelLocationUtils.getModelLocation(columnBlock)).with(BlockModelGenerators.UV_LOCK)));
        blockStateModelGenerator.registerSimpleItemModel(columnBlock, ModelLocationUtils.getModelLocation(columnBlock));
    }

    public static void generatePedestalBlock(BlockModelGenerators blockStateModelGenerator, PedestalBlock pedestalBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(pedestalBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/pedestal")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(pedestalBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        pedestalBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(pedestalBlock))
                                .with(BlockModelGenerators.UV_LOCK)
                ).with(PropertyDispatch.modify(PedestalBlock.VERTICAL_DIRECTION)
                        .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
                        .select(Direction.UP, BlockModelGenerators.NOP)
                )
        );
        blockStateModelGenerator.registerSimpleItemModel(pedestalBlock, ModelLocationUtils.getModelLocation(pedestalBlock));
    }

    public static void generateTableBlock(BlockModelGenerators blockStateModelGenerator, TableBlock tableBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(tableBlock.fullBlock);
        ModelTemplate tableModel = new ModelTemplate(Optional.of(Id.of("block/table")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ModelTemplate tableNoSupportModel = new ModelTemplate(Optional.of(Id.of("block/table_no_support")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ResourceLocation tableModelId = tableModel.create(ModelLocationUtils.getModelLocation(tableBlock), texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        ResourceLocation noSupportModelId = tableNoSupportModel.create(ModelLocationUtils.getModelLocation(tableBlock, "_no_support"), texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(tableBlock)
                        .with(PropertyDispatch.initial(TableBlock.SUPPORT)
                                .select(true, plainVariant(tableModelId))
                                .select(false, plainVariant(noSupportModelId))
                        )
        );
    }

    public void generateVaseBlock(BlockModelGenerators blockStateModelGenerator, VaseBlock vaseBlock) {
        ResourceLocation modelId = new ModelTemplate(
                Optional.of(Id.of("block/vase")),
                Optional.empty(),
                TextureSlot.PARTICLE,
                TextureSlot.TOP,
                TextureSlot.SIDE,
                TextureSlot.BOTTOM
        ).create(
                ModelLocationUtils.getModelLocation(vaseBlock),
                TextureMapping.cubeBottomTop(vaseBlock).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(vaseBlock, "_side")
        ), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(vaseBlock, plainVariant(modelId)));
    }

    public void generateWoolSlab(BlockModelGenerators blockStateModelGenerator, DyeColor dyeColor, SlabBlock woolSlabBlock) {
        Block woolBlock = DyeUtils.WOOL_COLORS.get(dyeColor);
        ResourceLocation identifier = ModelLocationUtils.getModelLocation(woolBlock);
        TexturedModel texturedModel = TexturedModel.CUBE.get(woolBlock);
        ResourceLocation identifier2 = ModelTemplates.SLAB_BOTTOM.create(woolSlabBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        ResourceLocation identifier3 = ModelTemplates.SLAB_TOP.create(woolSlabBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(createSlab(woolSlabBlock, identifier2, identifier3, identifier));
    }

    public static PropertyDispatch<VariantMutator> createUpDefaultRotationStates() {
        return PropertyDispatch.modify(BlockStateProperties.FACING)
                .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
                .select(Direction.UP, BlockModelGenerators.NOP)
                .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
                .select(Direction.SOUTH, BlockModelGenerators.X_ROT_270)
                .select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                .select(Direction.EAST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90));
    }

    public static PropertyDispatch<MultiVariant> createMouldingOrientationMap(ResourceLocation horizontalId, ResourceLocation verticalId) {
        return PropertyDispatch.initial(MouldingBlock.ORIENTATION)
                // horizontal, bottom - west, north, east, south
                .select(0, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.Y_ROT_180)))
                .select(1, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.Y_ROT_270)))
                .select(2, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK))
                .select(3, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.Y_ROT_90)))
                // vertical - west, north, east, south
                .select(4, plainVariant(verticalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.Y_ROT_180)))
                .select(5, plainVariant(verticalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.Y_ROT_270)))
                .select(6, plainVariant(verticalId).with(BlockModelGenerators.UV_LOCK))
                .select(7, plainVariant(verticalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.Y_ROT_90)))
                // horizontal, top - west, north, east, south
                .select(8, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.X_ROT_180)))
                .select(9, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.X_ROT_180).then(BlockModelGenerators.Y_ROT_90)))
                .select(10, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.X_ROT_180).then(BlockModelGenerators.Y_ROT_180)))
                .select(11, plainVariant(horizontalId).with(BlockModelGenerators.UV_LOCK.then(BlockModelGenerators.X_ROT_180).then(BlockModelGenerators.Y_ROT_270)));
    }

    public static PropertyDispatch<VariantMutator> createCornerOrientationMap() {
        return PropertyDispatch.modify(CornerBlock.ORIENTATION)
                // bottom - south-west, north-west, north-east, south-east
                .select(0, BlockModelGenerators.Y_ROT_90)
                .select(1, BlockModelGenerators.Y_ROT_180)
                .select(2, BlockModelGenerators.Y_ROT_270)
                .select(3, BlockModelGenerators.NOP)
                // top - south-west, north-west, north-east, south-east
                .select(4, BlockModelGenerators.X_ROT_180)
                .select(5, BlockModelGenerators.X_ROT_180.then(BlockModelGenerators.Y_ROT_90))
                .select(6, BlockModelGenerators.X_ROT_180.then(BlockModelGenerators.Y_ROT_180))
                .select(7, BlockModelGenerators.X_ROT_180.then(BlockModelGenerators.Y_ROT_270));
    }

    public void generateDirtAndGrassSlab(BlockModelGenerators blockStateModelGenerator) {
        //Dirt Slab
        TexturedModel dirtTexturedModel = TexturedModel.CUBE.get(Blocks.DIRT);
        ResourceLocation dirtTexture = dirtTexturedModel.getMapping().get(TextureSlot.ALL);
        ResourceLocation dirtSlab = TexturedModel.createDefault(
                block -> new TextureMapping()
                        .put(TextureSlot.BOTTOM, dirtTexture)
                        .put(TextureSlot.TOP, dirtTexture)
                        .put(TextureSlot.SIDE, dirtTexture),
                ModelTemplates.SLAB_BOTTOM
        ).create(BwtBlocks.dirtSlabBlock, blockStateModelGenerator.modelOutput);
        ResourceLocation snowyDirtSlab = Id.of("block/snowy_dirt_slab");
        ResourceLocation dirtPathSlab = Id.of("block/dirt_path_slab");
        ResourceLocation grassSlab = Id.of("block/grass_slab");
        ResourceLocation snowyGrassSlab = Id.of("block/snowy_grass_slab");
        ResourceLocation myceliumSlab = Id.of("block/mycelium_slab");
        ResourceLocation snowyMyceliumSlab = Id.of("block/snowy_mycelium_slab");
        ResourceLocation podzolSlab = Id.of("block/podzol_slab");
        ResourceLocation snowyPodzolSlab = Id.of("block/snowy_podzol_slab");

        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.dirtSlabBlock)
                        .with(
                                PropertyDispatch.initial(DirtSlabBlock.SNOWY)
                                        .select(true, plainVariant(snowyDirtSlab))
                                        .select(false, plainVariant(dirtSlab))
                        )

        );

        //Grass Slab
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.grassSlabBlock)
                        .with(
                                PropertyDispatch.initial(DirtSlabBlock.SNOWY)
                                        .select(true, plainVariant(snowyGrassSlab))
                                        .select(false, plainVariant(grassSlab))
                        )
        );
        blockStateModelGenerator.registerSimpleTintedItemModel(BwtBlocks.grassSlabBlock, ModelLocationUtils.getModelLocation(BwtBlocks.grassSlabBlock), new GrassColorSource());
        //Mycelium Slab
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.myceliumSlabBlock)
                        .with(
                                PropertyDispatch.initial(DirtSlabBlock.SNOWY)
                                        .select(true, plainVariant(snowyMyceliumSlab))
                                        .select(false, plainVariant(myceliumSlab))
                        )
        );
        //Podzol Slab
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.podzolSlabBlock)
                        .with(
                                PropertyDispatch.initial(DirtSlabBlock.SNOWY)
                                        .select(true, plainVariant(snowyPodzolSlab))
                                        .select(false, plainVariant(podzolSlab))
                        )
        );
        //Dirt Path Slab
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(BwtBlocks.dirtPathSlabBlock, plainVariant(dirtPathSlab)));
    }

    private static PropertyDispatch<MultiVariant> createBooleanModelDispatch(
            BooleanProperty booleanProperty, MultiVariant trueVariant, MultiVariant falseVariant
    ) {
        return PropertyDispatch.<Boolean>initial(booleanProperty).select(true, trueVariant).select(false, falseVariant);
    }

    private static PropertyDispatch<MultiVariant> createBooleanModelDispatch(
            BooleanProperty booleanProperty, ResourceLocation trueVariant, ResourceLocation falseVariant
    ) {
        return createBooleanModelDispatch(booleanProperty, plainVariant(trueVariant), plainVariant(falseVariant));
    }

    static MultiVariantGenerator createSimpleBlock(Block block) {
        return createSimpleBlock(block, ModelLocationUtils.getModelLocation(block));
    }

    static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation resourceLocation) {
        return createSimpleBlock(block, plainVariant(resourceLocation));
    }

    static MultiVariantGenerator createSimpleBlock(Block block, MultiVariant multiVariant) {
        return MultiVariantGenerator.dispatch(block, multiVariant);
    }

    protected static MultiVariant variants(Stream<Variant> variants) {
        return new MultiVariant(WeightedList.of(variants.map(variant -> new Weighted<>(variant, 1)).toList()));
    }

    protected static MultiVariant variants(Variant... variants) {
        return variants(Arrays.stream(variants));
    }

    protected static MultiVariant variants(ResourceLocation... resourceLocations) {
        return variants(Arrays.stream(resourceLocations).map(ModelGenerator::plainModel));
    }

    protected static MultiVariant plainVariant(ResourceLocation resourceLocation) {
        return variant(plainModel(resourceLocation));
    }

    static Variant plainModel(ResourceLocation resourceLocation) {
        return new Variant(resourceLocation);
    }

    static MultiVariant variant(Variant variant) {
        return new MultiVariant(WeightedList.of(variant));
    }

    static BlockModelDefinitionGenerator createSlab(Block block, MultiVariant multiVariant, MultiVariant multiVariant2, MultiVariant multiVariant3) {
        return MultiVariantGenerator.dispatch(block)
                .with(
                        PropertyDispatch.initial(BlockStateProperties.SLAB_TYPE)
                                .select(SlabType.BOTTOM, multiVariant)
                                .select(SlabType.TOP, multiVariant2)
                                .select(SlabType.DOUBLE, multiVariant3)
                );
    }

    static BlockModelDefinitionGenerator createSlab(Block block, ResourceLocation variant1, ResourceLocation variant2, ResourceLocation variant3) {
        return createSlab(block, plainVariant(variant1), plainVariant(variant2), plainVariant(variant3));
    }

}

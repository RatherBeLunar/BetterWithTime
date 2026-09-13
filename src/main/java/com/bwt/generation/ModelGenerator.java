package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.dirt_slab.DirtSlabBlock;
import com.bwt.blocks.lens.LensBeamBlock;
import com.bwt.blocks.turntable.TurntableBlock;
import com.bwt.blocks.unfired_pottery.UnfiredPotteryBlock;
import com.bwt.items.BwtItems;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.Id;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import java.util.List;
import java.util.Optional;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        generateDirtAndGrassSlab(blockStateModelGenerator);
        generateCompanionBlocks(blockStateModelGenerator);
        generateBloodWoodBlocks(blockStateModelGenerator);
        generateStokedFireBlock(blockStateModelGenerator);
        blockStateModelGenerator.delegateItemModel(BwtBlocks.sawBlock, ModelLocationUtils.getModelLocation(BwtBlocks.sawBlock));
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
        blockStateModelGenerator.createSimpleFlatItemModel(BwtBlocks.slatsBlock);
        blockStateModelGenerator.createSimpleFlatItemModel(BwtBlocks.grateBlock);
        blockStateModelGenerator.createSimpleFlatItemModel(BwtBlocks.wickerPaneBlock);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.cauldronBlock,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.cauldronBlock))
                ).with(PropertyDispatch.property(AbstractCookingPotBlock.TIP_DIRECTION)
                        .select(Direction.UP, Variant.variant())
                        .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                        .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                        .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                        .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.crucibleBlock,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.crucibleBlock))
                ).with(PropertyDispatch.property(AbstractCookingPotBlock.TIP_DIRECTION)
                        .select(Direction.UP, Variant.variant())
                        .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                        .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                        .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                        .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.pulleyBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelTemplates.CUBE_BOTTOM_TOP.create(
                                        BwtBlocks.pulleyBlock,
                                        TexturedModel.CUBE_TOP_BOTTOM.get(BwtBlocks.pulleyBlock).getMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(BwtBlocks.pulleyBlock, "_side")),
                                        blockStateModelGenerator.modelOutput
                                )
                        )
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(BwtBlocks.turntableBlock)
                .with(PropertyDispatch.property(TurntableBlock.TICK_SETTING)
                        .select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_0")))
                        .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_1")))
                        .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_2")))
                        .select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_3")))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.platformBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelLocationUtils.getModelLocation(BwtBlocks.platformBlock)
                        )
                )
        );
        ResourceLocation bellowsId = ModelLocationUtils.getModelLocation(BwtBlocks.bellowsBlock);
        ResourceLocation bellowsCompressedId = bellowsId.withSuffix("_compressed");
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(BwtBlocks.bellowsBlock)
                .with(PropertyDispatch.property(BellowsBlock.MECH_POWERED)
                        .select(true, Variant.variant().with(VariantProperties.MODEL, bellowsCompressedId))
                        .select(false, Variant.variant().with(VariantProperties.MODEL, bellowsId))
                )
                .with(BlockModelGenerators.createHorizontalFacingDispatch())
        );
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.soulForgeBlock, ModelLocationUtils.getModelLocation(BwtBlocks.soulForgeBlock))
                .with(BlockModelGenerators.createHorizontalFacingDispatch())
        );
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.screwPumpBlock, ModelLocationUtils.getModelLocation(BwtBlocks.screwPumpBlock))
                .with(BlockModelGenerators.createHorizontalFacingDispatch())
        );

        blockStateModelGenerator.skipAutoItemBlock(BwtBlocks.unfiredDecoratedPotBlockWithSherds);
        for (UnfiredPotteryBlock unfiredPotteryBlock : new UnfiredPotteryBlock[]{BwtBlocks.unfiredDecoratedPotBlock, BwtBlocks.unfiredDecoratedPotBlockWithSherds, BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredFlowerPotBlock}) {
            blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(unfiredPotteryBlock)
                    .with(PropertyDispatch.property(UnfiredPotteryBlock.COOKING)
                            .select(false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(unfiredPotteryBlock)))
                            .select(true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(unfiredPotteryBlock).withSuffix("_cooking")))
                    )
            );
        }
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(BwtBlocks.urnBlock)
                .with(PropertyDispatch.property(UrnBlock.CONNECTED_UP)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.urnBlock)))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.urnBlock).withSuffix("_connected_up")))
                )
        );
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.planterBlock, ModelLocationUtils.getModelLocation(BwtBlocks.planterBlock)));
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.soilPlanterBlock, ModelLocationUtils.getModelLocation(BwtBlocks.soilPlanterBlock)));
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.soulSandPlanterBlock, ModelLocationUtils.getModelLocation(BwtBlocks.soulSandPlanterBlock)));
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.grassPlanterBlock, ModelLocationUtils.getModelLocation(BwtBlocks.grassPlanterBlock)));
        ResourceLocation buddyBlockModelId = TexturedModel.createDefault(block -> TextureMapping.orientableCubeOnlyTop(block).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side")), ModelTemplates.CUBE_ORIENTABLE)
                .create(BwtBlocks.buddyBlock, blockStateModelGenerator.modelOutput);
        ResourceLocation buddyBlockPoweredModelId = TexturedModel.createDefault(block -> new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side_powered")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front_powered")).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side_powered")), ModelTemplates.CUBE_ORIENTABLE)
                .createWithSuffix(BwtBlocks.buddyBlock, "_powered", blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(BwtBlocks.buddyBlock, Variant.variant().with(VariantProperties.MODEL, buddyBlockModelId))
                        .with(BlockModelGenerators.createBooleanModelDispatch(BuddyBlock.POWERED, buddyBlockPoweredModelId, buddyBlockModelId))
                        .with(BlockModelGenerators.createFacingDispatch())
        );
        TexturedModel.createDefault(block -> TextureMapping.orientableCubeOnlyTop(block).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side")), ModelTemplates.CUBE_ORIENTABLE)
                .create(BwtBlocks.soapBlock, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.delegateItemModel(BwtBlocks.soapBlock, ModelLocationUtils.getModelLocation(BwtBlocks.soapBlock));
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.ropeCoilBlock, TexturedModel.COLUMN);
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.paddingBlock, TexturedModel.CUBE);
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.wickerBlock, TexturedModel.CUBE);
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createRotatedVariant(BwtBlocks.dungBlock, ModelLocationUtils.getModelLocation(BwtBlocks.dungBlock)));
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSlab(
                BwtBlocks.wickerSlabBlock,
                ModelLocationUtils.getModelLocation(BwtBlocks.wickerSlabBlock),
                ModelLocationUtils.getModelLocation(BwtBlocks.wickerSlabBlock, "_top"),
                ModelLocationUtils.getModelLocation(BwtBlocks.wickerBlock)
        ));
        generateMiningChargeBlock(blockStateModelGenerator);
        TexturedModel.createDefault(TextureMapping::commandBlock, ModelTemplates.COMMAND_BLOCK)
                .create(BwtBlocks.lensBlock, blockStateModelGenerator.modelOutput);
        generateDebugLensBeam(blockStateModelGenerator);
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BwtBlocks.lensBeamGlassBlock, ModelLocationUtils.getModelLocation(Blocks.GLASS)));
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.aqueductBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelTemplates.CUBE_BOTTOM_TOP.create(
                                        BwtBlocks.aqueductBlock,
                                        TexturedModel.CUBE_TOP_BOTTOM.get(BwtBlocks.aqueductBlock).getMapping(),
                                        blockStateModelGenerator.modelOutput
                                )
                        )
                )
        );

        blockStateModelGenerator.delegateItemModel(BwtBlocks.aqueductBlock, ModelLocationUtils.getModelLocation(BwtBlocks.aqueductBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.anchorBlock, ModelLocationUtils.getModelLocation(BwtBlocks.anchorBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.axleBlock, ModelLocationUtils.getModelLocation(BwtBlocks.axleBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.creativePowerSouceBlock, ModelLocationUtils.getModelLocation(BwtBlocks.creativePowerSouceBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.blockDispenserBlock, ModelLocationUtils.getModelLocation(BwtBlocks.blockDispenserBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.cauldronBlock, ModelLocationUtils.getModelLocation(BwtBlocks.cauldronBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.crucibleBlock, ModelLocationUtils.getModelLocation(BwtBlocks.crucibleBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.detectorBlock, ModelLocationUtils.getModelLocation(BwtBlocks.detectorBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.gearBoxBlock, ModelLocationUtils.getModelLocation(BwtBlocks.gearBoxBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.screwPumpBlock, ModelLocationUtils.getModelLocation(BwtBlocks.screwPumpBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.redstoneClutchBlock, ModelLocationUtils.getModelLocation(BwtBlocks.redstoneClutchBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.handCrankBlock, ModelLocationUtils.getModelLocation(BwtBlocks.handCrankBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.hibachiBlock, ModelLocationUtils.getModelLocation(BwtBlocks.hibachiBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.hopperBlock, ModelLocationUtils.getModelLocation(BwtBlocks.hopperBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.lensBlock, ModelLocationUtils.getModelLocation(BwtBlocks.lensBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.lightBlockBlock, ModelLocationUtils.getModelLocation(BwtBlocks.lightBlockBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.millStoneBlock, ModelLocationUtils.getModelLocation(BwtBlocks.millStoneBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.obsidianPressurePlateBlock, ModelLocationUtils.getModelLocation(BwtBlocks.obsidianPressurePlateBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.pulleyBlock, ModelLocationUtils.getModelLocation(BwtBlocks.pulleyBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.turntableBlock, ModelLocationUtils.getModelLocation(BwtBlocks.turntableBlock, "_0"));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.bellowsBlock, ModelLocationUtils.getModelLocation(BwtBlocks.bellowsBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.unfiredDecoratedPotBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredDecoratedPotBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.unfiredCrucibleBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredCrucibleBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.unfiredPlanterBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredPlanterBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.unfiredVaseBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredVaseBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.unfiredUrnBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredUrnBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.unfiredFlowerPotBlock, ModelLocationUtils.getModelLocation(BwtBlocks.unfiredFlowerPotBlock));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.vineTrapBlock, ModelLocationUtils.getModelLocation(BwtBlocks.vineTrapBlock));
        blockStateModelGenerator.createSimpleFlatItemModel(BwtBlocks.urnBlock.asItem());
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
        itemModelGenerator.generateFlatItem(BwtItems.concentratedHellfireItem, ModelTemplates.FLAT_ITEM);
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
        blockStateModelGenerator.createPlant(BwtBlocks.bloodWoodBlocks.saplingBlock, BwtBlocks.bloodWoodBlocks.pottedSaplingBlock, BlockModelGenerators.TintState.NOT_TINTED);
        blockStateModelGenerator.family(BwtBlocks.bloodWoodBlocks.blockFamily.getBaseBlock()).generateFor(BwtBlocks.bloodWoodBlocks.blockFamily);
    }

    private void generateCompanionBlocks(BlockModelGenerators blockStateModelGenerator) {
        ResourceLocation companionCubeModelId = TexturedModel.ORIENTABLE_ONLY_TOP.create(BwtBlocks.companionCubeBlock, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.companionCubeBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelLocationUtils.getModelLocation(BwtBlocks.companionCubeBlock)
                        )
                ).with(BlockModelGenerators.createFacingDispatch())
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
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSlab(BwtBlocks.companionSlabBlock, companionSlabBottom, companionSlabTop, companionCubeModelId));
        blockStateModelGenerator.delegateItemModel(BwtBlocks.companionSlabBlock, companionSlabBottom);
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
        List<ResourceLocation> tallFloorIdentifiers = ImmutableList.of(
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_floor0"), TextureMapping.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_floor1"), TextureMapping.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput)
        );
        List<ResourceLocation> shortFloorIdentifiers = ImmutableList.of(
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_floor0"), short0, blockStateModelGenerator.modelOutput),
                tallFireFloorTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_floor1"), short1, blockStateModelGenerator.modelOutput)
        );
        List<ResourceLocation> tallSideIdentifiers = ImmutableList.of(
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side0"), TextureMapping.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side1"), TextureMapping.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side_alt0"), TextureMapping.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_tall_side_alt1"), TextureMapping.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelOutput)
        );
        List<ResourceLocation> shortSideIdentifiers = ImmutableList.of(
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side0"), short0, blockStateModelGenerator.modelOutput),
                tallFireSideTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side1"), short1, blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side_alt0"), short0, blockStateModelGenerator.modelOutput),
                tallFireSideAltTemplate.create(ModelLocationUtils.getModelLocation(BwtBlocks.stokedFireBlock, "_short_side_alt1"), short1, blockStateModelGenerator.modelOutput)
        );
        Condition whenShort = Condition.condition().term(StokedFireBlock.TWO_HIGH, false);
        Condition whenTall = Condition.condition().term(StokedFireBlock.TWO_HIGH, true);
        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(BwtBlocks.stokedFireBlock)
                .with(whenShort, BlockModelGenerators.wrapModels(shortFloorIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenTall, BlockModelGenerators.wrapModels(tallFloorIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenShort, BlockModelGenerators.wrapModels(shortSideIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenTall, BlockModelGenerators.wrapModels(tallSideIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenShort, BlockModelGenerators.wrapModels(shortSideIdentifiers, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))
                .with(whenTall, BlockModelGenerators.wrapModels(tallSideIdentifiers, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))
                .with(whenShort, BlockModelGenerators.wrapModels(shortSideIdentifiers, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)))
                .with(whenTall, BlockModelGenerators.wrapModels(tallSideIdentifiers, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)))
                .with(whenShort, BlockModelGenerators.wrapModels(shortSideIdentifiers, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
                .with(whenTall, BlockModelGenerators.wrapModels(tallSideIdentifiers, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    private void generateMiningChargeBlock(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(BwtBlocks.miningChargeBlock, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.miningChargeBlock)))
                        .with(PropertyDispatch
                                .properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                                .select(AttachFace.CEILING, Direction.NORTH, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.CEILING, Direction.EAST, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(AttachFace.CEILING, Direction.SOUTH, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.CEILING, Direction.WEST, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
                                .select(AttachFace.FLOOR, Direction.EAST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.FLOOR, Direction.WEST, Variant.variant()
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(AttachFace.WALL, Direction.NORTH, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                                .select(AttachFace.WALL, Direction.EAST, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(AttachFace.WALL, Direction.SOUTH, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(AttachFace.WALL, Direction.WEST, Variant.variant()
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
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
                                Condition.condition().term(LensBeamBlock.NORTH, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier)
                        ).with(
                                Condition.condition().term(LensBeamBlock.EAST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(LensBeamBlock.SOUTH, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                        ).with(
                                Condition.condition().term(LensBeamBlock.WEST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                        ).with(
                                Condition.condition().term(LensBeamBlock.DOWN, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(LensBeamBlock.UP, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
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
                                Condition.condition().term(LensBeamBlock.NORTH, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier)
                        ).with(
                                Condition.condition().term(LensBeamBlock.EAST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(LensBeamBlock.SOUTH, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                        ).with(
                                Condition.condition().term(LensBeamBlock.WEST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                        ).with(
                                Condition.condition().term(LensBeamBlock.DOWN, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(LensBeamBlock.UP, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                        )
        );
    }

    public static void generatePaneBlock(BlockModelGenerators blockStateModelGenerator, Block pane) {
        ResourceLocation identifier = ModelLocationUtils.getModelLocation(pane, "_post_ends");
        ResourceLocation identifier2 = ModelLocationUtils.getModelLocation(pane, "_post");
        ResourceLocation identifier3 = ModelLocationUtils.getModelLocation(pane, "_cap");
        ResourceLocation identifier4 = ModelLocationUtils.getModelLocation(pane, "_cap_alt");
        ResourceLocation identifier5 = ModelLocationUtils.getModelLocation(pane, "_side");
        ResourceLocation identifier6 = ModelLocationUtils.getModelLocation(pane, "_side_alt");
        blockStateModelGenerator.blockStateOutput.accept(
                MultiPartGenerator
                        .multiPart(pane)
                        .with(Variant.variant().with(VariantProperties.MODEL, identifier))
                        .with(
                                Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false),
                                Variant.variant().with(VariantProperties.MODEL, identifier2)
                        ).with(
                                Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false),
                                Variant.variant().with(VariantProperties.MODEL, identifier3)
                        ).with(
                                Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false),
                                Variant.variant().with(VariantProperties.MODEL, identifier3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false),
                                Variant.variant().with(VariantProperties.MODEL, identifier4)
                        ).with(
                                Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(BlockStateProperties.NORTH, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier5)
                        ).with(
                                Condition.condition().term(BlockStateProperties.EAST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        ).with(
                                Condition.condition().term(BlockStateProperties.SOUTH, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier6)
                        ).with(
                                Condition.condition().term(BlockStateProperties.WEST, true),
                                Variant.variant().with(VariantProperties.MODEL, identifier6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        )
        );
        blockStateModelGenerator.createSimpleFlatItemModel(pane);
    }

    public static void generateSidingBlock(BlockModelGenerators blockStateModelGenerator, SidingBlock sidingBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(sidingBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/siding")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(sidingBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        sidingBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelLocationUtils.getModelLocation(sidingBlock)
                        ).with(VariantProperties.UV_LOCK, true)
                ).with(BlockModelGenerators.createFacingDispatch())
        );
        blockStateModelGenerator.delegateItemModel(sidingBlock, ModelLocationUtils.getModelLocation(sidingBlock));
    }

    public static void generateMouldingBlock(BlockModelGenerators blockStateModelGenerator, MouldingBlock mouldingBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(mouldingBlock.fullBlock);
        ModelTemplate horizontalModel = new ModelTemplate(Optional.of(Id.of("block/moulding")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ModelTemplate verticalModel = new ModelTemplate(Optional.of(Id.of("block/moulding_vertical")), Optional.of("_vertical"), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ResourceLocation horizontalId = horizontalModel.create(mouldingBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        ResourceLocation verticalId = verticalModel.create(mouldingBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(mouldingBlock).with(createMouldingOrientationMap(horizontalId, verticalId)));
        blockStateModelGenerator.delegateItemModel(mouldingBlock, horizontalId);
    }

    public static void generateCornerBlock(BlockModelGenerators blockStateModelGenerator, CornerBlock cornerBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(cornerBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/corner")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(cornerBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        cornerBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelLocationUtils.getModelLocation(cornerBlock)
                        ).with(VariantProperties.UV_LOCK, true)
                ).with(createCornerOrientationMap())
        );
        blockStateModelGenerator.delegateItemModel(cornerBlock, ModelLocationUtils.getModelLocation(cornerBlock));
    }

    public static void generateColumnBlock(BlockModelGenerators blockStateModelGenerator, ColumnBlock columnBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(columnBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/column")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(columnBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(columnBlock, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(columnBlock)).with(VariantProperties.UV_LOCK, true)));
        blockStateModelGenerator.delegateItemModel(columnBlock, ModelLocationUtils.getModelLocation(columnBlock));
    }

    public static void generatePedestalBlock(BlockModelGenerators blockStateModelGenerator, PedestalBlock pedestalBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(pedestalBlock.fullBlock);
        ModelTemplate model = new ModelTemplate(Optional.of(Id.of("block/pedestal")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        model.create(pedestalBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        pedestalBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelLocationUtils.getModelLocation(pedestalBlock)
                        ).with(VariantProperties.UV_LOCK, true)
                ).with(PropertyDispatch.property(PedestalBlock.VERTICAL_DIRECTION)
                        .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                        .select(Direction.UP, Variant.variant())
                )
        );
        blockStateModelGenerator.delegateItemModel(pedestalBlock, ModelLocationUtils.getModelLocation(pedestalBlock));
    }

    public static void generateTableBlock(BlockModelGenerators blockStateModelGenerator, TableBlock tableBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE.get(tableBlock.fullBlock);
        ModelTemplate tableModel = new ModelTemplate(Optional.of(Id.of("block/table")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ModelTemplate tableNoSupportModel = new ModelTemplate(Optional.of(Id.of("block/table_no_support")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
        ResourceLocation tableModelId = tableModel.create(ModelLocationUtils.getModelLocation(tableBlock), texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        ResourceLocation noSupportModelId = tableNoSupportModel.create(ModelLocationUtils.getModelLocation(tableBlock, "_no_support"), texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(tableBlock)
                        .with(PropertyDispatch.property(TableBlock.SUPPORT)
                                .select(true, Variant.variant().with(VariantProperties.MODEL, tableModelId))
                                .select(false, Variant.variant().with(VariantProperties.MODEL, noSupportModelId))
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
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(vaseBlock, Variant.variant().with(VariantProperties.MODEL, modelId)));
    }

    public void generateWoolSlab(BlockModelGenerators blockStateModelGenerator, DyeColor dyeColor, SlabBlock woolSlabBlock) {
        Block woolBlock = DyeUtils.WOOL_COLORS.get(dyeColor);
        ResourceLocation identifier = ModelLocationUtils.getModelLocation(woolBlock);
        TexturedModel texturedModel = TexturedModel.CUBE.get(woolBlock);
        ResourceLocation identifier2 = ModelTemplates.SLAB_BOTTOM.create(woolSlabBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        ResourceLocation identifier3 = ModelTemplates.SLAB_TOP.create(woolSlabBlock, texturedModel.getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSlab(woolSlabBlock, identifier2, identifier3, identifier));
    }

    public static PropertyDispatch createUpDefaultRotationStates() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.UP, Variant.variant())
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    public static PropertyDispatch createMouldingOrientationMap(ResourceLocation horizontalId, ResourceLocation verticalId) {
        return PropertyDispatch.property(MouldingBlock.ORIENTATION)
                // horizontal, bottom - west, north, east, south
                .select(0, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(1, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(2, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true))
                .select(3, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                // vertical - west, north, east, south
                .select(4, Variant.variant().with(VariantProperties.MODEL, verticalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(5, Variant.variant().with(VariantProperties.MODEL, verticalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(6, Variant.variant().with(VariantProperties.MODEL, verticalId).with(VariantProperties.UV_LOCK, true))
                .select(7, Variant.variant().with(VariantProperties.MODEL, verticalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                // horizontal, top - west, north, east, south
                .select(8, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(9, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(10, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(11, Variant.variant().with(VariantProperties.MODEL, horizontalId).with(VariantProperties.UV_LOCK, true).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    public static PropertyDispatch createCornerOrientationMap() {
        return PropertyDispatch.property(CornerBlock.ORIENTATION)
                // bottom - south-west, north-west, north-east, south-east
                .select(0, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(1, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(2, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(3, Variant.variant())
                // top - south-west, north-west, north-east, south-east
                .select(4, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(5, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(6, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(7, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
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
                MultiVariantGenerator.multiVariant(BwtBlocks.dirtSlabBlock)
                        .with(
                                PropertyDispatch.property(DirtSlabBlock.SNOWY)
                                        .select(true, Variant.variant().with(VariantProperties.MODEL, snowyDirtSlab))
                                        .select(false, Variant.variant().with(VariantProperties.MODEL, dirtSlab))
                        )

        );

        //Grass Slab
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(BwtBlocks.grassSlabBlock)
                        .with(
                                PropertyDispatch.property(DirtSlabBlock.SNOWY)
                                        .select(true, Variant.variant().with(VariantProperties.MODEL, snowyGrassSlab))
                                        .select(false, Variant.variant().with(VariantProperties.MODEL, grassSlab))
                        )
        );
        //Mycelium Slab
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(BwtBlocks.myceliumSlabBlock)
                        .with(
                                PropertyDispatch.property(DirtSlabBlock.SNOWY)
                                        .select(true, Variant.variant().with(VariantProperties.MODEL, snowyMyceliumSlab))
                                        .select(false, Variant.variant().with(VariantProperties.MODEL, myceliumSlab))
                        )
        );
        //Podzol Slab
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(BwtBlocks.podzolSlabBlock)
                        .with(
                                PropertyDispatch.property(DirtSlabBlock.SNOWY)
                                        .select(true, Variant.variant().with(VariantProperties.MODEL, snowyPodzolSlab))
                                        .select(false, Variant.variant().with(VariantProperties.MODEL, podzolSlab))
                        )
        );
        //Dirt Path Slab
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(BwtBlocks.dirtPathSlabBlock, Variant.variant().with(VariantProperties.MODEL, dirtPathSlab)));
    }

}

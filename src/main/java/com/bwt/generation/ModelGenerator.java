package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.blocks.turntable.TurntableBlock;
import com.bwt.items.BwtItems;
import com.bwt.utils.DyeUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.client.*;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Optional;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        generateCompanionBlocks(blockStateModelGenerator);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.sawBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(BwtBlocks.sawBlock)
                        )
                ).coordinate(createUpDefaultRotationStates())
        );
        for (SidingBlock sidingBlock : BwtBlocks.sidingBlocks) {
            generateSidingBlock(blockStateModelGenerator, sidingBlock);
        }
        for (MouldingBlock mouldingBlock : BwtBlocks.mouldingBlocks) {
            generateMouldingBlock(blockStateModelGenerator, mouldingBlock);
        }
        for (CornerBlock cornerBlock : BwtBlocks.cornerBlocks) {
            generateCornerBlock(blockStateModelGenerator, cornerBlock);
        }
        for (VaseBlock vaseBlock : BwtBlocks.vaseBlocks.values()) {
            generateVaseBlock(blockStateModelGenerator, vaseBlock);
        }
        BwtBlocks.woolSlabBlocks.forEach((dyeColor, woolSlab) -> generateWoolSlab(blockStateModelGenerator, dyeColor, woolSlab));
        blockStateModelGenerator.registerStraightRail(BwtBlocks.stoneDetectorRailBlock);
        blockStateModelGenerator.registerStraightRail(BwtBlocks.obsidianDetectorRailBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.grateBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.slatsBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.wickerBlock);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.pulleyBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                Models.CUBE_BOTTOM_TOP.upload(
                                        BwtBlocks.pulleyBlock,
                                        TexturedModel.CUBE_BOTTOM_TOP.get(BwtBlocks.pulleyBlock).getTextures().put(TextureKey.TOP, TextureMap.getSubId(BwtBlocks.pulleyBlock, "_side")),
                                        blockStateModelGenerator.modelCollector
                                )
                        )
                )
        );
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(BwtBlocks.turntableBlock)
                .coordinate(BlockStateVariantMap.create(TurntableBlock.TICK_SETTING)
                        .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(BwtBlocks.turntableBlock, "_0")))
                        .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(BwtBlocks.turntableBlock, "_1")))
                        .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(BwtBlocks.turntableBlock, "_2")))
                        .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(BwtBlocks.turntableBlock, "_3")))
                )
        );
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.platformBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(BwtBlocks.platformBlock)
                        )
                )
        );
        List<Identifier> list = blockStateModelGenerator.getFireFloorModels(BwtBlocks.stokedFireBlock);
        List<Identifier> list2 = blockStateModelGenerator.getFireSideModels(BwtBlocks.stokedFireBlock);
        blockStateModelGenerator.blockStateCollector.accept(MultipartBlockStateSupplier.create(BwtBlocks.stokedFireBlock)
                .with(BlockStateModelGenerator.buildBlockStateVariants(list, blockStateVariant -> blockStateVariant))
                .with(BlockStateModelGenerator.buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant))
                .with(BlockStateModelGenerator.buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90)))
                .with(BlockStateModelGenerator.buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180)))
                .with(BlockStateModelGenerator.buildBlockStateVariants(list2, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270))));
        Identifier bellowsId = TexturedModel.ORIENTABLE_WITH_BOTTOM.upload(BwtBlocks.bellowsBlock, blockStateModelGenerator.modelCollector);
        Identifier bellowsCompressedId = bellowsId.withSuffixedPath("_compressed");
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(BwtBlocks.bellowsBlock)
                .coordinate(BlockStateVariantMap.create(BellowsBlock.MECH_POWERED)
                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, bellowsCompressedId))
                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, bellowsId))
                )
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(BwtBlocks.soulForgeBlock, ModelIds.getBlockModelId(BwtBlocks.soulForgeBlock))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );

        for (UnfiredPotteryBlock unfiredPotteryBlock : new UnfiredPotteryBlock[]{BwtBlocks.unfiredCrucibleBlock, BwtBlocks.unfiredPlanterBlock, BwtBlocks.unfiredVaseBlock, BwtBlocks.unfiredUrnBlock, BwtBlocks.unfiredMouldBlock}) {
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(unfiredPotteryBlock)
                    .coordinate(BlockStateVariantMap.create(UnfiredPotteryBlock.COOKING)
                            .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(unfiredPotteryBlock)))
                            .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(unfiredPotteryBlock).withSuffixedPath("_cooking")))
                    )
            );
        }
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(BwtBlocks.kilnBlock, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.BRICKS))));
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(BwtBlocks.urnBlock)
                .coordinate(BlockStateVariantMap.create(UrnBlock.CONNECTED_UP)
                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.urnBlock)))
                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.urnBlock).withSuffixedPath("_connected_up")))
                )
        );
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(BwtBlocks.planterBlock, ModelIds.getBlockModelId(BwtBlocks.planterBlock)));
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(BwtBlocks.soilPlanterBlock, ModelIds.getBlockModelId(BwtBlocks.soilPlanterBlock)));
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(BwtBlocks.soulSandPlanterBlock, ModelIds.getBlockModelId(BwtBlocks.soulSandPlanterBlock)));
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(BwtBlocks.grassPlanterBlock, ModelIds.getBlockModelId(BwtBlocks.grassPlanterBlock)));


        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.anchorBlock, ModelIds.getBlockModelId(BwtBlocks.anchorBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.axleBlock, ModelIds.getBlockModelId(BwtBlocks.axleBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.blockDispenserBlock, ModelIds.getBlockModelId(BwtBlocks.blockDispenserBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.cauldronBlock, ModelIds.getBlockModelId(BwtBlocks.cauldronBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.crucibleBlock, ModelIds.getBlockModelId(BwtBlocks.crucibleBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.detectorBlock, ModelIds.getBlockModelId(BwtBlocks.detectorBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.gearBoxBlock, ModelIds.getBlockModelId(BwtBlocks.gearBoxBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.handCrankBlock, ModelIds.getBlockModelId(BwtBlocks.handCrankBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.hibachiBlock, ModelIds.getBlockModelId(BwtBlocks.hibachiBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.hopperBlock, ModelIds.getBlockModelId(BwtBlocks.hopperBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.lightBlockBlock, ModelIds.getBlockModelId(BwtBlocks.lightBlockBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.millStoneBlock, ModelIds.getBlockModelId(BwtBlocks.millStoneBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.obsidianPressuePlateBlock, ModelIds.getBlockModelId(BwtBlocks.obsidianPressuePlateBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.pulleyBlock, ModelIds.getBlockModelId(BwtBlocks.pulleyBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.turntableBlock, ModelIds.getBlockSubModelId(BwtBlocks.turntableBlock, "_0"));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.bellowsBlock, ModelIds.getBlockModelId(BwtBlocks.bellowsBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredCrucibleBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredCrucibleBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredPlanterBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredPlanterBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredVaseBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredVaseBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredUrnBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredUrnBlock));
        blockStateModelGenerator.registerItemModel(BwtBlocks.urnBlock.asItem());
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(BwtItems.armorPlateItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.beltItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.breedingHarnessItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.broadheadItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.broadheadArrowItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.canvasItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.coalDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.concentratedHellfireItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.cementBucketItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.cookedWolfChopItem, Items.COOKED_PORKCHOP, Models.GENERATED);
        itemModelGenerator.register(BwtItems.donutItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.dungItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.dynamiteItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.fabricItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.filamentItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.flourItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.foulFoodItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.gearItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.groundNetherrackItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.glueItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.haftItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hempItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hempFiberItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hempSeedsItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hellfireDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.kibbleItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.mouldItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.nethercoalItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.netherSludgeItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.paddingItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.potashItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.redstoneEyeItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.refinedPickaxeItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.refinedShovelItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.refinedAxeItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.refinedHoeItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.refinedSwordItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.mattockItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.battleAxeItem, Models.HANDHELD);
        itemModelGenerator.registerArmor(BwtItems.plateHelmArmorItem);
        itemModelGenerator.registerArmor(BwtItems.chestPlateArmorItem);
        itemModelGenerator.registerArmor(BwtItems.plateLeggingsArmorItem);
        itemModelGenerator.registerArmor(BwtItems.plateBootsArmorItem);
        itemModelGenerator.register(BwtItems.ropeItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.rottedArrowItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.sawDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.sailItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.scouredLeatherItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.screwItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soapItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soulDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soulforgedSteelItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soulUrnItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.strapItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.tallowItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.tannedLeatherItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.waterWheelItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.windmillItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.wolfChopItem, Items.PORKCHOP, Models.GENERATED);
        itemModelGenerator.register(BwtItems.woodBladeItem, Models.GENERATED);
    }

    private void generateCompanionBlocks(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier companionCubeModelId = TexturedModel.ORIENTABLE.upload(BwtBlocks.companionCubeBlock, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.companionCubeBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(BwtBlocks.companionCubeBlock)
                        )
                ).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        Identifier companionSlabBottom = TexturedModel.makeFactory(
                block -> new TextureMap()
                        .put(TextureKey.BOTTOM, TextureMap.getSubId(BwtBlocks.companionCubeBlock, "_top"))
                        .put(TextureKey.TOP, TextureMap.getSubId(BwtBlocks.companionSlabBlock, "_top"))
                        .put(TextureKey.SIDE, TextureMap.getSubId(BwtBlocks.companionCubeBlock, "_side"))
                , Models.SLAB
        ).upload(BwtBlocks.companionSlabBlock, blockStateModelGenerator.modelCollector);
        Identifier companionSlabTop = TexturedModel.makeFactory(
                block -> new TextureMap()
                        .put(TextureKey.BOTTOM, TextureMap.getSubId(BwtBlocks.companionSlabBlock, "_top"))
                        .put(TextureKey.TOP, TextureMap.getSubId(BwtBlocks.companionCubeBlock, "_top"))
                        .put(TextureKey.SIDE, TextureMap.getSubId(BwtBlocks.companionCubeBlock, "_side"))
                , Models.SLAB_TOP
        ).upload(BwtBlocks.companionSlabBlock, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(BwtBlocks.companionSlabBlock, companionSlabBottom, companionSlabTop, companionCubeModelId));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.companionSlabBlock, companionSlabBottom);
    }

    public static void generatePaneBlock(BlockStateModelGenerator blockStateModelGenerator, Block pane) {
        Identifier identifier = ModelIds.getBlockSubModelId(pane, "_post_ends");
        Identifier identifier2 = ModelIds.getBlockSubModelId(pane, "_post");
        Identifier identifier3 = ModelIds.getBlockSubModelId(pane, "_cap");
        Identifier identifier4 = ModelIds.getBlockSubModelId(pane, "_cap_alt");
        Identifier identifier5 = ModelIds.getBlockSubModelId(pane, "_side");
        Identifier identifier6 = ModelIds.getBlockSubModelId(pane, "_side_alt");
        blockStateModelGenerator.blockStateCollector.accept(
                MultipartBlockStateSupplier
                        .create(pane)
                        .with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
                        .with(
                                When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)
                        ).with(
                                When.create().set(Properties.NORTH, true).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)
                        ).with(
                                When.create().set(Properties.NORTH, false).set(Properties.EAST, true).set(Properties.SOUTH, false).set(Properties.WEST, false),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                        ).with(
                                When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, true).set(Properties.WEST, false),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)
                        ).with(
                                When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                        ).with(
                                When.create().set(Properties.NORTH, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier5)
                        ).with(
                                When.create().set(Properties.EAST, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                        ).with(
                                When.create().set(Properties.SOUTH, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier6)
                        ).with(
                                When.create().set(Properties.WEST, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier6).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                        )
        );
        blockStateModelGenerator.registerItemModel(pane);
    }

    public static void generateSidingBlock(BlockStateModelGenerator blockStateModelGenerator, SidingBlock sidingBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(sidingBlock.fullBlock);
        Model model = new Model(Optional.of(new Identifier("bwt", "block/siding")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        model.upload(sidingBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        sidingBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(sidingBlock)
                        ).put(VariantSettings.UVLOCK, true)
                ).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        blockStateModelGenerator.registerParentedItemModel(sidingBlock, ModelIds.getBlockModelId(sidingBlock));
    }

    public static void generateMouldingBlock(BlockStateModelGenerator blockStateModelGenerator, MouldingBlock mouldingBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(mouldingBlock.fullBlock);
        Model horizontalModel = new Model(Optional.of(new Identifier("bwt", "block/moulding")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        Model verticalModel = new Model(Optional.of(new Identifier("bwt", "block/moulding_vertical")), Optional.of("_vertical"), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        Identifier horizontalId = horizontalModel.upload(mouldingBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        Identifier verticalId = verticalModel.upload(mouldingBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(mouldingBlock).coordinate(createMouldingOrientationMap(horizontalId, verticalId)));
        blockStateModelGenerator.registerParentedItemModel(mouldingBlock, horizontalId);
    }

    public static void generateCornerBlock(BlockStateModelGenerator blockStateModelGenerator, CornerBlock cornerBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(cornerBlock.fullBlock);
        Model model = new Model(Optional.of(new Identifier("bwt", "block/corner")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        model.upload(cornerBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        cornerBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(cornerBlock)
                        ).put(VariantSettings.UVLOCK, true)
                ).coordinate(createCornerOrientationMap())
        );
        blockStateModelGenerator.registerParentedItemModel(cornerBlock, ModelIds.getBlockModelId(cornerBlock));
    }

    public void generateVaseBlock(BlockStateModelGenerator blockStateModelGenerator, VaseBlock vaseBlock) {
        Identifier modelId = new Model(Optional.of(new Identifier("bwt", "block/vase")), Optional.empty(), TextureKey.TEXTURE, TextureKey.PARTICLE).upload(ModelIds.getBlockModelId(vaseBlock), TextureMap.texture(vaseBlock).put(TextureKey.PARTICLE, TextureMap.getId(vaseBlock)), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(vaseBlock, BlockStateVariant.create().put(VariantSettings.MODEL, modelId)));
    }

    public void generateWoolSlab(BlockStateModelGenerator blockStateModelGenerator, DyeColor dyeColor, SlabBlock woolSlabBlock) {
        Block woolBlock = DyeUtils.WOOL_COLORS.get(dyeColor);
        Identifier identifier = ModelIds.getBlockModelId(woolBlock);
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(woolBlock);
        Identifier identifier2 = Models.SLAB.upload(woolSlabBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        Identifier identifier3 = Models.SLAB_TOP.upload(woolSlabBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(woolSlabBlock, identifier2, identifier3, identifier));
    }

    public static BlockStateVariantMap createUpDefaultRotationStates() {
        return BlockStateVariantMap.create(Properties.FACING)
                .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                .register(Direction.UP, BlockStateVariant.create())
                .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }

    public static BlockStateVariantMap createMouldingOrientationMap(Identifier horizontalId, Identifier verticalId) {
        return BlockStateVariantMap.create(MouldingBlock.ORIENTATION)
                // horizontal, bottom - west, north, east, south
                .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true))
                .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                // vertical - west, north, east, south
                .register(4, BlockStateVariant.create().put(VariantSettings.MODEL, verticalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(5, BlockStateVariant.create().put(VariantSettings.MODEL, verticalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(6, BlockStateVariant.create().put(VariantSettings.MODEL, verticalId).put(VariantSettings.UVLOCK, true))
                .register(7, BlockStateVariant.create().put(VariantSettings.MODEL, verticalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                // horizontal, top - west, north, east, south
                .register(8, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.X, VariantSettings.Rotation.R180))
                .register(9, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(10, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(11, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalId).put(VariantSettings.UVLOCK, true).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270));
    }

    public static BlockStateVariantMap createCornerOrientationMap() {
        return BlockStateVariantMap.create(CornerBlock.ORIENTATION)
                // bottom - south-west, north-west, north-east, south-east
                .register(0, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(1, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(2, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(3, BlockStateVariant.create())
                // top - south-west, north-west, north-east, south-east
                .register(4, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                .register(5, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(6, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(7, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270));
    }
}

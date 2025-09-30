package com.bwt.generation;

import com.bwt.blocks.*;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.dirt_slab.DirtSlabBlock;
import com.bwt.blocks.lens.LensBeamBlock;
import com.bwt.blocks.turntable.TurntableBlock;
import com.bwt.items.BwtItems;
import com.bwt.utils.DyeUtils;
import com.bwt.utils.Id;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.BlockHalf;
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
        generateGrothedNetherrack(blockStateModelGenerator);
        generateDirtAndGrassSlab(blockStateModelGenerator);
        generateCompanionBlocks(blockStateModelGenerator);
        generateBloodWoodBlocks(blockStateModelGenerator);
        generateStokedFireBlock(blockStateModelGenerator);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.sawBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(BwtBlocks.sawBlock)
                        )
                ).coordinate(createUpDefaultRotationStates())
        );
        BwtBlocks.sidingBlocks.forEach(sidingBlock -> generateSidingBlock(blockStateModelGenerator, sidingBlock));
        BwtBlocks.mouldingBlocks.forEach(mouldingBlock -> generateMouldingBlock(blockStateModelGenerator, mouldingBlock));
        BwtBlocks.cornerBlocks.forEach(cornerBlock -> generateCornerBlock(blockStateModelGenerator, cornerBlock));
        BwtBlocks.columnBlocks.forEach(columnBlock -> generateColumnBlock(blockStateModelGenerator, columnBlock));
        BwtBlocks.pedestalBlocks.forEach(pedestalBlock -> generatePedestalBlock(blockStateModelGenerator, pedestalBlock));
        BwtBlocks.tableBlocks.forEach(tableBlock -> generateTableBlock(blockStateModelGenerator, tableBlock));
        BwtBlocks.vaseBlocks.values().forEach(vaseBlock -> generateVaseBlock(blockStateModelGenerator, vaseBlock));
        BwtBlocks.woolSlabBlocks.forEach((dyeColor, woolSlab) -> generateWoolSlab(blockStateModelGenerator, dyeColor, woolSlab));
        blockStateModelGenerator.registerStraightRail(BwtBlocks.stoneDetectorRailBlock);
        blockStateModelGenerator.registerStraightRail(BwtBlocks.obsidianDetectorRailBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.grateBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.slatsBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.wickerPaneBlock);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.cauldronBlock,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.cauldronBlock))
                ).coordinate(BlockStateVariantMap.create(AbstractCookingPotBlock.TIP_DIRECTION)
                        .register(Direction.UP, BlockStateVariant.create())
                        .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
                        .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                )
        );
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.crucibleBlock,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.crucibleBlock))
                ).coordinate(BlockStateVariantMap.create(AbstractCookingPotBlock.TIP_DIRECTION)
                        .register(Direction.UP, BlockStateVariant.create())
                        .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
                        .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                )
        );
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
        Identifier buddyBlockModelId = TexturedModel.makeFactory(block -> TextureMap.sideFrontTop(block).put(TextureKey.TOP, TextureMap.getSubId(block, "_side")), Models.ORIENTABLE)
                .upload(BwtBlocks.buddyBlock, blockStateModelGenerator.modelCollector);
        Identifier buddyBlockPoweredModelId = TexturedModel.makeFactory(block -> new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side_powered")).put(TextureKey.FRONT, TextureMap.getSubId(block, "_front_powered")).put(TextureKey.TOP, TextureMap.getSubId(block, "_side_powered")), Models.ORIENTABLE)
                .upload(BwtBlocks.buddyBlock, "_powered", blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.buddyBlock, BlockStateVariant.create().put(VariantSettings.MODEL, buddyBlockModelId))
                        .coordinate(BlockStateModelGenerator.createBooleanModelMap(BuddyBlock.POWERED, buddyBlockPoweredModelId, buddyBlockModelId))
                        .coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.soapBlock, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.makeFactory(block -> TextureMap.sideFrontTop(block).put(TextureKey.TOP, TextureMap.getSubId(block, "_side")), Models.ORIENTABLE).upload(BwtBlocks.soapBlock, blockStateModelGenerator.modelCollector)))
                        .coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        blockStateModelGenerator.registerSingleton(BwtBlocks.ropeCoilBlock, TexturedModel.CUBE_COLUMN);
        blockStateModelGenerator.registerSingleton(BwtBlocks.paddingBlock, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(BwtBlocks.wickerBlock, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(BwtBlocks.dungBlock, ModelIds.getBlockModelId(BwtBlocks.dungBlock)));
        blockStateModelGenerator.registerSingleton(BwtBlocks.concentratedHellfireBlock, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(
                BwtBlocks.wickerSlabBlock,
                Models.SLAB.upload(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE_ALL.get(BwtBlocks.wickerBlock).getTextures(), blockStateModelGenerator.modelCollector),
                Models.SLAB_TOP.upload(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE_ALL.get(BwtBlocks.wickerBlock).getTextures(), blockStateModelGenerator.modelCollector),
                ModelIds.getBlockModelId(BwtBlocks.wickerBlock)
        ));
        generateMiningChargeBlock(blockStateModelGenerator);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier
                .create(BwtBlocks.vineTrapBlock)
                .coordinate(
                        BlockStateVariantMap.create(VineTrapBlock.HALF)
                                .register(BlockHalf.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.vineTrapBlock)))
                                .register(BlockHalf.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.vineTrapBlock)).put(VariantSettings.X, VariantSettings.Rotation.R180))
                )
        );
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier
                .create(BwtBlocks.lensBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                TexturedModel.makeFactory(TextureMap::sideFrontBack, Models.TEMPLATE_COMMAND_BLOCK)
                                        .upload(BwtBlocks.lensBlock, blockStateModelGenerator.modelCollector)
                        )
                ).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        generateDebugLensBeam(blockStateModelGenerator);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.aqueductBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                Models.CUBE_BOTTOM_TOP.upload(
                                        BwtBlocks.aqueductBlock,
                                        TexturedModel.CUBE_BOTTOM_TOP.get(BwtBlocks.aqueductBlock).getTextures(),
                                        blockStateModelGenerator.modelCollector
                                )
                        )
                )
        );

        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.aqueductBlock, ModelIds.getBlockModelId(BwtBlocks.aqueductBlock));
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
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.lensBlock, ModelIds.getBlockModelId(BwtBlocks.lensBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.lightBlockBlock, ModelIds.getBlockModelId(BwtBlocks.lightBlockBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.millStoneBlock, ModelIds.getBlockModelId(BwtBlocks.millStoneBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.obsidianPressurePlateBlock, ModelIds.getBlockModelId(BwtBlocks.obsidianPressurePlateBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.pulleyBlock, ModelIds.getBlockModelId(BwtBlocks.pulleyBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.turntableBlock, ModelIds.getBlockSubModelId(BwtBlocks.turntableBlock, "_0"));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.bellowsBlock, ModelIds.getBlockModelId(BwtBlocks.bellowsBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredCrucibleBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredCrucibleBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredPlanterBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredPlanterBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredVaseBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredVaseBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.unfiredUrnBlock, ModelIds.getBlockModelId(BwtBlocks.unfiredUrnBlock));
        blockStateModelGenerator.registerParentedItemModel(BwtBlocks.vineTrapBlock, ModelIds.getBlockModelId(BwtBlocks.vineTrapBlock));
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
        itemModelGenerator.register(BwtItems.friedEggItem, Models.GENERATED);
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
        itemModelGenerator.register(BwtItems.poachedEggItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.potashItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.rawEggItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.redstoneEyeItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.netheriteMattockItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.netheriteBattleAxeItem, Models.HANDHELD);
        itemModelGenerator.register(BwtItems.ropeItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.rottedArrowItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.sawDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.sailItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.scouredLeatherItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.screwItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soapItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soulDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.soulUrnItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.strapItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.tallowItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.tannedLeatherItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.waterWheelItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.windmillItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.wolfChopItem, Items.PORKCHOP, Models.GENERATED);
        itemModelGenerator.register(BwtItems.woodBladeItem, Models.GENERATED);
    }

    private void generateBloodWoodBlocks(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerLog(BwtBlocks.bloodWoodBlocks.logBlock).log(BwtBlocks.bloodWoodBlocks.logBlock).wood(BwtBlocks.bloodWoodBlocks.woodBlock);
        blockStateModelGenerator.registerLog(BwtBlocks.bloodWoodBlocks.strippedLogBlock).log(BwtBlocks.bloodWoodBlocks.strippedLogBlock).wood(BwtBlocks.bloodWoodBlocks.strippedWoodBlock);
        blockStateModelGenerator.registerSingleton(BwtBlocks.bloodWoodBlocks.leavesBlock, TexturedModel.LEAVES);
        blockStateModelGenerator.registerFlowerPotPlant(BwtBlocks.bloodWoodBlocks.saplingBlock, BwtBlocks.bloodWoodBlocks.pottedSaplingBlock, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerCubeAllModelTexturePool(BwtBlocks.bloodWoodBlocks.blockFamily.getBaseBlock()).family(BwtBlocks.bloodWoodBlocks.blockFamily);
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

    private void generateStokedFireBlock(BlockStateModelGenerator blockStateModelGenerator) {
        Model tallFireFloorTemplate = new Model(Optional.of(Id.of("block/template_tall_fire_floor")), Optional.empty(), TextureKey.FIRE);
        Model shortFireFloorTemplate = new Model(Optional.of(Id.of("block/template_short_fire_floor")), Optional.empty(), TextureKey.FIRE);
        Model tallFireSideTemplate = new Model(Optional.of(Id.of("block/template_tall_fire_side")), Optional.empty(), TextureKey.FIRE);
        Model shortFireSideTemplate = new Model(Optional.of(Id.of("block/template_short_fire_side")), Optional.empty(), TextureKey.FIRE);
        Model tallFireSideAltTemplate = new Model(Optional.of(Id.of("block/template_tall_fire_side_alt")), Optional.empty(), TextureKey.FIRE);
        Model shortFireSideAltTemplate = new Model(Optional.of(Id.of("block/template_short_fire_side_alt")), Optional.empty(), TextureKey.FIRE);
        TextureMap short0 = new TextureMap().put(TextureKey.FIRE, TextureMap.getSubId(BwtBlocks.stokedFireBlock, "_short_0"));
        TextureMap short1 = new TextureMap().put(TextureKey.FIRE, TextureMap.getSubId(BwtBlocks.stokedFireBlock, "_short_1"));
        List<Identifier> tallFloorIdentifiers = ImmutableList.of(
                tallFireFloorTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_tall_floor0"), TextureMap.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelCollector),
                tallFireFloorTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_tall_floor1"), TextureMap.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelCollector)
        );
        List<Identifier> shortFloorIdentifiers = ImmutableList.of(
                tallFireFloorTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_short_floor0"), short0, blockStateModelGenerator.modelCollector),
                tallFireFloorTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_short_floor1"), short1, blockStateModelGenerator.modelCollector)
        );
        List<Identifier> tallSideIdentifiers = ImmutableList.of(
                tallFireSideTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_tall_side0"), TextureMap.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelCollector),
                tallFireSideTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_tall_side1"), TextureMap.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelCollector),
                tallFireSideAltTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_tall_side_alt0"), TextureMap.fire0(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelCollector),
                tallFireSideAltTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_tall_side_alt1"), TextureMap.fire1(BwtBlocks.stokedFireBlock), blockStateModelGenerator.modelCollector)
        );
        List<Identifier> shortSideIdentifiers = ImmutableList.of(
                tallFireSideTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_short_side0"), short0, blockStateModelGenerator.modelCollector),
                tallFireSideTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_short_side1"), short1, blockStateModelGenerator.modelCollector),
                tallFireSideAltTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_short_side_alt0"), short0, blockStateModelGenerator.modelCollector),
                tallFireSideAltTemplate.upload(ModelIds.getBlockSubModelId(BwtBlocks.stokedFireBlock, "_short_side_alt1"), short1, blockStateModelGenerator.modelCollector)
        );
        When whenShort = When.create().set(StokedFireBlock.TWO_HIGH, false);
        When whenTall = When.create().set(StokedFireBlock.TWO_HIGH, true);
        blockStateModelGenerator.blockStateCollector.accept(MultipartBlockStateSupplier.create(BwtBlocks.stokedFireBlock)
                .with(whenShort, BlockStateModelGenerator.buildBlockStateVariants(shortFloorIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenTall, BlockStateModelGenerator.buildBlockStateVariants(tallFloorIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenShort, BlockStateModelGenerator.buildBlockStateVariants(shortSideIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenTall, BlockStateModelGenerator.buildBlockStateVariants(tallSideIdentifiers, blockStateVariant -> blockStateVariant))
                .with(whenShort, BlockStateModelGenerator.buildBlockStateVariants(shortSideIdentifiers, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90)))
                .with(whenTall, BlockStateModelGenerator.buildBlockStateVariants(tallSideIdentifiers, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R90)))
                .with(whenShort, BlockStateModelGenerator.buildBlockStateVariants(shortSideIdentifiers, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180)))
                .with(whenTall, BlockStateModelGenerator.buildBlockStateVariants(tallSideIdentifiers, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R180)))
                .with(whenShort, BlockStateModelGenerator.buildBlockStateVariants(shortSideIdentifiers, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270)))
                .with(whenTall, BlockStateModelGenerator.buildBlockStateVariants(tallSideIdentifiers, blockStateVariant -> blockStateVariant.put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void generateMiningChargeBlock(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.miningChargeBlock, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.miningChargeBlock)))
                        .coordinate(BlockStateVariantMap
                                .create(Properties.BLOCK_FACE, Properties.HORIZONTAL_FACING)
                                .register(BlockFace.CEILING, Direction.NORTH, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                                .register(BlockFace.CEILING, Direction.EAST, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270))
                                .register(BlockFace.CEILING, Direction.SOUTH, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R180))
                                .register(BlockFace.CEILING, Direction.WEST, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R180)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                                .register(BlockFace.FLOOR, Direction.NORTH, BlockStateVariant.create())
                                .register(BlockFace.FLOOR, Direction.EAST, BlockStateVariant.create()
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                                .register(BlockFace.FLOOR, Direction.SOUTH, BlockStateVariant.create()
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                                .register(BlockFace.FLOOR, Direction.WEST, BlockStateVariant.create()
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270))
                                .register(BlockFace.WALL, Direction.NORTH, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R90))
                                .register(BlockFace.WALL, Direction.EAST, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                                .register(BlockFace.WALL, Direction.SOUTH, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                                .register(BlockFace.WALL, Direction.WEST, BlockStateVariant.create()
                                        .put(VariantSettings.X, VariantSettings.Rotation.R90)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        )
        );
    }

    public static void generateDebugLensBeam(BlockStateModelGenerator blockStateModelGenerator) {
        LensBeamBlock beam = BwtBlocks.lensBeamBlock;
        Identifier identifier = ModelIds.getBlockModelId(beam);
        blockStateModelGenerator.blockStateCollector.accept(
                MultipartBlockStateSupplier
                        .create(beam)
                        .with(
                                When.create().set(LensBeamBlock.NORTH, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier)
                        ).with(
                                When.create().set(LensBeamBlock.EAST, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                        ).with(
                                When.create().set(LensBeamBlock.SOUTH, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R180)
                        ).with(
                                When.create().set(LensBeamBlock.WEST, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                        ).with(
                                When.create().set(LensBeamBlock.DOWN, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R90)
                        ).with(
                                When.create().set(LensBeamBlock.UP, true),
                                BlockStateVariant.create().put(VariantSettings.MODEL, identifier).put(VariantSettings.X, VariantSettings.Rotation.R270)
                        )
        );
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
        Model model = new Model(Optional.of(Id.of("block/siding")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
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
        Model horizontalModel = new Model(Optional.of(Id.of("block/moulding")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        Model verticalModel = new Model(Optional.of(Id.of("block/moulding_vertical")), Optional.of("_vertical"), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        Identifier horizontalId = horizontalModel.upload(mouldingBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        Identifier verticalId = verticalModel.upload(mouldingBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(mouldingBlock).coordinate(createMouldingOrientationMap(horizontalId, verticalId)));
        blockStateModelGenerator.registerParentedItemModel(mouldingBlock, horizontalId);
    }

    public static void generateCornerBlock(BlockStateModelGenerator blockStateModelGenerator, CornerBlock cornerBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(cornerBlock.fullBlock);
        Model model = new Model(Optional.of(Id.of("block/corner")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
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

    public static void generateColumnBlock(BlockStateModelGenerator blockStateModelGenerator, ColumnBlock columnBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(columnBlock.fullBlock);
        Model model = new Model(Optional.of(Id.of("block/column")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        model.upload(columnBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(columnBlock, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(columnBlock)).put(VariantSettings.UVLOCK, true)));
        blockStateModelGenerator.registerParentedItemModel(columnBlock, ModelIds.getBlockModelId(columnBlock));
    }

    public static void generatePedestalBlock(BlockStateModelGenerator blockStateModelGenerator, PedestalBlock pedestalBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(pedestalBlock.fullBlock);
        Model model = new Model(Optional.of(Id.of("block/pedestal")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        model.upload(pedestalBlock, texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        pedestalBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(pedestalBlock)
                        ).put(VariantSettings.UVLOCK, true)
                ).coordinate(BlockStateVariantMap.create(PedestalBlock.VERTICAL_DIRECTION)
                        .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                        .register(Direction.UP, BlockStateVariant.create())
                )
        );
        blockStateModelGenerator.registerParentedItemModel(pedestalBlock, ModelIds.getBlockModelId(pedestalBlock));
    }

    public static void generateTableBlock(BlockStateModelGenerator blockStateModelGenerator, TableBlock tableBlock) {
        TexturedModel texturedModel = TexturedModel.CUBE_ALL.get(tableBlock.fullBlock);
        Model tableModel = new Model(Optional.of(Id.of("block/table")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        Model tableNoSupportModel = new Model(Optional.of(Id.of("block/table_no_support")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
        Identifier tableModelId = tableModel.upload(ModelIds.getBlockModelId(tableBlock), texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        Identifier noSupportModelId = tableNoSupportModel.upload(ModelIds.getBlockSubModelId(tableBlock, "_no_support"), texturedModel.getTextures(), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(tableBlock)
                        .coordinate(BlockStateVariantMap.create(TableBlock.SUPPORT)
                                .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, tableModelId))
                                .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, noSupportModelId))
                        )
        );
    }

    public void generateVaseBlock(BlockStateModelGenerator blockStateModelGenerator, VaseBlock vaseBlock) {
        Identifier modelId = new Model(
                Optional.of(Id.of("block/vase")),
                Optional.empty(),
                TextureKey.PARTICLE,
                TextureKey.TOP,
                TextureKey.SIDE,
                TextureKey.BOTTOM
        ).upload(
                ModelIds.getBlockModelId(vaseBlock),
                TextureMap.sideTopBottom(vaseBlock).put(TextureKey.PARTICLE, TextureMap.getSubId(vaseBlock, "_side")
        ), blockStateModelGenerator.modelCollector);
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

    public void generateGrothedNetherrack(BlockStateModelGenerator blockStateModelGenerator) {
        TexturedModel netherrackTexturedModel = TexturedModel.CUBE_ALL.get(Blocks.NETHERRACK);
        Identifier netherrackTexture = netherrackTexturedModel.getTextures().getTexture(TextureKey.ALL);
        TextureMap grothedNetherrackTextureMap = new TextureMap()
                .put(TextureKey.SIDE, TextureMap.getSubId(BwtBlocks.grothedNetherrackBlock, "_side"))
                .put(TextureKey.TOP, TextureMap.getSubId(BwtBlocks.grothedNetherrackBlock, "_top"))
                .put(TextureKey.BOTTOM, netherrackTexture);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.grothedNetherrackBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                Models.CUBE_BOTTOM_TOP.upload(
                                        BwtBlocks.grothedNetherrackBlock,
                                        grothedNetherrackTextureMap,
                                        blockStateModelGenerator.modelCollector
                                )
                        )
                )
        );
    }

    public void generateDirtAndGrassSlab(BlockStateModelGenerator blockStateModelGenerator) {
        //Dirt Slab
        TexturedModel dirtTexturedModel = TexturedModel.CUBE_ALL.get(Blocks.DIRT);
        Identifier dirtTexture = dirtTexturedModel.getTextures().getTexture(TextureKey.ALL);
        Identifier dirtSlab = TexturedModel.makeFactory(
                block -> new TextureMap()
                        .put(TextureKey.BOTTOM, dirtTexture)
                        .put(TextureKey.TOP, dirtTexture)
                        .put(TextureKey.SIDE, dirtTexture),
                Models.SLAB
        ).upload(BwtBlocks.dirtSlabBlock, blockStateModelGenerator.modelCollector);
        Identifier snowyDirtSlab = Id.of("block/snowy_dirt_slab");
        Identifier dirtPathSlab = Id.of("block/dirt_path_slab");
        Identifier grassSlab = Id.of("block/grass_slab");
        Identifier snowyGrassSlab = Id.of("block/snowy_grass_slab");
        Identifier myceliumSlab = Id.of("block/mycelium_slab");
        Identifier snowyMyceliumSlab = Id.of("block/snowy_mycelium_slab");
        Identifier podzolSlab = Id.of("block/podzol_slab");
        Identifier snowyPodzolSlab = Id.of("block/snowy_podzol_slab");

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.dirtSlabBlock)
                        .coordinate(
                                BlockStateVariantMap.create(DirtSlabBlock.SNOWY)
                                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, snowyDirtSlab))
                                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, dirtSlab))
                        )

        );

        //Grass Slab
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.grassSlabBlock)
                        .coordinate(
                                BlockStateVariantMap.create(DirtSlabBlock.SNOWY)
                                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, snowyGrassSlab))
                                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, grassSlab))
                        )
        );
        //Mycelium Slab
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.myceliumSlabBlock)
                        .coordinate(
                                BlockStateVariantMap.create(DirtSlabBlock.SNOWY)
                                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, snowyMyceliumSlab))
                                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, myceliumSlab))
                        )
        );
        //Podzol Slab
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.podzolSlabBlock)
                        .coordinate(
                                BlockStateVariantMap.create(DirtSlabBlock.SNOWY)
                                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, snowyPodzolSlab))
                                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, podzolSlab))
                        )
        );
        //Dirt Path Slab
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(BwtBlocks.dirtPathSlabBlock, BlockStateVariant.create().put(VariantSettings.MODEL, dirtPathSlab)));
    }

}

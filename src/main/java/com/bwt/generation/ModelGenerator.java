package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModelGenerator extends FabricModelProvider {
    public ModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TexturedModel.ORIENTABLE.upload(BwtBlocks.companionCubeBlock, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.companionCubeBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(BwtBlocks.companionCubeBlock)
                        )
                ).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        BwtBlocks.sawBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                ModelIds.getBlockModelId(BwtBlocks.sawBlock)
                        )
                ).coordinate(createUpDefaultRotationStates())
        );
        blockStateModelGenerator.registerStraightRail(BwtBlocks.stoneDetectorRailBlock);
        blockStateModelGenerator.registerStraightRail(BwtBlocks.obsidianDetectorRailBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.grateBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.slatsBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.wickerBlock);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(BwtItems.cementBucketItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.cookedWolfChopItem, Items.COOKED_PORKCHOP, Models.GENERATED);
        itemModelGenerator.register(BwtItems.donutItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.flourItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.gearItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hempItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hempFiberItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.hempSeedsItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.ropeItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.sawDustItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.scouredLeatherItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.strapItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.waterWheelItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.windmillItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.wolfChopItem, Items.PORKCHOP, Models.GENERATED);
    }

    protected void generatePaneBlock(BlockStateModelGenerator blockStateModelGenerator, Block pane) {
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

    public static BlockStateVariantMap createUpDefaultRotationStates() {
        return BlockStateVariantMap.create(Properties.FACING)
                .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                .register(Direction.UP, BlockStateVariant.create())
                .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }
}

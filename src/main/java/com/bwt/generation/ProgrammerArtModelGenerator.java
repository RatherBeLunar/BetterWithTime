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

public class ProgrammerArtModelGenerator extends ModelGenerator {
    public ProgrammerArtModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(BwtBlocks.concentratedHellfireBlock, TexturedModel.CUBE_ALL);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.grateBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.slatsBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.wickerPaneBlock);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier
                .create(BwtBlocks.lensBlock,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                TexturedModel.makeFactory(TextureMap::sideFrontBack, Models.TEMPLATE_COMMAND_BLOCK)
                                        .upload(BwtBlocks.lensBlock, blockStateModelGenerator.modelCollector)
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
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(BwtBlocks.soapBlock, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.makeFactory(block -> TextureMap.sideFrontTop(block).put(TextureKey.TOP, TextureMap.getSubId(block, "_side")), Models.ORIENTABLE).upload(BwtBlocks.soapBlock, blockStateModelGenerator.modelCollector)))
                        .coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier
                .create(BwtBlocks.vineTrapBlock)
                .coordinate(
                        BlockStateVariantMap.create(VineTrapBlock.HALF)
                                .register(BlockHalf.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.vineTrapBlock)))
                                .register(BlockHalf.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(BwtBlocks.vineTrapBlock)).put(VariantSettings.X, VariantSettings.Rotation.R180))
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
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(
                BwtBlocks.wickerSlabBlock,
                Models.SLAB.upload(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE_ALL.get(BwtBlocks.wickerBlock).getTextures(), blockStateModelGenerator.modelCollector),
                Models.SLAB_TOP.upload(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE_ALL.get(BwtBlocks.wickerBlock).getTextures(), blockStateModelGenerator.modelCollector),
                ModelIds.getBlockModelId(BwtBlocks.wickerBlock)
        ));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    }
}

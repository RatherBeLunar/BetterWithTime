package com.bwt.generation;

import com.bwt.blocks.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Half;

public class ProgrammerArtModelGenerator extends ModelGenerator {
    public ProgrammerArtModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        generateGrothedNetherrack(blockStateModelGenerator);
        blockStateModelGenerator.createTrivialBlock(BwtBlocks.concentratedHellfireBlock, TexturedModel.CUBE);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.grateBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.slatsBlock);
        generatePaneBlock(blockStateModelGenerator, BwtBlocks.wickerPaneBlock);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(BwtBlocks.lensBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                TexturedModel.createDefault(TextureMapping::commandBlock, ModelTemplates.COMMAND_BLOCK)
                                        .create(BwtBlocks.lensBlock, blockStateModelGenerator.modelOutput)
                        )
                ).with(BlockModelGenerators.createFacingDispatch())
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.sawBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelLocationUtils.getModelLocation(BwtBlocks.sawBlock)
                        )
                ).with(createUpDefaultRotationStates())
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(BwtBlocks.soapBlock, Variant.variant().with(VariantProperties.MODEL, TexturedModel.createDefault(block -> TextureMapping.orientableCubeOnlyTop(block).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side")), ModelTemplates.CUBE_ORIENTABLE).create(BwtBlocks.soapBlock, blockStateModelGenerator.modelOutput)))
                        .with(BlockModelGenerators.createFacingDispatch())
        );
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(BwtBlocks.vineTrapBlock)
                .with(
                        PropertyDispatch.property(VineTrapBlock.HALF)
                                .select(Half.BOTTOM, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.vineTrapBlock)))
                                .select(Half.TOP, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(BwtBlocks.vineTrapBlock)).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                )
        );
        TexturedModel.ORIENTABLE.create(BwtBlocks.bellowsBlock, blockStateModelGenerator.modelOutput);
        ModelTemplates.SLAB_BOTTOM.create(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE.get(BwtBlocks.wickerBlock).getMapping(), blockStateModelGenerator.modelOutput);
        ModelTemplates.SLAB_TOP.create(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE.get(BwtBlocks.wickerBlock).getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(BwtBlocks.kilnBlock, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BRICKS))));
    }

    public void generateGrothedNetherrack(BlockModelGenerators blockStateModelGenerator) {
        TexturedModel netherrackTexturedModel = TexturedModel.CUBE.get(Blocks.NETHERRACK);
        ResourceLocation netherrackTexture = netherrackTexturedModel.getMapping().get(TextureSlot.ALL);
        TextureMapping grothedNetherrackTextureMap = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BwtBlocks.grothedNetherrackBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(BwtBlocks.grothedNetherrackBlock, "_top"))
                .put(TextureSlot.BOTTOM, netherrackTexture);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        BwtBlocks.grothedNetherrackBlock,
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                ModelTemplates.CUBE_BOTTOM_TOP.create(
                                        BwtBlocks.grothedNetherrackBlock,
                                        grothedNetherrackTextureMap,
                                        blockStateModelGenerator.modelOutput
                                )
                        )
                )
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}

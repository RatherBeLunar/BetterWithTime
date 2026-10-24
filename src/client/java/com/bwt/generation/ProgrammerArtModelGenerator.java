package com.bwt.generation;

import com.bwt.blocks.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.Identifier;
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
                .dispatch(BwtBlocks.lensBlock,
                        plainVariant(
                                TexturedModel.createDefault(TextureMapping::commandBlock, ModelTemplates.COMMAND_BLOCK)
                                        .create(BwtBlocks.lensBlock, blockStateModelGenerator.modelOutput)
                        )
                ).with(NORTH_DEFAULT_ROTATION)
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.sawBlock,
                        plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.sawBlock))
                ).with(createUpDefaultRotationStates())
        );
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(BwtBlocks.soapBlock, plainVariant(TexturedModel.createDefault(block -> TextureMapping.orientableCubeOnlyTop(block).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_side")), ModelTemplates.CUBE_ORIENTABLE).create(BwtBlocks.soapBlock, blockStateModelGenerator.modelOutput)))
                        .with(NORTH_DEFAULT_ROTATION)
        );
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(BwtBlocks.vineTrapBlock)
                .with(
                        PropertyDispatch.initial(VineTrapBlock.HALF)
                                .select(Half.BOTTOM, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.vineTrapBlock)))
                                .select(Half.TOP, plainVariant(ModelLocationUtils.getModelLocation(BwtBlocks.vineTrapBlock)).with(BlockModelGenerators.X_ROT_180))
                )
        );
        TexturedModel.ORIENTABLE.create(BwtBlocks.bellowsBlock, blockStateModelGenerator.modelOutput);
        ModelTemplates.SLAB_BOTTOM.create(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE.get(BwtBlocks.wickerBlock).getMapping(), blockStateModelGenerator.modelOutput);
        ModelTemplates.SLAB_TOP.create(BwtBlocks.wickerSlabBlock, TexturedModel.CUBE.get(BwtBlocks.wickerBlock).getMapping(), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(BwtBlocks.kilnBlock, plainVariant(ModelLocationUtils.getModelLocation(Blocks.BRICKS))));
    }

    public void generateGrothedNetherrack(BlockModelGenerators blockStateModelGenerator) {
        TexturedModel netherrackTexturedModel = TexturedModel.CUBE.get(Blocks.NETHERRACK);
        Identifier netherrackTexture = netherrackTexturedModel.getMapping().get(TextureSlot.ALL);
        TextureMapping grothedNetherrackTextureMap = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(BwtBlocks.grothedNetherrackBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(BwtBlocks.grothedNetherrackBlock, "_top"))
                .put(TextureSlot.BOTTOM, netherrackTexture);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        BwtBlocks.grothedNetherrackBlock,
                        plainVariant(
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

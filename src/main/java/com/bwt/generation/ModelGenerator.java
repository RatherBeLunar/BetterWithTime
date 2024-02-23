package com.bwt.generation;

import com.bwt.blocks.BwtBlocks;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Items;

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
        itemModelGenerator.register(BwtItems.scouredLeatherItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.windmillItem, Models.GENERATED);
        itemModelGenerator.register(BwtItems.wolfChopItem, Items.PORKCHOP, Models.GENERATED);
    }
}

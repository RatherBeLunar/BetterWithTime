package com.bwt.features;

import com.bwt.blocks.blood_wood.BloodWoodLogBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.utils.Id;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import java.util.concurrent.CompletableFuture;



public class BwtConfiguredFeatures extends FabricDynamicRegistryProvider {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOD_WOOD_KEY = registerKey("blood_wood");

    public BwtConfiguredFeatures(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(BLOOD_WOOD_KEY,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeConfiguration.TreeConfigurationBuilder(
                                BlockStateProvider.simple(BwtBlocks.bloodWoodBlocks.logBlock.defaultBlockState().setValue(BloodWoodLogBlock.CAN_GROW, true)),
                                new BloodWoodTrunkPlacer(4, 2, 0),
                                BlockStateProvider.simple(BwtBlocks.bloodWoodBlocks.leavesBlock),
                                new BlobFoliagePlacer(
                                        ConstantInt.of(2),
                                        ConstantInt.of(0),
                                        3
                                ),
                                new TwoLayersFeatureSize(1, 0, 1)
                        ).dirt(BlockStateProvider.simple(Blocks.SOUL_SOIL)).ignoreVines().build()
                )
        );
    }

    @Override
    public String getName() {
        return "bwt_features";
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Id.of(name));
    }
}

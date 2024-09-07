package com.bwt.features;

import java.util.OptionalInt;

import com.bwt.blocks.BloodWoodLogBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.utils.Id;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;



public class BwtConfiguredFeatures extends FabricDynamicRegistryProvider {
    public static final RegistryKey<ConfiguredFeature<?, ?>> BLOOD_WOOD_KEY = registerKey("blood_wood");

    public BwtConfiguredFeatures(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.add(BLOOD_WOOD_KEY,
                new ConfiguredFeature<>(
                        Feature.TREE,
                        new TreeFeatureConfig.Builder(
                                BlockStateProvider.of(BwtBlocks.bloodWoodBlocks.logBlock.getDefaultState().with(BloodWoodLogBlock.CAN_GROW, true)),
                                new BloodWoodTrunkPlacer(4, 2, 0),
                                BlockStateProvider.of(BwtBlocks.bloodWoodBlocks.leavesBlock),
                                new BlobFoliagePlacer(
                                        ConstantIntProvider.create(2),
                                        ConstantIntProvider.create(0),
                                        3
                                ),
                                new TwoLayersFeatureSize(1, 0, 1)
                        ).dirtProvider(BlockStateProvider.of(Blocks.SOUL_SOIL)).ignoreVines().build()
                )
        );
    }

    @Override
    public String getName() {
        return "bwt_features";
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Id.of(name));
    }
}

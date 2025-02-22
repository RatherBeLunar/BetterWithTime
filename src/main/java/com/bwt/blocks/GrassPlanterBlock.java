package com.bwt.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

import java.util.List;
import java.util.Optional;

public class GrassPlanterBlock extends PlanterBlock {
    public GrassPlanterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return PlanterBlock.flatTopOutlineShape;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        BlockPos abovePlanterPos = pos.up();
        BlockState shortGrassState = Blocks.SHORT_GRASS.getDefaultState();
        Optional<RegistryEntry.Reference<PlacedFeature>> optionalPlacedFeature = world.getRegistryManager().get(RegistryKeys.PLACED_FEATURE).getEntry(VegetationPlacedFeatures.GRASS_BONEMEAL);
        block0: for (int i = 0; i < 128; ++i) {
            RegistryEntry<PlacedFeature> registryEntry;
            BlockPos aboveNeighborPos = abovePlanterPos;
            for (int j = 0; j < i / 16; ++j) {
                aboveNeighborPos = aboveNeighborPos.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!world.getBlockState(aboveNeighborPos.down()).isOf(this) || world.getBlockState(aboveNeighborPos).isFullCube(world, aboveNeighborPos)) {
                    continue block0;
                }
            }
            BlockState aboveNeighborState = world.getBlockState(aboveNeighborPos);
            if (aboveNeighborState.isOf(shortGrassState.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable) shortGrassState.getBlock()).grow(world, random, aboveNeighborPos, aboveNeighborState);
            }
            if (!aboveNeighborState.isIn(BlockTags.AIR)) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = world.getBiome(aboveNeighborPos).value().getGenerationSettings().getFlowerFeatures();
                if (list.isEmpty()) continue;
                registryEntry = ((RandomPatchFeatureConfig)list.get(0).config()).feature();
            } else {
                if (optionalPlacedFeature.isEmpty()) continue;
                registryEntry = optionalPlacedFeature.get();
            }
            registryEntry.value().generateUnregistered(world, world.getChunkManager().getChunkGenerator(), random, aboveNeighborPos);
        }
    }
}

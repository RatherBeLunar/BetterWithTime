package com.bwt.blocks;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrassPlanterBlock extends PlanterBlock {
    public GrassPlanterBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PlanterBlock.flatTopOutlineShape;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        BlockPos abovePlanterPos = pos.above();
        BlockState shortGrassState = Blocks.SHORT_GRASS.defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> optionalPlacedFeature = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);
        block0: for (int i = 0; i < 128; ++i) {
            Holder<PlacedFeature> registryEntry;
            BlockPos aboveNeighborPos = abovePlanterPos;
            for (int j = 0; j < i / 16; ++j) {
                aboveNeighborPos = aboveNeighborPos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!level.getBlockState(aboveNeighborPos.below()).is(this) || level.getBlockState(aboveNeighborPos).isCollisionShapeFullBlock(level, aboveNeighborPos)) {
                    continue block0;
                }
            }
            BlockState aboveNeighborState = level.getBlockState(aboveNeighborPos);
            if (aboveNeighborState.is(shortGrassState.getBlock()) && random.nextInt(10) == 0) {
                ((BonemealableBlock) shortGrassState.getBlock()).performBonemeal(level, random, aboveNeighborPos, aboveNeighborState);
            }
            if (!aboveNeighborState.is(BlockTags.AIR)) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = level.getBiome(aboveNeighborPos).value().getGenerationSettings().getFlowerFeatures();
                if (list.isEmpty()) continue;
                registryEntry = ((RandomPatchConfiguration)list.get(0).config()).feature();
            } else {
                if (optionalPlacedFeature.isEmpty()) continue;
                registryEntry = optionalPlacedFeature.get();
            }
            registryEntry.value().place(level, level.getChunkSource().getGenerator(), random, aboveNeighborPos);
        }
    }
}

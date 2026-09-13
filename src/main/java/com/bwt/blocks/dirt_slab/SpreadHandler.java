package com.bwt.blocks.dirt_slab;

import com.bwt.blocks.BwtBlocks;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import java.util.Map;

public class SpreadHandler {


    @FunctionalInterface
    interface Spreader {
        void spread(Level level, BlockPos pos, BlockState state);
    }

    static final Map<Block, Map<Block, Spreader>> SOURCE_DEST_SPREADERS = ImmutableMap.<Block, Map<Block, Spreader>>builder()
            .put(Blocks.GRASS_BLOCK, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (level, pos, state) -> level.setBlockAndUpdate(pos, BwtBlocks.grassSlabBlock.defaultBlockState().setValue(DirtSlabBlock.SNOWY, state.getValue(DirtSlabBlock.SNOWY)))
                    ).build())
            .put(Blocks.MYCELIUM, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (level, pos, state) -> level.setBlockAndUpdate(pos, BwtBlocks.myceliumSlabBlock.defaultBlockState().setValue(DirtSlabBlock.SNOWY, state.getValue(DirtSlabBlock.SNOWY))))
                    .build())
            .put(BwtBlocks.grassSlabBlock, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (level, pos, state) -> level.setBlockAndUpdate(pos, BwtBlocks.grassSlabBlock.defaultBlockState().setValue(DirtSlabBlock.SNOWY, state.getValue(DirtSlabBlock.SNOWY))))
                    .put(Blocks.DIRT, (level, pos, state) -> level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState().setValue(GrassBlock.SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW))))
                    .build()
            )
            .put(BwtBlocks.myceliumSlabBlock, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (level, pos, state) -> level.setBlockAndUpdate(pos, BwtBlocks.myceliumSlabBlock.defaultBlockState().setValue(DirtSlabBlock.SNOWY, state.getValue(DirtSlabBlock.SNOWY))))
                    .put(Blocks.DIRT, (level, pos, state) -> level.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState().setValue(GrassBlock.SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW))))
                    .build()
            ).build();


    public static boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && (Integer) blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(level, state, pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(level, blockPos));
            return i < level.getMaxLightLevel();
        }
    }

    public static boolean canSpread(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.above();
        return canSurvive(state, level, pos) && !level.getFluidState(blockPos).is(FluidTags.WATER);
    }


    public static void randomTick(BlockState inputState, Level level, BlockPos pos, RandomSource random) {
        Block inputBlock = inputState.getBlock();
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            for (int i = 0; i < 4; ++i) {
                BlockPos destPost = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                BlockState destState = level.getBlockState(destPost);

                if (canSpread(inputState, level, destPost)) {
                    Map<Block, Spreader> DEST_SPREADER = SOURCE_DEST_SPREADERS.get(inputBlock);
                    if(DEST_SPREADER != null) {
                        Spreader spreader = DEST_SPREADER.get(destState.getBlock());
                        if (spreader != null) {
                            spreader.spread(level, destPost, destState);
                        }
                    }
                }
            }
        }
    }


}

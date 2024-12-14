package com.bwt.blocks.dirt_slab;

import com.bwt.blocks.BwtBlocks;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.Map;

public class SpreadHandler {


    @FunctionalInterface
    interface Spreader {
        void spread(World world, BlockPos pos, BlockState state);
    }

    static Map<Block, Map<Block, Spreader>> SOURCE_DEST_SPREADERS = ImmutableMap.<Block, Map<Block, Spreader>>builder()
            .put(Blocks.GRASS_BLOCK, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (world, pos, state) -> world.setBlockState(pos, BwtBlocks.grassSlabBlock.getDefaultState().with(DirtSlabBlock.SNOWY, state.get(DirtSlabBlock.SNOWY)))
                    ).build())
            .put(Blocks.MYCELIUM, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (world, pos, state) -> world.setBlockState(pos, BwtBlocks.myceliumSlabBlock.getDefaultState().with(DirtSlabBlock.SNOWY, state.get(DirtSlabBlock.SNOWY))))
                    .build())
            .put(BwtBlocks.grassSlabBlock, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (world, pos, state) -> world.setBlockState(pos, BwtBlocks.grassSlabBlock.getDefaultState().with(DirtSlabBlock.SNOWY, state.get(DirtSlabBlock.SNOWY))))
                    .put(Blocks.DIRT, (world, pos, state) -> world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState().with(GrassBlock.SNOWY, world.getBlockState(pos.up()).isOf(Blocks.SNOW))))
                    .build()
            )
            .put(BwtBlocks.myceliumSlabBlock, ImmutableMap.<Block, Spreader>builder()
                    .put(BwtBlocks.dirtSlabBlock, (world, pos, state) -> world.setBlockState(pos, BwtBlocks.myceliumSlabBlock.getDefaultState().with(DirtSlabBlock.SNOWY, state.get(DirtSlabBlock.SNOWY))))
                    .put(Blocks.DIRT, (world, pos, state) -> world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState().with(GrassBlock.SNOWY, world.getBlockState(pos.up()).isOf(Blocks.SNOW))))
                    .build()
            ).build();


    public static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW) && (Integer) blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getLevel() == 8) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
            return i < world.getMaxLightLevel();
        }
    }

    public static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return canSurvive(state, world, pos) && !world.getFluidState(blockPos).isIn(FluidTags.WATER);
    }


    public static void randomTick(BlockState inputState, World world, BlockPos pos, Random random) {
        Block inputBlock = inputState.getBlock();
        if (world.getLightLevel(pos.up()) >= 9) {
            for (int i = 0; i < 4; ++i) {
                BlockPos destPost = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                BlockState destState = world.getBlockState(destPost);

                if (canSpread(inputState, world, destPost)) {
                    Map<Block, Spreader> DEST_SPREADER = SOURCE_DEST_SPREADERS.get(inputBlock);
                    if(DEST_SPREADER != null) {
                        Spreader spreader = DEST_SPREADER.get(destState.getBlock());
                        if (spreader != null) {
                            spreader.spread(world, destPost, destState);
                        }
                    }
                }
            }
        }
    }


}

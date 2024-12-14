package com.bwt.blocks.dirt_slab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class GrassSlabBlock extends DirtSlabBlock {
    public GrassSlabBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        SpreadHandler.randomTick(state, world, pos, random);
        meltSnowFromLight(world, pos, state);
    }
}

package com.bwt.blocks.dirt_slab;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GrassSlabBlock extends DirtSlabBlock {
    public GrassSlabBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        SpreadHandler.randomTick(state, level, pos, random);
        meltSnowFromLight(level, pos, state);
    }
}

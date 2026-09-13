package com.bwt.blocks.dirt_slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumSlabBlock extends DirtSlabBlock {
    public MyceliumSlabBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        SpreadHandler.randomTick(state, level, pos, random);
        if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.1, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }
}

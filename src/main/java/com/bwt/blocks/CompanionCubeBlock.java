package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CompanionCubeBlock extends SimpleFacingBlock {

    public CompanionCubeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        addHearts(level, pos);
    }

    public void addHearts(Level level, BlockPos pos) {
        for( int tempCount = 0; tempCount < 7; tempCount++)
        {
            double d = level.random.nextGaussian() * 0.02D;
            double d1 = level.random.nextGaussian() * 0.02D;
            double d2 = level.random.nextGaussian() * 0.02D;

            level.addParticle(ParticleTypes.HEART,
                    (double) pos.getX() + level.random.nextDouble(),
                    (double)(pos.getY() + 1 ) + level.random.nextDouble(),
                    ((double)pos.getZ()) + level.random.nextDouble(),
                    d, d1, d2);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        level.playSound(null, pos, BwtSoundEvents.COMPANION_CUBE_WHINE, SoundSource.BLOCKS, 0.5f, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
    }
}

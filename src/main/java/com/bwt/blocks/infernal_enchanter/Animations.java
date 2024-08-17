package com.bwt.blocks.infernal_enchanter;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class Animations {

    @FunctionalInterface
    interface AnimationFrame {
        void animate(World world);
    }


    public record EntranceAnimationFrame(
            List<BlockPos> blockPosIterator) implements AnimationFrame {
        @Override
        public void animate(World world) {
            blockPosIterator.forEach(detectPos -> {
                BlockState detectState = world.getBlockState(detectPos);
                if(detectState.getBlock() == BwtBlocks.infernalEnchanterBlock) {
                    world.playSound(null, detectPos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.setBlockState(detectPos, detectState.with(InfernalEnchanterBlock.LIT, true), 11);
                    world.markDirty(detectPos);
                }
                if (detectState.isIn(BlockTags.CANDLES) && detectState.contains(CandleBlock.LIT)) {
                    world.playSound(null, detectPos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.setBlockState(detectPos, detectState.with(CandleBlock.LIT, true), 11);
                    world.markDirty(detectPos);

                }
            });
        }
    }

    public record LeavingAnimationFrame(List<BlockPos> blockPosIterator) implements AnimationFrame {
        @Override
        public void animate(World world) {
            blockPosIterator.forEach(detectPos -> {
                BlockState detectState = world.getBlockState(detectPos);
                if(detectState.getBlock() == BwtBlocks.infernalEnchanterBlock) {
                    world.playSound(null, detectPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.setBlockState(detectPos, detectState.with(InfernalEnchanterBlock.LIT, false), 11);
                    world.markDirty(detectPos);
                }

                if (detectState.isIn(BlockTags.CANDLES) && detectState.contains(CandleBlock.LIT)) {
                    world.setBlockState(detectPos, detectState.with(CandleBlock.LIT, false), 11);
                    world.markDirty(detectPos);
                }
            });
        }
    }

}

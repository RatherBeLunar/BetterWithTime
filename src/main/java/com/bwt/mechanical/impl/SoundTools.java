package com.bwt.mechanical.impl;

import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundTools {

    public static void playBangSound(World world, BlockPos pos, float volume, float pitch) {
        world.playSound(null, pos, BwtSoundEvents.MECH_BANG, SoundCategory.BLOCKS, volume, pitch);
    }

    public static void playBangSound(World world, BlockPos pos, float volume) {
        playBangSound(world, pos, volume, 1);
    }

    public static void playBangSound(World world, BlockPos pos) {
        playBangSound(world, pos, 0.5f);
    }

    public static void playCreakSound(World world, BlockPos pos, float volume) {
        world.playSound(null, pos, BwtSoundEvents.MECH_CREAK,
                SoundCategory.BLOCKS, volume, world.random.nextFloat() * 0.25F + 0.25F);
    }

    public static void playCreakSound(World world, BlockPos pos) {
        playCreakSound(world, pos, 0.25f);
    }

}

package com.bwt.blocks;

import com.bwt.blocks.axles.AxlePowerLevelGetter;
import com.bwt.blocks.axles.AxlePowerSourceBlock;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface MechPowerBlockBase {
    int turnOnTickRate = 10;
    int turnOffTickRate = 9;

    BooleanProperty MECH_POWERED = BooleanProperty.of("mech_powered");

    static int getTurnOnTickRate() { return turnOnTickRate; }
    static int getTurnOffTickRate() { return turnOffTickRate; }

    default void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED);
    }

    default boolean isMechPowered(BlockState blockState) {
        return blockState.get(MECH_POWERED);
    }

    default Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> true;
    }

    default Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> !direction.equals(Direction.DOWN);
    }

    default Stream<Direction> getPowerInputFaces(World world, BlockPos pos, BlockState blockState) {
        Predicate<Direction> axlePredicate = getValidAxleInputFaces(blockState, pos);
        Predicate<Direction> handCrankPredicate = getValidHandCrankFaces(blockState, pos);
        return Arrays.stream(Direction.values())
                .filter(direction -> {
                    BlockState inputBlockState = world.getBlockState(pos.offset(direction));
                    return (axlePredicate.test(direction) && inputBlockState.getBlock() instanceof AxlePowerLevelGetter axlePowerLevelGetter && axlePowerLevelGetter.getMechPowerForNeighbor(inputBlockState, direction.getAxis()) > 0)
                            || (handCrankPredicate.test(direction) && inputBlockState.getBlock() instanceof HandCrankBlock && HandCrankBlock.isPowered(inputBlockState));
                });
    }

    default boolean isReceivingMechPower(World world, BlockState blockState, BlockPos pos) {
        return getPowerInputFaces(world, pos, blockState).findAny().isPresent();
    }

    default boolean isOverPowered(World world, BlockState blockState, BlockPos pos) {
        return getPowerInputFaces(world, pos, blockState).limit(2).count() > 1;
    }

    default void playBangSound(World world, BlockPos pos, float volume, float pitch) {
        world.playSound(null, pos, BwtSoundEvents.MECH_BANG, SoundCategory.BLOCKS, volume, pitch);
    }

    default void playBangSound(World world, BlockPos pos, float volume) {
        playBangSound(world, pos, volume, 1);
    }

    default void playBangSound(World world, BlockPos pos) {
        playBangSound(world, pos, 0.5f);
    }

    default void playCreakSound(World world, BlockPos pos, float volume) {
        world.playSound(null, pos, BwtSoundEvents.MECH_CREAK,
                SoundCategory.BLOCKS, volume, world.random.nextFloat() * 0.25F + 0.25F);
    }

    default void playCreakSound(World world, BlockPos pos) {
        playCreakSound(world, pos, 0.25f);
    }
}

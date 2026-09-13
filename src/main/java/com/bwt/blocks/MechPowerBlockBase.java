package com.bwt.blocks;

import com.bwt.blocks.axles.AxlePowerLevelGetter;
import com.bwt.sounds.BwtSoundEvents;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface MechPowerBlockBase {
    int turnOnTickRate = 10;
    int turnOffTickRate = 9;

    BooleanProperty MECH_POWERED = BooleanProperty.create("mech_powered");

    static int getTurnOnTickRate() { return turnOnTickRate; }
    static int getTurnOffTickRate() { return turnOffTickRate; }

    default void appendProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED);
    }

    default boolean isMechPowered(BlockState blockState) {
        return blockState.getValue(MECH_POWERED);
    }

    default Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> true;
    }

    default Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> !direction.equals(Direction.DOWN);
    }

    default Stream<Direction> getPowerInputFaces(Level level, BlockPos pos, BlockState blockState) {
        Predicate<Direction> axlePredicate = getValidAxleInputFaces(blockState, pos);
        Predicate<Direction> handCrankPredicate = getValidHandCrankFaces(blockState, pos);
        return Arrays.stream(Direction.values())
                .filter(direction -> {
                    BlockState inputBlockState = level.getBlockState(pos.relative(direction));
                    return (axlePredicate.test(direction) && inputBlockState.getBlock() instanceof AxlePowerLevelGetter axlePowerLevelGetter && axlePowerLevelGetter.getMechPowerForNeighbor(inputBlockState, direction.getAxis()) > 0)
                            || (handCrankPredicate.test(direction) && inputBlockState.getBlock() instanceof HandCrankBlock && HandCrankBlock.isPowered(inputBlockState));
                });
    }

    default boolean isReceivingMechPower(Level level, BlockState blockState, BlockPos pos) {
        return getPowerInputFaces(level, pos, blockState).findAny().isPresent();
    }

    default boolean isOverPowered(Level level, BlockState blockState, BlockPos pos) {
        return getPowerInputFaces(level, pos, blockState).limit(2).count() > 1;
    }

    default void playBangSound(Level level, BlockPos pos, float volume, float pitch) {
        level.playSound(null, pos, BwtSoundEvents.MECH_BANG, SoundSource.BLOCKS, volume, pitch);
    }

    default void playBangSound(Level level, BlockPos pos, float volume) {
        playBangSound(level, pos, volume, 1);
    }

    default void playBangSound(Level level, BlockPos pos) {
        playBangSound(level, pos, 0.5f);
    }

    default void playCreakSound(Level level, BlockPos pos, float volume) {
        level.playSound(null, pos, BwtSoundEvents.MECH_CREAK,
                SoundSource.BLOCKS, volume, level.random.nextFloat() * 0.25F + 0.25F);
    }

    default void playCreakSound(Level level, BlockPos pos) {
        playCreakSound(level, pos, 0.25f);
    }
}

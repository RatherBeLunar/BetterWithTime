package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockPosAndState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface MechPowerBlockBase extends IMechPowerBlock {
    int turnOnTickRate = 10;
    int turnOffTickRate = 9;

    static int getTurnOnTickRate() {
        return turnOnTickRate;
    }

    static int getTurnOffTickRate() {
        return turnOffTickRate;
    }

    default void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MECH_POWERED);
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
                    var inputBlock = inputBlockState.getBlock();
                    if (inputBlock instanceof IMechPowerBlock inputMechBlock) {
                        return axlePredicate.test(direction) && inputMechBlock.isMechPowered(inputBlockState) && (
                                inputMechBlock.canTransferPower(inputBlockState, direction.getOpposite())
                        );
                    }
                    // (axlePredicate.test(direction) && inputBlockState.getBlock() instanceof AxleBlock && AxleBlock.isPowered(inputBlockState))
                    return (handCrankPredicate.test(direction) && inputBlockState.getBlock() instanceof HandCrankBlock && HandCrankBlock.isPowered(inputBlockState));
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

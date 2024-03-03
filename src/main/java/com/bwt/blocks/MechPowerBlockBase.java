package com.bwt.blocks;

import com.bwt.BetterWithTime;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

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

    default List<BlockPos> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values()).map(pos::offset).toList();
    }

    default List<BlockPos> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values())
                .filter(direction -> !direction.equals(Direction.DOWN))
                .map(pos::offset)
                .toList();
    }

    default boolean isReceivingMechPower(World world, BlockState blockState, BlockPos pos) {
        for (BlockPos inputBlockPos : getValidAxleInputFaces(blockState, pos)) {
            BlockState inputBlockState = world.getBlockState(inputBlockPos);
            if (!(inputBlockState.isOf(BwtBlocks.axleBlock) || inputBlockState.isOf(BwtBlocks.axlePowerSourceBlock))) {
                continue;
            }

            Vec3i directionVector = pos.subtract(inputBlockPos);
            Direction direction = Direction.fromVector(directionVector.getX(), directionVector.getY(), directionVector.getZ());
            if (direction == null) {
                continue;
            }

            Direction.Axis axis = direction.getAxis();
            if (inputBlockState.get(AxleBlock.AXIS).equals(axis) && AxleBlock.isPowered(inputBlockState)) {
                return true;
            }
        }
        for (BlockPos handCrankPos : getValidHandCrankFaces(blockState, pos)) {
            BlockState handCrankBlockState = world.getBlockState(handCrankPos);
            if (handCrankBlockState.isOf(BwtBlocks.handCrankBlock) && HandCrankBlock.isPowered(handCrankBlockState)) {
               return true;
            }
        }
        return false;
    }

    default void playBangSound(World world, BlockPos pos, float volume, float pitch) {
        world.playSound(null, pos, BetterWithTime.MECH_BANG_SOUND, SoundCategory.BLOCKS, volume, pitch);
    }

    default void playBangSound(World world, BlockPos pos, float volume) {
        playBangSound(world, pos, volume, 1);
    }

    default void playBangSound(World world, BlockPos pos) {
        playBangSound(world, pos, 0.5f);
    }

    default void playCreakSound(World world, BlockPos pos, float volume) {
        world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS, volume, world.random.nextFloat() * 0.25F + 0.25F);
    }

    default void playCreakSound(World world, BlockPos pos) {
        playCreakSound(world, pos, 0.25f);
    }
}

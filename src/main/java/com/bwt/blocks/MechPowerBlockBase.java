package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

    default List<BlockPos> getValidInputFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values()).map(pos::offset).toList();
    }

    default boolean isReceivingMechPower(World world, BlockState blockState, BlockPos pos) {
        for (BlockPos inputBlockPos : getValidInputFaces(blockState, pos)) {
            BlockState inputBlockState = world.getBlockState(inputBlockPos);
            if (!(inputBlockState.isOf(BwtBlocks.axleBlock) || inputBlockState.isOf(BwtBlocks.axlePowerSourceBlock))) {
                return false;
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
        return false;
    }

}

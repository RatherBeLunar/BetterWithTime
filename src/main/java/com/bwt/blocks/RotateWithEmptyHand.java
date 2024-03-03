package com.bwt.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface RotateWithEmptyHand {
    default BlockState getNextOrientation(BlockState blockState) {
        return blockState.with(Properties.FACING, switch (blockState.get(Properties.FACING)) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.UP;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.NORTH;
        });
    }

    default BlockState onUseRotate(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!player.getMainHandStack().isEmpty()) {
            return state;
        }
        world.playSound(null, pos, state.getSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS, 0.25f, world.random.nextFloat() * 0.25F + 0.25F);
        return getNextOrientation(state);
    }
}

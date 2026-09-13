package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface RotateWithEmptyHand {
    default BlockState getNextOrientation(BlockState blockState) {
        return blockState.setValue(BlockStateProperties.FACING, switch (blockState.getValue(BlockStateProperties.FACING)) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.UP;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.NORTH;
        });
    }

    default BlockState onUseRotate(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getMainHandItem().isEmpty()) {
            return state;
        }
        level.playSound(null, pos, state.getSoundType().getPlaceSound(),
                SoundSource.BLOCKS, 0.25f, level.random.nextFloat() * 0.25F + 0.25F);
        return getNextOrientation(state);
    }
}

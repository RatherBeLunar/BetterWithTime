package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ObsidianPressurePlateBlock extends PressurePlateBlock {
    public ObsidianPressurePlateBlock(Properties settings) {
        super(BlockSetType.STONE, settings);
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos pos) {
        return PressurePlateBlock.getEntityCount(level, TOUCH_AABB.move(pos), Player.class) > 0 ? 15 : 0;
    }
}

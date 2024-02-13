package com.bwt.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ObsidianPressurePlateBlock extends PressurePlateBlock {
    public ObsidianPressurePlateBlock(Settings settings) {
        super(BlockSetType.STONE, settings);
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        return PressurePlateBlock.getEntityCount(world, BOX.offset(pos), PlayerEntity.class) > 0 ? 15 : 0;
    }
}

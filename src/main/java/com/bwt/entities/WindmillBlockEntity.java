package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class WindmillBlockEntity extends BlockEntity {
    public WindmillBlockEntity(BlockPos pos, BlockState state) {
        super(BwtEntities.windmillBlockEntity, pos, state);
    }
}

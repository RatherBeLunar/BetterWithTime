package com.bwt.blocks.dirt_slab;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirtPathSlabBlock extends DirtSlabBlock {
    protected static final VoxelShape BOTTOM_PATH_SHAPE;

    public DirtPathSlabBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOTTOM_PATH_SHAPE;
    }

    @Override
    public boolean enableSnow() {
        return false;
    }

    static {
        BOTTOM_PATH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    }
}

package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColumnBlock extends DecorativeBlock {
    final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 16, 13);

    public ColumnBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
    }

    public static ColumnBlock ofBlock(Block fullBlock) {
        return new ColumnBlock(Properties.ofFullCopy(fullBlock), fullBlock);
    }

    public static ColumnBlock ofWoodBlock(Block woodBlock) {
        ColumnBlock columnBlock = ofBlock(woodBlock);
        columnBlock.isWood = true;
        return columnBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}

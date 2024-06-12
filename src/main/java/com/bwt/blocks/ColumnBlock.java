package com.bwt.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ColumnBlock extends DecorativeBlock {
    VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 16, 13);

    public ColumnBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
    }

    public static ColumnBlock ofBlock(Block fullBlock, Block slabBlock) {
        return new ColumnBlock(Settings.copy(slabBlock), fullBlock);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

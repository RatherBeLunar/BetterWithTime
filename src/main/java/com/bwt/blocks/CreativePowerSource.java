package com.bwt.blocks;

import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.digraph.SourceNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CreativePowerSource extends Block implements SourceNode {

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6f, 6f, 0f, 10f, 10f, 16f);

    public CreativePowerSource(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.union(X_SHAPE, Y_SHAPE, Z_SHAPE);
    }


    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowered.appendProperties(builder);
    }

    @Override
    public boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {

        return true;
    }
}

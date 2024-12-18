package com.bwt.blocks;

import com.bwt.mechanical.api.NodeProvider;
import com.bwt.mechanical.api.digraph.Node;
import com.bwt.mechanical.api.digraph.SourceNode;
import com.bwt.mechanical.api.IMechPoweredBlock;
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

public class CreativePowerSource extends Block implements NodeProvider {

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6f, 6f, 0f, 10f, 10f, 16f);

    private final SourceNode sourceNode = new SourceNode() {
        @Override
        public boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {
            return true;
        }
    };

    public CreativePowerSource(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.union(X_SHAPE, Y_SHAPE, Z_SHAPE);
    }


    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        IMechPoweredBlock.appendProperties(builder);
    }

    @Override
    public Node getNode() {
        return this.sourceNode;
    }
}

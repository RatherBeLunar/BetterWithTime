package com.bwt.blocks;

import com.bwt.mechanical.api.ArcProvider;
import com.bwt.mechanical.api.NodeProvider;
import com.bwt.mechanical.api.digraph.Node;
import com.bwt.mechanical.api.digraph.SourceNode;
import com.bwt.mechanical.impl.Axle;
import com.bwt.mechanical.impl.DirectionTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class AxlePowerSourceBlock extends AxleBlock implements NodeProvider, ArcProvider {

    private final SourceNode sourceNode = new SourceNode() {
        @Override
        public boolean isSendingOutput(World world, BlockState state, BlockPos blockPos, Direction direction) {
            return state.get(AXIS).equals(direction.getAxis());
        }
    };

    public AxlePowerSourceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(Axle.MECH_POWER, 3));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

    }

    @Override
    public Node getNode() {
        return this.sourceNode;
    }

}

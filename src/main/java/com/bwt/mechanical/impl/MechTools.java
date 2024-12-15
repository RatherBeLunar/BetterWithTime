package com.bwt.mechanical.impl;

import com.bwt.mechanical.api.ArcProvider;
import com.bwt.mechanical.api.NodeProvider;
import com.bwt.mechanical.api.digraph.Arc;
import com.bwt.mechanical.api.digraph.Node;
import com.bwt.mechanical.api.digraph.SourceNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class MechTools {

    public static Optional<Node> getNode(BlockState state) {
        if (state == null) {
            return Optional.empty();
        }
        var block = state.getBlock();
        if (block instanceof NodeProvider) {
            return Optional.of(((NodeProvider) block).getNode());
        }
        return Optional.empty();
    }

    public static Optional<SourceNode> getSourceNode(BlockState state) {
        return getNode(state).map(node -> {
            if (node instanceof SourceNode) {
                return (SourceNode) node;
            }
            return null;
        });
    }

    public static Optional<Arc> getArc(BlockState state) {
        if (state == null) {
            return Optional.empty();
        }
        var block = state.getBlock();
        if (block instanceof ArcProvider arcProvider) {
            var arc = arcProvider.getArc();
            return Optional.ofNullable(arc);
        }
        return Optional.empty();
    }


    public static boolean isReceivingPowerFromArcOrSource(World world, BlockState state, BlockPos blockPos, List<Direction> inputFaces) {
        for(Direction direction : inputFaces) {
            var inputPos = blockPos.offset(direction);
            var inputState = world.getBlockState(inputPos);
            var optNode = MechTools.getSourceNode(inputState);
            var neighborPos = blockPos.offset(direction);
            var neighborState = world.getBlockState(neighborPos);
            if(optNode.isPresent()) {
                var node = optNode.get();
                if(node.isSendingOutput(world, neighborState, neighborPos, direction.getOpposite())) {
                    return true;
                }
            }
            var arc = MechTools.getArc(inputState);
            if(arc.isPresent()) {
                if(arc.get().isSendingOutput(world, inputState, inputPos, direction)) {
                    return true;
                }
            }
        }
        return false;
    }

}

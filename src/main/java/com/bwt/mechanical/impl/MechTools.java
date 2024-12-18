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
import org.apache.commons.lang3.stream.Streams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MechTools {

    public static Optional<Node> getNode(BlockState state) {
        if (state == null) {
            return Optional.empty();
        }
        var block = state.getBlock();
        if (block instanceof Node node) {
            return Optional.of(node);
        } else if (block instanceof NodeProvider) {
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
        if (block instanceof Arc arc) {
            return Optional.of(arc);
        } else if (block instanceof ArcProvider arcProvider) {
            var arc = arcProvider.getArc();
            return Optional.ofNullable(arc);
        }
        return Optional.empty();
    }

    public static boolean isReceivingPowerFromArcOrSourceDirection(World world, BlockState state, BlockPos blockPos, List<Direction> inputFaces) {
        return !getReceivingPowerFromArcOrSourceDirections(world, state, blockPos, inputFaces).isEmpty();
    }

    @NotNull
    public static List<Direction> getReceivingPowerFromArcOrSourceDirections(World world, BlockState state, BlockPos blockPos, @NotNull  List<Direction> inputFaces) {

        return Streams.of(inputFaces).filter(direction -> {
            var inputPos = blockPos.offset(direction);
            var inputState = world.getBlockState(inputPos);
            var optNode = MechTools.getSourceNode(inputState);
            var neighborPos = blockPos.offset(direction);
            var neighborState = world.getBlockState(neighborPos);
            if (optNode.isPresent()) {
                return optNode.map(node -> node.isSendingOutput(world, neighborState, neighborPos, direction.getOpposite())).orElse(false);
            } else {
                var optArc = MechTools.getArc(inputState);
                return optArc.map(value -> value.isSendingOutput(world, inputState, inputPos, direction)).orElse(false);
            }
        }).toList();
    }

}

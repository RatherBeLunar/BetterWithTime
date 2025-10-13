package com.bwt.blocks.lens;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.gamerules.BwtGameRules;
import com.bwt.utils.BlockPosAndState;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.NeighborUpdater;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

public class LensBeamHelper {
    protected static boolean anyEntitiesIntersecting(World world, BlockPos pos) {
        ArrayList<Entity> list = Lists.newArrayList();
        world.collectEntitiesByType(
                TypeFilter.instanceOf(Entity.class),
                new Box(pos),
                EntityPredicates.EXCEPT_SPECTATOR,
                list,
                1
        );
        return !list.isEmpty();
    }

    protected static int getRemainingRange(World world, BlockPos pos, Direction direction) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        int distanceFromLens = 0;
        int maxRange = world.getGameRules().getInt(BwtGameRules.LENS_BEAM_RANGE);
        while (distanceFromLens < maxRange) {
            mutable.move(direction.getOpposite());
            distanceFromLens++;
            BlockState possibleLensBlockState = world.getBlockState(mutable);
            if (possibleLensBlockState.isOf(BwtBlocks.lensBeamBlock)) {
                if (possibleLensBlockState.get(LensBeamBlock.FACING_PROPERTIES.get(direction))) {
                    continue;
                }
                break;
            }
            if (possibleLensBlockState.isOf(BwtBlocks.lensBlock)) {
                break;
            }
        }
        return maxRange - distanceFromLens;
    }

    public static void fireBeam(World world, BlockPos lensPos, BlockState lensState) {
        propagateBeam(world, lensPos, lensState, lensState.get(LensBlock.FACING), world.getGameRules().getInt(BwtGameRules.LENS_BEAM_RANGE));
    }

    public static void killBeam(World world, BlockPos originPos, Direction facing) {
        BlockPos targetPos = originPos;
        BlockState targetState;
        while (true) {
            targetPos = targetPos.offset(facing);
            targetState = world.getBlockState(targetPos);
            if (!(targetState.getBlock() instanceof LensBeamBlock) || !targetState.get(LensBeamBlock.FACING_PROPERTIES.get(facing))) {
                break;
            }
            removeBeam(world, targetPos, targetState, facing);
        }
    }

    public static void propagateBeam(World world, BlockPos originBeamPos, BlockState originBeamState, Direction facing, int range) {
        if (range <= 0) {
            return;
        }
        if (!(originBeamState.getBlock() instanceof LensBeamBlock) && !originBeamState.isOf(BwtBlocks.lensBlock)) {
            return;
        }

        BlockPos targetPos = originBeamPos.offset(facing);
        BlockState targetState = world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        LensBeamBlock lensBeamBlock = targetBlock instanceof LensBeamBlock beamBlock ? beamBlock : null;
        boolean targetBlockIsBeamPermeable = targetState.isIn(BlockTags.AIR)
                || targetState.isOf(BwtBlocks.lensBeamGlassBlock)
                || targetState.isOf(BwtBlocks.lensBeamGlassBlock.glassBlock);
        boolean targetBlockIsForwardFacingBeam = lensBeamBlock != null && targetState.get(LensBeamBlock.FACING_PROPERTIES.get(facing));
        // if the first block is solid, or if it's a beam already being fired in the correct direction,
        // do nothing.
        if (!targetBlockIsBeamPermeable || targetBlockIsForwardFacingBeam) {
            if (!targetBlockIsBeamPermeable && originBeamState.getBlock() instanceof LensBeamBlock) {
                setTerminus(world, originBeamPos, originBeamState, true);
            }
            return;
        }
        boolean entitiesIntersecting = anyEntitiesIntersecting(world, targetPos);
        targetState = addBeam(world, targetPos, targetState, facing, entitiesIntersecting);

        if (entitiesIntersecting) {
            return;
        }

        propagateBeam(world, targetPos, targetState, facing, range - 1);

    }

    public static BlockState addBeam(World world, BlockPos targetPos, BlockState targetState, Direction facingToAdd, boolean entitiesIntersecting) {
        BlockState newState = targetState;
        if (!(newState.getBlock() instanceof LensBeamBlock)) {
            if (newState.isIn(BlockTags.AIR)) {
                newState = BwtBlocks.lensBeamBlock.getDefaultState();
            }
            else if (newState.isOf(BwtBlocks.lensBeamGlassBlock.glassBlock)) {
                newState = BwtBlocks.lensBeamGlassBlock.getDefaultState();
            }
        }
        newState = newState.with(LensBeamBlock.FACING_PROPERTIES.get(facingToAdd), true)
                .with(LensBeamBlock.TERMINUS, entitiesIntersecting || newState.get(LensBeamBlock.TERMINUS));
        world.setBlockState(targetPos, newState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        return newState;
    }

    public static void removeBeam(World world, BlockPos targetPos, BlockState targetState, Direction facingToRemove) {
        BlockState newState = targetState.with(LensBeamBlock.FACING_PROPERTIES.get(facingToRemove), false);
        boolean replacedWithAir = false;
        boolean terminusModified;
        if (streamFacingDirections(newState).findAny().isEmpty()) {
            newState = newState.getBlock() instanceof LensBeamBlock beamBlock
                    ? beamBlock.getStateLeftOverWhenEmpty(world, targetPos)
                    : Blocks.AIR.getDefaultState();
            terminusModified = targetState.get(LensBeamBlock.TERMINUS);
            replacedWithAir = newState.isIn(BlockTags.AIR);
        }
        else {
            newState = newState.with(LensBeamBlock.TERMINUS, anyNeighborNotPropagable(world, targetPos, newState));
            terminusModified = targetState.get(LensBeamBlock.TERMINUS) != newState.get(LensBeamBlock.TERMINUS);
        }
        world.setBlockState(targetPos, newState, Block.NOTIFY_LISTENERS | (terminusModified ? 0 : Block.FORCE_STATE));
        if (replacedWithAir || terminusModified) {
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BlockPosAndState neighborPosAndState = BlockPosAndState.of(world, targetPos.offset(direction));
                boolean facingIntoNeighbor = targetState.get(LensBeamBlock.FACING_PROPERTIES.get(direction)) && !neighborPosAndState.state().isIn(BlockTags.AIR);
                boolean detectorFacingIntoBeam = neighborPosAndState.state().isOf(BwtBlocks.detectorBlock) && neighborPosAndState.state().get(DetectorBlock.FACING).equals(direction.getOpposite());
                if (detectorFacingIntoBeam || (facingIntoNeighbor && terminusModified)) {
                    world.updateNeighbor(neighborPosAndState.state(), neighborPosAndState.pos(), newState.getBlock(), targetPos, false);
                }
                if (facingIntoNeighbor && terminusModified) {
                    world.replaceWithStateForNeighborUpdate(direction.getOpposite(), newState, neighborPosAndState.pos(), targetPos, Block.NOTIFY_LISTENERS, 512);
                }
            }
        }
    }

    public static boolean anyNeighborNotPropagable(WorldAccess world, BlockPos pos, BlockState state) {
        return streamFacingDirections(state).map(Map.Entry::getKey)
                .map(pos::offset)
                .map(world::getBlockState)
                .anyMatch(blockState -> !blockState.isIn(BlockTags.AIR)
                        && !blockState.isOf(BwtBlocks.lensBeamGlassBlock)
                        && !blockState.isOf(BwtBlocks.lensBeamGlassBlock.glassBlock));
    }

    public static BlockState setTerminus(World world, BlockPos pos, BlockState state, boolean terminus) {
        if (state.get(LensBeamBlock.TERMINUS) != terminus) {
            state = state.with(LensBeamBlock.TERMINUS, terminus);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        return state;
    }

    public static Stream<Map.Entry<Direction, BooleanProperty>> streamFacingDirections(BlockState state) {
        return LensBeamBlock.FACING_PROPERTIES.entrySet().stream()
                .filter(entry -> state.get(entry.getValue()));
    }
}

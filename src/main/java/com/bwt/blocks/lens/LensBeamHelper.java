package com.bwt.blocks.lens;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.gamerules.BwtGameRules;
import com.bwt.utils.BlockPosAndState;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.phys.AABB;

public class LensBeamHelper {
    protected static boolean anyEntitiesIntersecting(Level level, BlockPos pos) {
        ArrayList<Entity> list = Lists.newArrayList();
        level.getEntities(
                EntityTypeTest.forClass(Entity.class),
                new AABB(pos),
                EntitySelector.NO_SPECTATORS,
                list,
                1
        );
        return !list.isEmpty();
    }

    protected static int getRemainingRange(Level level, BlockPos pos, Direction direction) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        int distanceFromLens = 0;
        int maxRange = level.getGameRules().getInt(BwtGameRules.LENS_BEAM_RANGE);
        while (distanceFromLens < maxRange) {
            mutable.move(direction.getOpposite());
            distanceFromLens++;
            BlockState possibleLensBlockState = level.getBlockState(mutable);
            if (possibleLensBlockState.is(BwtBlocks.lensBeamBlock)) {
                if (possibleLensBlockState.getValue(LensBeamBlock.FACING_PROPERTIES.get(direction))) {
                    continue;
                }
                break;
            }
            if (possibleLensBlockState.is(BwtBlocks.lensBlock)) {
                break;
            }
        }
        return maxRange - distanceFromLens;
    }

    public static void fireBeam(Level level, BlockPos lensPos, BlockState lensState) {
        propagateBeam(level, lensPos, lensState, lensState.getValue(LensBlock.FACING), level.getGameRules().getInt(BwtGameRules.LENS_BEAM_RANGE));
    }

    public static void killBeam(Level level, BlockPos originPos, Direction facing) {
        BlockPos targetPos = originPos;
        BlockState targetState;
        while (true) {
            targetPos = targetPos.relative(facing);
            targetState = level.getBlockState(targetPos);
            if (!(targetState.getBlock() instanceof LensBeamBlock) || !targetState.getValue(LensBeamBlock.FACING_PROPERTIES.get(facing))) {
                break;
            }
            removeBeam(level, targetPos, targetState, facing);
        }
    }

    public static void propagateBeam(Level level, BlockPos originBeamPos, BlockState originBeamState, Direction facing, int range) {
        if (range <= 0) {
            return;
        }
        if (!(originBeamState.getBlock() instanceof LensBeamBlock) && !originBeamState.is(BwtBlocks.lensBlock)) {
            return;
        }

        BlockPos targetPos = originBeamPos.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        LensBeamBlock lensBeamBlock = targetBlock instanceof LensBeamBlock beamBlock ? beamBlock : null;
        boolean targetBlockIsBeamPermeable = targetState.is(BlockTags.AIR)
                || targetState.is(BwtBlocks.lensBeamGlassBlock)
                || targetState.is(BwtBlocks.lensBeamGlassBlock.glassBlock);
        boolean targetBlockIsForwardFacingBeam = lensBeamBlock != null && targetState.getValue(LensBeamBlock.FACING_PROPERTIES.get(facing));
        // if the first block is solid, or if it's a beam already being fired in the correct direction,
        // do nothing.
        if (!targetBlockIsBeamPermeable || targetBlockIsForwardFacingBeam) {
            if (!targetBlockIsBeamPermeable && originBeamState.getBlock() instanceof LensBeamBlock) {
                setTerminus(level, originBeamPos, originBeamState, true);
            }
            return;
        }
        boolean entitiesIntersecting = anyEntitiesIntersecting(level, targetPos);
        targetState = addBeam(level, targetPos, targetState, facing, entitiesIntersecting);

        if (entitiesIntersecting) {
            return;
        }

        propagateBeam(level, targetPos, targetState, facing, range - 1);

    }

    public static BlockState addBeam(Level level, BlockPos targetPos, BlockState targetState, Direction facingToAdd, boolean entitiesIntersecting) {
        BlockState newState = targetState;
        if (!(newState.getBlock() instanceof LensBeamBlock)) {
            if (newState.is(BlockTags.AIR)) {
                newState = BwtBlocks.lensBeamBlock.defaultBlockState();
            }
            else if (newState.is(BwtBlocks.lensBeamGlassBlock.glassBlock)) {
                newState = BwtBlocks.lensBeamGlassBlock.defaultBlockState();
            }
        }
        newState = newState.setValue(LensBeamBlock.FACING_PROPERTIES.get(facingToAdd), true)
                .setValue(LensBeamBlock.TERMINUS, entitiesIntersecting || newState.getValue(LensBeamBlock.TERMINUS));
        level.setBlock(targetPos, newState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        return newState;
    }

    public static void removeBeam(Level level, BlockPos targetPos, BlockState targetState, Direction facingToRemove) {
        BlockState newState = targetState.setValue(LensBeamBlock.FACING_PROPERTIES.get(facingToRemove), false);
        boolean replacedWithAir = false;
        boolean terminusModified;
        if (streamFacingDirections(newState).findAny().isEmpty()) {
            newState = newState.getBlock() instanceof LensBeamBlock beamBlock
                    ? beamBlock.getStateLeftOverWhenEmpty(level, targetPos)
                    : Blocks.AIR.defaultBlockState();
            terminusModified = targetState.getValue(LensBeamBlock.TERMINUS);
            replacedWithAir = newState.is(BlockTags.AIR);
        }
        else {
            newState = newState.setValue(LensBeamBlock.TERMINUS, anyNeighborNotPropagable(level, targetPos, newState));
            terminusModified = targetState.getValue(LensBeamBlock.TERMINUS) != newState.getValue(LensBeamBlock.TERMINUS);
        }
        level.setBlock(targetPos, newState, Block.UPDATE_CLIENTS | (terminusModified ? 0 : Block.UPDATE_KNOWN_SHAPE));
        if (replacedWithAir || terminusModified) {
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BlockPosAndState neighborPosAndState = BlockPosAndState.of(level, targetPos.relative(direction));
                boolean facingIntoNeighbor = targetState.getValue(LensBeamBlock.FACING_PROPERTIES.get(direction)) && !neighborPosAndState.state().is(BlockTags.AIR);
                boolean detectorFacingIntoBeam = neighborPosAndState.state().is(BwtBlocks.detectorBlock) && neighborPosAndState.state().getValue(DetectorBlock.FACING).equals(direction.getOpposite());
                if (detectorFacingIntoBeam || (facingIntoNeighbor && terminusModified)) {
                    level.neighborChanged(neighborPosAndState.state(), neighborPosAndState.pos(), newState.getBlock(), targetPos, false);
                }
                if (facingIntoNeighbor && terminusModified) {
                    level.neighborShapeChanged(direction.getOpposite(), newState, neighborPosAndState.pos(), targetPos, Block.UPDATE_CLIENTS, 512);
                }
            }
        }
    }

    public static boolean anyNeighborNotPropagable(LevelAccessor level, BlockPos pos, BlockState state) {
        return streamFacingDirections(state).map(Map.Entry::getKey)
                .map(pos::relative)
                .map(level::getBlockState)
                .anyMatch(blockState -> !blockState.is(BlockTags.AIR)
                        && !blockState.is(BwtBlocks.lensBeamGlassBlock)
                        && !blockState.is(BwtBlocks.lensBeamGlassBlock.glassBlock));
    }

    public static BlockState setTerminus(Level level, BlockPos pos, BlockState state, boolean terminus) {
        if (state.getValue(LensBeamBlock.TERMINUS) != terminus) {
            state = state.setValue(LensBeamBlock.TERMINUS, terminus);
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
        return state;
    }

    public static boolean isValidInputBeamOrLens(BlockState neighborState, Direction directionToThisBlock) {
        return (neighborState.is(BwtBlocks.lensBlock) && neighborState.getValue(LensBlock.FACING).equals(directionToThisBlock) && neighborState.getValue(LensBlock.LIT))
                || (neighborState.getBlock() instanceof LensBeamBlock && neighborState.getValue(LensBeamBlock.FACING_PROPERTIES.get(directionToThisBlock)));
    }

    public static Stream<Map.Entry<Direction, BooleanProperty>> streamFacingDirections(BlockState state) {
        return LensBeamBlock.FACING_PROPERTIES.entrySet().stream()
                .filter(entry -> state.getValue(entry.getValue()));
    }
}

package com.bwt.blocks.lens;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.gamerules.BwtGameRules;
import com.bwt.utils.BlockPosAndState;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.NeighborUpdater;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LensBeamBlock extends Block {
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty TERMINUS = BooleanProperty.of("terminus");
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), directions -> {
        directions.put(Direction.NORTH, NORTH);
        directions.put(Direction.EAST, EAST);
        directions.put(Direction.SOUTH, SOUTH);
        directions.put(Direction.WEST, WEST);
        directions.put(Direction.UP, UP);
        directions.put(Direction.DOWN, DOWN);
    }));

    public LensBeamBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(TERMINUS, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, TERMINUS);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        // Kill beams in all directions that were on before
        Stream<Map.Entry<Direction, BooleanProperty>> stream = streamFacingDirections(state);

        if (newState.isOf(this)) {
            stream = stream.filter(entry -> !newState.get(entry.getValue()));
        }
        stream.forEachOrdered(entry -> killBeam(world, pos, entry.getKey()));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // If any neighbor the beam is facing into is solid, it's a terminus
        return state.with(TERMINUS, anyNeighborSolid(world, pos, state));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        Vec3i vec = sourcePos.subtract(pos);
        Direction direction = Direction.fromVector(vec.getX(), vec.getY(), vec.getZ());
        if (direction == null) {
            return;
        }
        if (!state.get(FACING_PROPERTIES.get(direction))) {
            return;
        }
        int range = getRemainingRange(world, pos, direction);
        if (range > 0 && !anyEntitiesIntersecting(world, pos)) {
            propagateBeam(world, pos, state, direction, range);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        BlockState finalState = setTerminus(world, pos, state, true);
        streamFacingDirections(finalState)
                .forEach(entry -> killBeam(world, pos, entry.getKey()));
        // Need to keep checking for the entity leaving
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        boolean entityIntersect = anyEntitiesIntersecting(world, pos);
        if (entityIntersect) {
            BlockState finalState = setTerminus(world, pos, state, true);
            streamFacingDirections(finalState)
                    .forEach(entry -> killBeam(world, pos, entry.getKey()));
            // Need to keep checking for the entity leaving
            world.scheduleBlockTick(pos, this, 10);
        }
        else {
            BlockState finalState = setTerminus(world, pos, state, false);
            streamFacingDirections(finalState)
                    .forEach(entry -> {
                        int range = getRemainingRange(world, pos, entry.getKey());
                        propagateBeam(world, pos, finalState, entry.getKey(), range);
                    });
        }
    }

    protected boolean anyEntitiesIntersecting(World world, BlockPos pos) {
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

    protected int getRemainingRange(World world, BlockPos pos, Direction direction) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        int distanceFromLens = 0;
        int maxRange = world.getGameRules().getInt(BwtGameRules.LENS_BEAM_RANGE);
        while (distanceFromLens < maxRange) {
            mutable.move(direction.getOpposite());
            distanceFromLens++;
            BlockState possibleLensBlockState = world.getBlockState(mutable);
            if (possibleLensBlockState.isOf(BwtBlocks.lensBeamBlock)) {
                if (possibleLensBlockState.get(FACING_PROPERTIES.get(direction))) {
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

    public void fireBeam(World world, BlockPos lensPos, BlockState lensState) {
        propagateBeam(world, lensPos, lensState, lensState.get(LensBlock.FACING), world.getGameRules().getInt(BwtGameRules.LENS_BEAM_RANGE));
    }

    public void killBeam(World world, BlockPos originPos, Direction facing) {
        BlockPos targetPos = originPos;
        BlockState targetState;
        while (true) {
            targetPos = targetPos.offset(facing);
            targetState = world.getBlockState(targetPos);
            if (!targetState.isOf(this) || !targetState.get(FACING_PROPERTIES.get(facing))) {
                break;
            }
            removeBeam(world, targetPos, targetState, facing);
        }
    }

    public void propagateBeam(World world, BlockPos originBeamPos, BlockState originBeamState, Direction facing, int range) {
        if (range <= 0) {
            return;
        }
        if (!originBeamState.isOf(this) && !originBeamState.isOf(BwtBlocks.lensBlock)) {
            return;
        }

        BlockPos targetPos = originBeamPos.offset(facing);
        BlockState targetState = world.getBlockState(targetPos);
        boolean stateIsBeam = targetState.isOf(this);
        boolean isOtherAir = targetState.isIn(BlockTags.AIR);
        boolean isForwardFacingBeam = stateIsBeam && targetState.get(FACING_PROPERTIES.get(facing));
        // if the first block is solid, or if it's a beam already being fired in the correct direction,
        // do nothing.
        if (!isOtherAir || isForwardFacingBeam) {
            if (!isOtherAir && originBeamState.isOf(this)) {
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

    public BlockState addBeam(World world, BlockPos targetPos, BlockState targetState, Direction facingToAdd, boolean entitiesIntersecting) {
        BlockState newState = targetState;
        if (!targetState.isOf(this)) {
            newState = this.getDefaultState();
        }
        newState = newState.with(FACING_PROPERTIES.get(facingToAdd), true).with(TERMINUS, entitiesIntersecting || newState.get(TERMINUS));
        world.setBlockState(targetPos, newState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        return newState;
    }

    public void removeBeam(World world, BlockPos targetPos, BlockState targetState, Direction facingToRemove) {
        BlockState newState = targetState.with(FACING_PROPERTIES.get(facingToRemove), false);
        boolean replacedWithAir = false;
        boolean terminusModified;
        if (streamFacingDirections(newState).findAny().isEmpty()) {
            newState = world.getFluidState(targetPos).getBlockState();
            terminusModified = targetState.get(TERMINUS);
            replacedWithAir = true;
        }
        else {
            newState = newState.with(TERMINUS, anyNeighborSolid(world, targetPos, newState));
            terminusModified = targetState.get(TERMINUS) != newState.get(TERMINUS);
        }
        world.setBlockState(targetPos, newState, Block.NOTIFY_LISTENERS | (terminusModified ? 0 : Block.FORCE_STATE));
        if (replacedWithAir || terminusModified) {
            for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
                BlockPosAndState neighborPosAndState = BlockPosAndState.of(world, targetPos.offset(direction));
                boolean facingIntoNeighbor = targetState.get(FACING_PROPERTIES.get(direction)) && !neighborPosAndState.state().isIn(BlockTags.AIR);
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

    public boolean anyNeighborSolid(WorldAccess world, BlockPos pos, BlockState state) {
         return streamFacingDirections(state).map(Map.Entry::getKey)
                .map(pos::offset)
                .map(world::getBlockState)
                .anyMatch(blockState -> !blockState.isIn(BlockTags.AIR));
    }

    public BlockState setTerminus(World world, BlockPos pos, BlockState state, boolean terminus) {
        if (state.get(TERMINUS) != terminus) {
            state = state.with(TERMINUS, terminus);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        return state;
    }

    public Stream<Map.Entry<Direction, BooleanProperty>> streamFacingDirections(BlockState state) {
        return FACING_PROPERTIES.entrySet().stream()
                .filter(entry -> state.get(entry.getValue()));
    }
}

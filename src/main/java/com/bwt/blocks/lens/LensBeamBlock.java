package com.bwt.blocks.lens;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;
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
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        // Kill beams in all directions that were on before
        Stream<Map.Entry<Direction, BooleanProperty>> stream = LensBeamHelper.streamFacingDirections(state);

        if (newState.isOf(this)) {
            stream = stream.filter(entry -> !newState.get(entry.getValue()));
        }
        stream.forEachOrdered(entry -> LensBeamHelper.killBeam(world, pos, entry.getKey()));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // If any neighbor the beam is facing into is solid, it's a terminus
        return state.with(TERMINUS, LensBeamHelper.anyNeighborNotPropagable(world, pos, state));
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
        int range = LensBeamHelper.getRemainingRange(world, pos, direction);
        if (range > 0 && !LensBeamHelper.anyEntitiesIntersecting(world, pos)) {
            LensBeamHelper.propagateBeam(world, pos, state, direction, range);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        BlockState finalState = LensBeamHelper.setTerminus(world, pos, state, true);
        LensBeamHelper.streamFacingDirections(finalState)
                .forEach(entry -> LensBeamHelper.killBeam(world, pos, entry.getKey()));
        // Need to keep checking for the entity leaving
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        boolean entityIntersect = LensBeamHelper.anyEntitiesIntersecting(world, pos);
        if (entityIntersect) {
            BlockState finalState = LensBeamHelper.setTerminus(world, pos, state, true);
            LensBeamHelper.streamFacingDirections(finalState)
                    .forEach(entry -> LensBeamHelper.killBeam(world, pos, entry.getKey()));
            // Need to keep checking for the entity leaving
            world.scheduleBlockTick(pos, this, 10);
        }
        else {
            BlockState finalState = LensBeamHelper.setTerminus(world, pos, state, false);
            LensBeamHelper.streamFacingDirections(finalState)
                    .forEach(entry -> {
                        int range = LensBeamHelper.getRemainingRange(world, pos, entry.getKey());
                        LensBeamHelper.propagateBeam(world, pos, finalState, entry.getKey(), range);
                    });
        }
    }

    public BlockState getStateLeftOverWhenEmpty(World world, BlockPos pos) {
        return world.getFluidState(pos).getBlockState();
    }
}

package com.bwt.blocks.lens;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Map;
import java.util.stream.Stream;

public class LensBeamBlock extends Block {
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty TERMINUS = BooleanProperty.create("terminus");
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), directions -> {
        directions.put(Direction.NORTH, NORTH);
        directions.put(Direction.EAST, EAST);
        directions.put(Direction.SOUTH, SOUTH);
        directions.put(Direction.WEST, WEST);
        directions.put(Direction.UP, UP);
        directions.put(Direction.DOWN, DOWN);
    }));

    public LensBeamBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(TERMINUS, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, TERMINUS);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, level, pos, newState, moved);
        // Kill beams in all directions that were on before
        Stream<Map.Entry<Direction, BooleanProperty>> stream = LensBeamHelper.streamFacingDirections(state);

        if (newState.is(this)) {
            stream = stream.filter(entry -> !newState.getValue(entry.getValue()));
        }
        stream.forEachOrdered(entry -> LensBeamHelper.killBeam(level, pos, entry.getKey()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // If any neighbor the beam is facing into is solid, it's a terminus
        return state.setValue(TERMINUS, LensBeamHelper.anyNeighborNotPropagable(level, pos, state));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        Vec3i vec = sourcePos.subtract(pos);
        Direction direction = Direction.fromDelta(vec.getX(), vec.getY(), vec.getZ());
        if (direction == null) {
            return;
        }
        if (!state.getValue(FACING_PROPERTIES.get(direction))) {
            return;
        }
        int range = LensBeamHelper.getRemainingRange(level, pos, direction);
        if (range > 0 && !LensBeamHelper.anyEntitiesIntersecting(level, pos)) {
            LensBeamHelper.propagateBeam(level, pos, state, direction, range);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        BlockState finalState = LensBeamHelper.setTerminus(level, pos, state, true);
        LensBeamHelper.streamFacingDirections(finalState)
                .forEach(entry -> LensBeamHelper.killBeam(level, pos, entry.getKey()));
        // Need to keep checking for the entity leaving
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        boolean entityIntersect = LensBeamHelper.anyEntitiesIntersecting(level, pos);
        if (entityIntersect) {
            BlockState finalState = LensBeamHelper.setTerminus(level, pos, state, true);
            LensBeamHelper.streamFacingDirections(finalState)
                    .forEach(entry -> LensBeamHelper.killBeam(level, pos, entry.getKey()));
            // Need to keep checking for the entity leaving
            level.scheduleTick(pos, this, 10);
        }
        else {
            BlockState finalState = LensBeamHelper.setTerminus(level, pos, state, false);
            LensBeamHelper.streamFacingDirections(finalState)
                    .forEach(entry -> {
                        int range = LensBeamHelper.getRemainingRange(level, pos, entry.getKey());
                        LensBeamHelper.propagateBeam(level, pos, finalState, entry.getKey(), range);
                    });
        }
    }

    public BlockState getStateLeftOverWhenEmpty(LevelAccessor level, BlockPos pos) {
        return level.getFluidState(pos).createLegacyBlock();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH))
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH))
                    .setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST))
                    .setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST))
                    .setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST))
                    .setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST))
                    .setValue(WEST, state.getValue(SOUTH));
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default -> super.mirror(state, mirror);
        };
    }
}

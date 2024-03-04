package com.bwt.blocks.detector;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DetectorLogicBlock extends AirBlock {
    private static final int tickRate = 4;
    public static final BooleanProperty ENTITY_INTERSECT = BooleanProperty.of("entity_intersect");
    public static final BooleanProperty BLOCK_INTERSECT = BooleanProperty.of("block_intersect");

    public DetectorLogicBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ENTITY_INTERSECT, false).with(BLOCK_INTERSECT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENTITY_INTERSECT);
        builder.add(BLOCK_INTERSECT);
    }

    public static boolean isEnabled(BlockState state) {
        return state.get(ENTITY_INTERSECT) || state.get(BLOCK_INTERSECT);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!anyNeighborDetectors(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (!direction.equals(Direction.DOWN)) {
            return state;
        }
        boolean blockIntersect = anyBlocksIntersecting(neighborState);
        if (blockIntersect == state.get(BLOCK_INTERSECT)) {
            return state;
        }
        world.scheduleBlockTick(pos, this, tickRate);
        return state.with(BLOCK_INTERSECT, blockIntersect);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        boolean updated = updateIntersectStates(state, world, pos, true, null);
        if (updated) {
            world.scheduleBlockTick(pos, this, tickRate);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        boolean blockIntersect = anyBlocksIntersecting(world.getBlockState(pos.down()));
        boolean entityIntersect = anyEntitiesIntersecting(world, pos);
        if (entityIntersect) {
            // Need to keep checking for the entity leaving
            world.scheduleBlockTick(pos, this, tickRate);
        }
        if (!updateIntersectStates(state, world, pos, entityIntersect, blockIntersect)) {
            // Detector block is gone, and the logic block is now destroyed
            return;
        }
    }

    protected boolean anyBlocksIntersecting(BlockState neighborState) {
        return neighborState.isIn(BwtBlockTags.DETECTABLE_SMALL_CROPS)
                && neighborState.getOrEmpty(CropBlock.AGE).orElse(0)
                >= ((CropBlock) neighborState.getBlock()).getMaxAge();
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

    protected boolean updateIntersectStates(BlockState state, World world, BlockPos pos, @Nullable Boolean entityIntersect, @Nullable Boolean blockIntersect) {
        if ((entityIntersect == null || entityIntersect == state.get(ENTITY_INTERSECT))
            && (blockIntersect == null || blockIntersect == state.get(BLOCK_INTERSECT))
        ) {
            return false;
        }
        if (entityIntersect != null) {
            state = state.with(ENTITY_INTERSECT, entityIntersect);
        }
        if (blockIntersect != null) {
            state = state.with(BLOCK_INTERSECT, blockIntersect);
        }
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        int detectorsUpdated = notifyNeighborDetectors(state, world, pos);
        if (detectorsUpdated == 0) {
            world.removeBlock(pos, false);
            return false;
        }
        return true;
    }

    public boolean anyNeighborDetectors(WorldAccess world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(targetPos);
            if (neighborState.isOf(BwtBlocks.detectorBlock) && neighborState.get(DetectorBlock.FACING).equals(direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public int notifyNeighborDetectors(BlockState state, World world, BlockPos pos) {
        int numDetectorsUpdated = 0;
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(targetPos);
            if (neighborState.isOf(BwtBlocks.detectorBlock) && neighborState.get(DetectorBlock.FACING).equals(direction.getOpposite())) {
                numDetectorsUpdated += 1;
                world.updateNeighbor(neighborState, targetPos, state.getBlock(), pos, false);
            }
        }
        return numDetectorsUpdated;
    }
}

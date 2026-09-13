package com.bwt.blocks.detector;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DetectorLogicBlock extends AirBlock {
    private static final int tickRate = 4;
    public static final BooleanProperty ENTITY_INTERSECT = BooleanProperty.create("entity_intersect");
    public static final BooleanProperty BLOCK_INTERSECT = BooleanProperty.create("block_intersect");

    public DetectorLogicBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(ENTITY_INTERSECT, false).setValue(BLOCK_INTERSECT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENTITY_INTERSECT);
        builder.add(BLOCK_INTERSECT);
    }

    public static boolean isEnabled(BlockState state) {
        return state.getValue(ENTITY_INTERSECT) || state.getValue(BLOCK_INTERSECT);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        level.scheduleTick(pos, this, tickRate);
        return state;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        if (!level.isClientSide() && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.is(this)) {
            notifyNeighborDetectors(newState, level, pos);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        boolean updated = updateIntersectStates(state, level, pos, true, null);
        if (updated) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        boolean blockIntersect = anyBlocksIntersecting(level.getBlockState(pos.below()));
        boolean entityIntersect = anyEntitiesIntersecting(level, pos);
        if (entityIntersect) {
            // Need to keep checking for the entity leaving
            level.scheduleTick(pos, this, tickRate);
        }
        if (!updateIntersectStates(state, level, pos, entityIntersect, blockIntersect)) {
            // Detector block is gone, and the logic block is now destroyed
            return;
        }
    }

    protected boolean anyBlocksIntersecting(BlockState neighborState) {
        return neighborState.is(BwtBlockTags.DETECTABLE_SMALL_CROPS)
                && neighborState.getOptionalValue(CropBlock.AGE).orElse(0)
                >= ((CropBlock) neighborState.getBlock()).getMaxAge();
    }

    protected boolean anyEntitiesIntersecting(Level level, BlockPos pos) {
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

    protected boolean updateIntersectStates(BlockState state, Level level, BlockPos pos, @Nullable Boolean entityIntersect, @Nullable Boolean blockIntersect) {
        if ((entityIntersect == null || entityIntersect == state.getValue(ENTITY_INTERSECT))
            && (blockIntersect == null || blockIntersect == state.getValue(BLOCK_INTERSECT))
        ) {
            return false;
        }
        if (entityIntersect != null) {
            state = state.setValue(ENTITY_INTERSECT, entityIntersect);
        }
        if (blockIntersect != null) {
            state = state.setValue(BLOCK_INTERSECT, blockIntersect);
        }
        level.setBlock(pos, state, Block.UPDATE_ALL, 0);
        int detectorsUpdated = notifyNeighborDetectors(state, level, pos);
        if (detectorsUpdated == 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL, 0);
            return false;
        }
        return true;
    }

    public static boolean anyNeighborDetectors(LevelAccessor level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(targetPos);
            if (neighborState.is(BwtBlocks.detectorBlock) && neighborState.getValue(DetectorBlock.FACING).equals(direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public int notifyNeighborDetectors(BlockState state, Level level, BlockPos pos) {
        int numDetectorsUpdated = 0;
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(targetPos);
            if (neighborState.is(BwtBlocks.detectorBlock) && neighborState.getValue(DetectorBlock.FACING).equals(direction.getOpposite())) {
                numDetectorsUpdated += 1;
                level.neighborShapeChanged(direction.getOpposite(), state, targetPos, pos, Block.UPDATE_ALL & ~(Block.UPDATE_NEIGHBORS | Block.UPDATE_SUPPRESS_DROPS), 512);
                level.neighborChanged(neighborState, targetPos, state.getBlock(), pos, false);
            }
        }
        return numDetectorsUpdated;
    }
}

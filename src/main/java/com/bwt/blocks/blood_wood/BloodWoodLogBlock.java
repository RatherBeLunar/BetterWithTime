package com.bwt.blocks.blood_wood;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import com.bwt.utils.BlockPosAndState;
import com.bwt.utils.RadiusAroundBlockStream;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BloodWoodLogBlock extends RotatedPillarBlock {
    public static final IntegerProperty POS_NEG = IntegerProperty.create("pos_neg", 0, 1);
    public static final BooleanProperty CAN_GROW = BooleanProperty.create("can_grow");

    public BloodWoodLogBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CAN_GROW, false).setValue(POS_NEG, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CAN_GROW, POS_NEG);
    }

    public boolean canGrow(BlockState state) {
        return state.is(this) && state.getValue(CAN_GROW);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return canGrow(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (canGrow(state)) {
            if (level.dimensionType().ultraWarm()) {
                grow(level, pos, state, random);
            }
            level.setBlockAndUpdate(pos, state.setValue(CAN_GROW, false));
        }
    }

    public static Direction getFacing(BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);
        Direction.AxisDirection axisDirection = state.getValue(POS_NEG) > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        return Direction.get(axisDirection, axis);
    }

    public static void setFacing(Level level, BlockPos pos, BlockState state, Direction facing) {
        level.setBlockAndUpdate(pos, withFacing(state, facing));
    }

    public static BlockState withFacing(BlockState state, Direction facing) {
        return state.setValue(AXIS, facing.getAxis()).setValue(POS_NEG, facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0);
    }

    public static Direction randomHorizontalDirection(RandomSource random) {
        return Direction.from3DDataValue(random.nextIntBetweenInclusive(2, 5));
    }

    public void grow(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random) {
        if (countBloodWoodNeighboringOnBlockWithSoulSand(serverLevel, pos) >= 2) {
            // too much neighboring wood to grow further
            return;
        }

        Direction facing = getFacing(state);
        int randomFactor = random.nextInt(100);
        if (facing == Direction.UP) {
            // trunk growth
            if (randomFactor < 25) {
                // just continue growing upwards
                attemptToGrowIntoBlock(serverLevel, pos.above(), Direction.UP);
            }
            else if (randomFactor < 90) {
                // split and grow upwards
                Direction targetFacing = randomHorizontalDirection(random);
                BlockPos targetPos = pos.relative(targetFacing);
                attemptToGrowIntoBlock(serverLevel, targetPos, targetFacing);
                attemptToGrowIntoBlock(serverLevel, pos.above(), Direction.UP);
            }
            else {
                // split
                for (int temp = 0; temp < 2; temp++) {
                    Direction targetFacing = randomHorizontalDirection(random);
                    BlockPos targetPos = pos.relative(targetFacing);
                    attemptToGrowIntoBlock(serverLevel, targetPos, targetFacing);
                }
            }
        }
        else {
            // branch growth

            if (randomFactor < 40) {
                // grow upwards
                attemptToGrowIntoBlock(serverLevel, pos.above(), facing);
                // reorient existing block so that it looks right
                setFacing(serverLevel, pos, state, Direction.UP);
            }
            else if (randomFactor < 65) {
                // grow in the growth direction
                attemptToGrowIntoBlock(serverLevel, pos.relative(facing), facing);
            }
            else if (randomFactor < 90) {
                // split and keep going
                Direction targetFacing = randomHorizontalDirection(random);
                if (targetFacing == facing) {
                    targetFacing = Direction.UP;
                }

                BlockPos targetPos = pos.relative(targetFacing);

                Direction targetGrowthDirection = facing;

                if (targetFacing.get3DDataValue() >= 2 ) {
                    targetGrowthDirection = targetFacing;
                }

                attemptToGrowIntoBlock(serverLevel, targetPos, targetGrowthDirection);

                if (!attemptToGrowIntoBlock(serverLevel, pos.relative(facing), facing) && targetFacing.get3DDataValue() == 1) {
                    // reorient existing block so that it looks right
                    setFacing(serverLevel, pos, state, Direction.UP);
                }
            }
            else {
                // split
                Direction[] growthDirections = new Direction[2];

                for (int iTempCount = 0; iTempCount < 2; iTempCount++) {
                    growthDirections[iTempCount] = Direction.DOWN;

                    Direction targetFacing = randomHorizontalDirection(random);

                    if (targetFacing == facing) {
                        targetFacing = Direction.UP;
                    }

                    BlockPos targetPos = pos.relative(targetFacing);

                    Direction iTargetGrowthDirection = facing;

                    if (targetFacing.get3DDataValue() >= 2) {
                        iTargetGrowthDirection = targetFacing;
                    }

                    if (attemptToGrowIntoBlock(serverLevel, targetPos, iTargetGrowthDirection)) {
                        growthDirections[iTempCount] = targetFacing;
                    }
                }

                if ((growthDirections[0] == Direction.UP && growthDirections[1].getAxis().isVertical()) || (growthDirections[1] == Direction.UP && growthDirections[0] == Direction.DOWN)) {
                    // reorient existing block so that it looks right
                    setFacing(serverLevel, pos, state, Direction.UP);
                }
            }
        }
    }

    public boolean attemptToGrowIntoBlock(Level level, BlockPos pos, Direction growthDirection) {
        BlockState state = level.getBlockState(pos);
        if (!(state.is(BlockTags.AIR) || state.is(BwtBlocks.bloodWoodBlocks.leavesBlock)) || countBloodWoodNeighboringOnBlockWithSoulSand(level, pos) >= 2) {
            // not empty, or too much neighboring wood to grow further
            return false;
        }
        level.setBlockAndUpdate(pos, withFacing(defaultBlockState(), growthDirection).setValue(CAN_GROW, true));
        growLeaves(level, pos);

        return true;
    }

    public void growLeaves(Level level, BlockPos pos) {
        RadiusAroundBlockStream
                .neighboringBlocksInRadius(pos, 1)
                .map(neighborPos -> BlockPosAndState.of(level, neighborPos))
                .filter(neighbor -> neighbor.state().is(BlockTags.AIR))
                .forEach(neighbor -> level.setBlockAndUpdate(neighbor.pos(), BwtBlocks.bloodWoodBlocks.leavesBlock.defaultBlockState()));
    }

    public int countBloodWoodNeighboringOnBlockWithSoulSand(Level level, BlockPos pos) {
        int neighborWoodCount = (int) Arrays.stream(Direction.values())
                .map(pos::relative)
                .map(level::getBlockState)
                .filter(blockState -> blockState.is(this))
                .count();
        if (level.getBlockState(pos.below()).is(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON)) {
            neighborWoodCount += 1;
        }
        return neighborWoodCount;
    }
}

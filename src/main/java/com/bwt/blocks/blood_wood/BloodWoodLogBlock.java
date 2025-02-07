package com.bwt.blocks.blood_wood;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Arrays;

public class BloodWoodLogBlock extends PillarBlock {
    public static final IntProperty POS_NEG = IntProperty.of("pos_neg", 0, 1);
    public static final BooleanProperty CAN_GROW = BooleanProperty.of("can_grow");

    public BloodWoodLogBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CAN_GROW, false).with(POS_NEG, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CAN_GROW, POS_NEG);
    }

    public boolean canGrow(BlockState state) {
        return state.isOf(this) && state.get(CAN_GROW);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return canGrow(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (canGrow(state)) {
            if (world.getDimension().ultrawarm()) {
                grow(world, pos, state, random);
            }
            world.setBlockState(pos, state.with(CAN_GROW, false));
        }
    }

    public static Direction getFacing(BlockState state) {
        Direction.Axis axis = state.get(AXIS);
        Direction.AxisDirection axisDirection = state.get(POS_NEG) > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        return Direction.get(axisDirection, axis);
    }

    public static void setFacing(World world, BlockPos pos, BlockState state, Direction facing) {
        world.setBlockState(pos, withFacing(state, facing));
    }

    public static BlockState withFacing(BlockState state, Direction facing) {
        return state.with(AXIS, facing.getAxis()).with(POS_NEG, facing.getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0);
    }

    public static Direction randomHorizontalDirection(Random random) {
        return Direction.byId(random.nextBetween(2, 5));
    }

    public void grow(ServerWorld serverWorld, BlockPos pos, BlockState state, Random random) {
        if (countBloodWoodNeighboringOnBlockWithSoulSand(serverWorld, pos) >= 2) {
            // too much neighboring wood to grow further
            return;
        }

        Direction facing = getFacing(state);
        int randomFactor = random.nextInt(100);
        if (facing == Direction.UP) {
            // trunk growth
            if (randomFactor < 25) {
                // just continue growing upwards
                attemptToGrowIntoBlock(serverWorld, pos.up(), Direction.UP);
            }
            else if (randomFactor < 90) {
                // split and grow upwards
                Direction targetFacing = randomHorizontalDirection(random);
                BlockPos targetPos = pos.offset(targetFacing);
                attemptToGrowIntoBlock(serverWorld, targetPos, targetFacing);
                attemptToGrowIntoBlock(serverWorld, pos.up(), Direction.UP);
            }
            else {
                // split
                for (int temp = 0; temp < 2; temp++) {
                    Direction targetFacing = randomHorizontalDirection(random);
                    BlockPos targetPos = pos.offset(targetFacing);
                    attemptToGrowIntoBlock(serverWorld, targetPos, targetFacing);
                }
            }
        }
        else {
            // branch growth

            if (randomFactor < 40) {
                // grow upwards
                attemptToGrowIntoBlock(serverWorld, pos.up(), facing);
                // reorient existing block so that it looks right
                setFacing(serverWorld, pos, state, Direction.UP);
            }
            else if (randomFactor < 65) {
                // grow in the growth direction
                attemptToGrowIntoBlock(serverWorld, pos.offset(facing), facing);
            }
            else if (randomFactor < 90) {
                // split and keep going
                Direction targetFacing = randomHorizontalDirection(random);
                if (targetFacing == facing) {
                    targetFacing = Direction.UP;
                }

                BlockPos targetPos = pos.offset(targetFacing);

                Direction targetGrowthDirection = facing;

                if (targetFacing.getId() >= 2 ) {
                    targetGrowthDirection = targetFacing;
                }

                attemptToGrowIntoBlock(serverWorld, targetPos, targetGrowthDirection);

                if (!attemptToGrowIntoBlock(serverWorld, pos.offset(facing), facing) && targetFacing.getId() == 1) {
                    // reorient existing block so that it looks right
                    setFacing(serverWorld, pos, state, Direction.UP);
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

                    BlockPos targetPos = pos.offset(targetFacing);

                    Direction iTargetGrowthDirection = facing;

                    if (targetFacing.getId() >= 2) {
                        iTargetGrowthDirection = targetFacing;
                    }

                    if (attemptToGrowIntoBlock(serverWorld, targetPos, iTargetGrowthDirection)) {
                        growthDirections[iTempCount] = targetFacing;
                    }
                }

                if ((growthDirections[0] == Direction.UP && growthDirections[1].getAxis().isVertical()) || (growthDirections[1] == Direction.UP && growthDirections[0] == Direction.DOWN)) {
                    // reorient existing block so that it looks right
                    setFacing(serverWorld, pos, state, Direction.UP);
                }
            }
        }
    }

    public boolean attemptToGrowIntoBlock(World world, BlockPos pos, Direction growthDirection) {
        BlockState state = world.getBlockState(pos);
        if (!(state.isIn(BlockTags.AIR) || state.isOf(BwtBlocks.bloodWoodBlocks.leavesBlock)) || countBloodWoodNeighboringOnBlockWithSoulSand(world, pos) >= 2) {
            // not empty, or too much neighboring wood to grow further
            return false;
        }
        world.setBlockState(pos, withFacing(getDefaultState(), growthDirection).with(CAN_GROW, true));
        growLeaves(world, pos);

        return true;
    }

    public void growLeaves(World world, BlockPos pos) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (world.getBlockState(pos.add(i, j, k)).isIn(BlockTags.AIR)) {
                        world.setBlockState(pos.add(i, j, k), BwtBlocks.bloodWoodBlocks.leavesBlock.getDefaultState());
                    }
                }
            }
        }
    }

    public int countBloodWoodNeighboringOnBlockWithSoulSand(World world, BlockPos pos) {
        int neighborWoodCount = (int) Arrays.stream(Direction.values())
                .map(pos::offset)
                .map(world::getBlockState)
                .filter(blockState -> blockState.isOf(this))
                .count();
        if (world.getBlockState(pos.down()).isIn(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON)) {
            neighborWoodCount += 1;
        }
        return neighborWoodCount;
    }
}

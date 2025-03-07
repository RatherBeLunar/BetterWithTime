package com.bwt.blocks.lens;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.RotateWithEmptyHand;
import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.utils.BlockPosAndState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

public class LensBlock extends SimpleFacingBlock implements RotateWithEmptyHand {
    public static BooleanProperty LIT = Properties.LIT;

    private final static int lensTickRate = 1;
    private final static float minTriggerLightValue = 12;


    public LensBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT);
    }

    @Override
    public @NotNull BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, this, lensTickRate);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!newState.isOf(this) || !newState.get(FACING).equals(state.get(FACING))) {
            BwtBlocks.lensBeamBlock.killBeam(world, pos, state.get(FACING));
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.getBlockTickScheduler().isTicking(pos, this)) {
            world.scheduleBlockTick(pos, this, lensTickRate);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Direction facing = state.get(FACING);
        boolean isLightDetector = isDirectlyFacingBlockDetector(world, pos, state);

        if (isLightDetector) {
            BlockPos sourcePos = pos.offset(facing.getOpposite());

            int sourceLightValue = world.getLightLevel(sourcePos);

            boolean shouldBeOn =  sourceLightValue >= 8;

            if (state.get(LIT) != shouldBeOn ) {
                setBlockState(world, pos, state.with(LIT, shouldBeOn));
            }

            // schedule another update immediately to check for light changes
            world.scheduleBlockTick(pos, this, lensTickRate);
        }
        else {
            boolean lightOn = hasEnoughDirectInputLight(world, pos, state);
            if (state.get(LIT) != lightOn) {
                setBlockState(world, pos, state.with(LIT, lightOn));
            }
            if (lightOn) {
                BwtBlocks.lensBeamBlock.fireBeam(world, pos, state);
            }
            else {
                BwtBlocks.lensBeamBlock.killBeam(world, pos, facing);
            }
        }
    }


    private boolean hasEnoughDirectInputLight(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);
        Direction targetFacing = facing.getOpposite();

        BlockState targetState = world.getBlockState(pos.offset(targetFacing));

        if (targetState.isIn(BlockTags.AIR) && !targetState.isOf(BwtBlocks.lensBeamBlock)) {
            return false;
        }
        if (targetState.isOf(this)) {
            // Lenses can feed directly into each other
            return targetState.get(LIT) && targetState.get(FACING) == facing;
        }
        if (targetState.getLuminance() > minTriggerLightValue) {
            // only power the lens with a terminus lens beam if it is facing directly into it.
            return !targetState.isOf(BwtBlocks.lensBeamBlock)
                    || targetState.get(LensBeamBlock.FACING_PROPERTIES.get(facing));
        }

        return false;
    }

    private boolean isDirectlyFacingBlockDetector(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);
        BlockPosAndState targetPosAndState = BlockPosAndState.of(world, pos.offset(facing));
        if (!targetPosAndState.state().isOf(BwtBlocks.detectorBlock)) {
            return false;
        }
        return targetPosAndState.state().get(DetectorBlock.FACING) == facing.getOpposite();
    }

    public static void setBlockState(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        for (Direction direction : Direction.values()) {
            if (direction.equals(state.get(FACING).getOpposite())) {
                continue;
            }
            BlockPos targetPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(targetPos);
            world.replaceWithStateForNeighborUpdate(direction.getOpposite(), state, targetPos, pos, Block.NOTIFY_ALL & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS), 511);
            world.updateNeighbor(neighborState, targetPos, state.getBlock(), pos, false);
        }
    }
}

package com.bwt.blocks.detector;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.blocks.lens.LensBlock;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class DetectorBlock extends SimpleFacingBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int tickRate = 4;

    public DetectorBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(POWERED, false);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        level.scheduleTick(pos, this, tickRate);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        boolean detected = checkForDetection(level, pos, state);
        boolean wasDetected = state.getValue(POWERED);
        if (detected != wasDetected) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        boolean placedLogic = placeDetectorLogicIfNecessary(level, pos, state);
        boolean detected = checkForDetection(level, pos, state);
        boolean wasDetected = state.getValue(POWERED);

        if (state.getValue(FACING).equals(Direction.UP)) {
            // facing upwards...check for rain or snow
            detected |= level.canSeeSky(pos.above()) && level.isRainingAt(pos.above());

            // upward facing blocks have to periodically poll for weather changes
            // or they risk missing them.
            level.scheduleTick(pos, this, tickRate);
        }

        if (detected) {
            if (!wasDetected) {
                level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_ALL);
                level.playSound(
                        null,
                        pos,
                        BwtSoundEvents.DETECTOR_CLICK,
                        SoundSource.BLOCKS,
                        1.0f,
                        2f
                );
            }
        }
        else {
            if (wasDetected) {
                if (!placedLogic) {
                    level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_ALL);
                    level.playSound(
                            null,
                            pos,
                            BwtSoundEvents.DETECTOR_CLICK,
                            SoundSource.BLOCKS,
                            1.0f,
                            2f
                    );
                }
                else {
                    // if we just placed the logic block, then wait a tick until we turn off
                    // to give it a chance to detect anything that might be there
                    level.scheduleTick(pos, this, tickRate);
                }
            }
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    /*
     * returns true if a new logic block needed to be placed
     */
    public boolean placeDetectorLogicIfNecessary(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos targetPos = pos.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(BlockTags.AIR) && !targetState.is(BwtBlocks.detectorLogicBlock) && !targetState.is(BwtBlocks.lensBeamBlock)) {
            level.setBlock(targetPos, BwtBlocks.detectorLogicBlock.defaultBlockState(), Block.UPDATE_ALL, 0);
            return true;
        }
        return false;
    }

    public void removeDetectorLogicIfNecessary(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos targetPos = pos.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(BwtBlocks.detectorLogicBlock) && !DetectorLogicBlock.anyNeighborDetectors(level, targetPos)) {
            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL, 0);
        }
    }


    public boolean checkForDetection(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos targetPos = pos.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.is(BlockTags.AIR) && !targetState.is(BwtBlocks.detectorLogicBlock) && !targetState.is(BwtBlocks.lensBeamBlock)) {
            // We haven't placed the logic block yet, return false for now
            return false;
        }
        if (targetState.is(BwtBlocks.lensBlock) && targetState.getValue(LensBlock.FACING).equals(facing.getOpposite())) {
            return targetState.getValue(LensBlock.LIT);
        }
        if (!targetState.is(BwtBlocks.detectorLogicBlock)) {
            // Logic block was replaced with something else
            return true;
        }
        // facing upwards...check for rain or snow
        if (state.getValue(FACING).equals(Direction.UP)
                && level.canSeeSky(pos.above()) && level.isRainingAt(pos.above())) {
            return true;
        }
        return DetectorLogicBlock.isEnabled(targetState);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) {
            return;
        }
        Direction facing = state.getValue(FACING);
        BlockPos blockPos = pos.relative(facing);
        if (level.getBlockState(blockPos).isSolidRender(level, blockPos)) return;
        Direction.Axis axis = facing.getAxis();
        double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)facing.getStepX() : (double)random.nextFloat();
        double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)facing.getStepY() : (double)random.nextFloat();
        double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)facing.getStepZ() : (double)random.nextFloat();
        level.addParticle(DustParticleOptions.REDSTONE, (double)pos.getX() + e, (double)pos.getY() + f, (double)pos.getZ() + g, 0.0, 0.0, 0.0);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, level, pos, newState, moved);
        if (!newState.is(this)) {
            removeDetectorLogicIfNecessary(level, pos, state);
        }
    }
}

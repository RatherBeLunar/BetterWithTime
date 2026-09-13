package com.bwt.blocks.lens;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.RotateWithEmptyHand;
import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.blocks.detector.DetectorBlock;
import com.bwt.utils.BlockPosAndState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class LensBlock extends SimpleFacingBlock implements RotateWithEmptyHand {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final static int lensTickRate = 1;
    private final static float minTriggerLightValue = 12;


    public LensBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        level.scheduleTick(pos, this, lensTickRate);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, level, pos, newState, moved);
        if (!newState.is(this) || !newState.getValue(FACING).equals(state.getValue(FACING))) {
            LensBeamHelper.killBeam(level, pos, state.getValue(FACING));
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.getBlockTicks().willTickThisTick(pos, this)) {
            level.scheduleTick(pos, this, lensTickRate);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        boolean isLightDetector = isDirectlyFacingBlockDetector(level, pos, state);

        if (isLightDetector) {
            BlockPos sourcePos = pos.relative(facing.getOpposite());

            int sourceLightValue = level.getMaxLocalRawBrightness(sourcePos);

            boolean shouldBeOn =  sourceLightValue >= 8;

            if (state.getValue(LIT) != shouldBeOn ) {
                setBlockState(level, pos, state.setValue(LIT, shouldBeOn));
            }

            // schedule another update immediately to check for light changes
            level.scheduleTick(pos, this, lensTickRate);
        }
        else {
            boolean lightOn = hasEnoughDirectInputLight(level, pos, state);
            if (state.getValue(LIT) != lightOn) {
                setBlockState(level, pos, state.setValue(LIT, lightOn));
            }
            if (lightOn) {
                LensBeamHelper.fireBeam(level, pos, state);
            }
            else {
                LensBeamHelper.killBeam(level, pos, facing);
            }
        }
    }


    private boolean hasEnoughDirectInputLight(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction targetFacing = facing.getOpposite();

        BlockState targetState = level.getBlockState(pos.relative(targetFacing));

        if (targetState.is(BlockTags.AIR) && !targetState.is(BwtBlocks.lensBeamBlock)) {
            return false;
        }
        if (targetState.is(this)) {
            // Lenses can feed directly into each other
            return targetState.getValue(LIT) && targetState.getValue(FACING) == facing;
        }
        if (targetState.getLightEmission() > minTriggerLightValue) {
            // only power the lens with a terminus lens beam if it is facing directly into it.
            return !(targetState.getBlock() instanceof LensBeamBlock)
                    || targetState.getValue(LensBeamBlock.FACING_PROPERTIES.get(facing));
        }

        return false;
    }

    private boolean isDirectlyFacingBlockDetector(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPosAndState targetPosAndState = BlockPosAndState.of(level, pos.relative(facing));
        if (!targetPosAndState.state().is(BwtBlocks.detectorBlock)) {
            return false;
        }
        return targetPosAndState.state().getValue(DetectorBlock.FACING) == facing.getOpposite();
    }

    public static void setBlockState(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        for (Direction direction : Direction.values()) {
            if (direction.equals(state.getValue(FACING).getOpposite())) {
                continue;
            }
            BlockPos targetPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(targetPos);
            level.neighborShapeChanged(direction.getOpposite(), state, targetPos, pos, Block.UPDATE_ALL & ~(Block.UPDATE_NEIGHBORS | Block.UPDATE_SUPPRESS_DROPS), 511);
            level.neighborChanged(neighborState, targetPos, state.getBlock(), pos, false);
        }
    }
}

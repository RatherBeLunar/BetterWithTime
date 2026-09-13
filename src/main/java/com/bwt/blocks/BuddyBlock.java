package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;


public class BuddyBlock extends SimpleFacingBlock implements RotateWithEmptyHand {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final static int tickRate = 5;

    public BuddyBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        // minimal delay same as when the buddy block is changed by a neighbor notification to handle
        // state changes due to being pushed around by a piston
        if (state.is(oldState.getBlock())) {
            return;
        }
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.is(newState.getBlock())) {
            return;
        }
        if (!level.isClientSide && state.getValue(POWERED) && level.getBlockTicks().willTickThisTick(pos, this)) {
            this.updateNeighbors(level, pos, state.setValue(POWERED, false));
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!level.isClientSide()
                && !state.getValue(POWERED)
                && !level.getBlockState(sourcePos).is(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY)
                && !level.getBlockTicks().willTickThisTick(pos, this)
        ) {
            // minimal delay when triggered to avoid notfying neighbors of change in same tick
            // that they are notifying of the original change. Not doing so causes problems
            // with some blocks (like ladders) that haven't finished initializing their state
            // on placement when they send out the notification
            level.scheduleTick(pos, this, 1);
        }
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        boolean powered = state.getValue(POWERED);

        level.setBlock(pos, state.setValue(POWERED, !powered), Block.UPDATE_CLIENTS);

        if (!powered) {
            // schedule another update to turn the block off
            level.scheduleTick(pos, this, tickRate);
            level.playSound(null, pos, BwtSoundEvents.BUDDY_CLICK,
                    SoundSource.BLOCKS, 0.5F, 2F);
        }

        updateNeighbors(level, pos, state);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(FACING) == direction.getOpposite() && state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public void updateNeighbors(Level level, BlockPos pos, BlockState state) {
        BlockPos outputPos = pos.relative(state.getValue(FACING));
        level.neighborChanged(outputPos, this, pos);
        level.updateNeighborsAtExceptFromFacing(outputPos, this, state.getValue(FACING).getOpposite());
    }
}

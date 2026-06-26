package com.bwt.blocks;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;


public class BuddyBlock extends SimpleFacingBlock implements RotateWithEmptyHand {
    public static BooleanProperty POWERED = Properties.POWERED;

    private final static int tickRate = 5;

    public BuddyBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        // minimal delay same as when the buddy block is changed by a neighbor notification to handle
        // state changes due to being pushed around by a piston
        if (state.isOf(oldState.getBlock())) {
            return;
        }
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        if (!world.isClient && state.get(POWERED) && world.getBlockTickScheduler().isTicking(pos, this)) {
            this.updateNeighbors(world, pos, state.with(POWERED, false));
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()
                && !state.get(POWERED)
                && !world.getBlockState(sourcePos).isIn(BwtBlockTags.DOES_NOT_TRIGGER_BUDDY)
                && !world.getBlockTickScheduler().isTicking(pos, this)
        ) {
            // minimal delay when triggered to avoid notfying neighbors of change in same tick
            // that they are notifying of the original change. Not doing so causes problems
            // with some blocks (like ladders) that haven't finished initializing their state
            // on placement when they send out the notification
            world.scheduleBlockTick(pos, this, 1);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        boolean powered = state.get(POWERED);

        world.setBlockState(pos, state.with(POWERED, !powered), Block.NOTIFY_LISTENERS);

        if (!powered) {
            // schedule another update to turn the block off
            world.scheduleBlockTick(pos, this, tickRate);
            world.playSound(null, pos, BwtSoundEvents.BUDDY_CLICK,
                    SoundCategory.BLOCKS, 0.5F, 2F);
        }

        updateNeighbors(world, pos, state);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(FACING) == direction.getOpposite() && state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public void updateNeighbors(World world, BlockPos pos, BlockState state) {
        BlockPos outputPos = pos.offset(state.get(FACING));
        world.updateNeighbor(outputPos, this, pos);
        world.updateNeighborsExcept(outputPos, this, state.get(FACING).getOpposite());
    }
}

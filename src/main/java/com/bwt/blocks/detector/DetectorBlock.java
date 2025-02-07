package com.bwt.blocks.detector;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.SimpleFacingBlock;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DetectorBlock extends SimpleFacingBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final int tickRate = 4;

    public DetectorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(POWERED, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.scheduleBlockTick(pos, this, tickRate);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        boolean detected = checkForDetection(world, pos, state);
        boolean wasDetected = state.get(POWERED);
        if (detected != wasDetected) {
            world.scheduleBlockTick(pos, this, tickRate);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        boolean placedLogic = placeDetectorLogicIfNecessary(world, pos, state);
        boolean detected = checkForDetection(world, pos, state);
        boolean wasDetected = state.get(POWERED);

        if (state.get(FACING).equals(Direction.UP)) {
            // facing upwards...check for rain or snow
            detected |= world.isSkyVisible(pos.up()) && world.hasRain(pos.up());

            // upward facing blocks have to periodically poll for weather changes
            // or they risk missing them.
            world.scheduleBlockTick(pos, this, tickRate);
        }

        if (detected) {
            if (!wasDetected) {
                world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL);
                world.playSound(
                        null,
                        pos,
                        BwtSoundEvents.DETECTOR_CLICK,
                        SoundCategory.BLOCKS,
                        1.0f,
                        2f
                );
            }
        }
        else {
            if (wasDetected) {
                if (!placedLogic) {
                    world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL);
                    world.playSound(
                            null,
                            pos,
                            BwtSoundEvents.DETECTOR_CLICK,
                            SoundCategory.BLOCKS,
                            1.0f,
                            2f
                    );
                }
                else {
                    // if we just placed the logic block, then wait a tick until we turn off
                    // to give it a chance to detect anything that might be there
                    world.scheduleBlockTick(pos, this, tickRate);
                }
            }
        }
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    /*
     * returns true if a new logic block needed to be placed
     */
    public boolean placeDetectorLogicIfNecessary(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);
        BlockPos targetPos = pos.offset(facing);
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.isIn(BlockTags.AIR) && !targetState.isOf(BwtBlocks.detectorLogicBlock) && !targetState.isOf(BwtBlocks.lensBeamBlock)) {
            world.setBlockState(targetPos, BwtBlocks.detectorLogicBlock.getDefaultState());
            return true;
        }
        return false;
    }

    public boolean checkForDetection(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);
        BlockPos targetPos = pos.offset(facing);
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.isOf(Blocks.AIR)) {
            // We haven't placed the logic block yet, return false for now
            return false;
        }
        if (!targetState.isOf(BwtBlocks.detectorLogicBlock)) {
            // Logic block was replaced with something else
            return true;
        }
        // facing upwards...check for rain or snow
        if (state.get(FACING).equals(Direction.UP)
                && world.isSkyVisible(pos.up()) && world.hasRain(pos.up())) {
            return true;
        }
        return DetectorLogicBlock.isEnabled(targetState);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(POWERED)) {
            return;
        }
        Direction facing = state.get(FACING);
        BlockPos blockPos = pos.offset(facing);
        if (world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) return;
        Direction.Axis axis = facing.getAxis();
        double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)facing.getOffsetX() : (double)random.nextFloat();
        double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)facing.getOffsetY() : (double)random.nextFloat();
        double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)facing.getOffsetZ() : (double)random.nextFloat();
        world.addParticle(DustParticleEffect.DEFAULT, (double)pos.getX() + e, (double)pos.getY() + f, (double)pos.getZ() + g, 0.0, 0.0, 0.0);
    }
}

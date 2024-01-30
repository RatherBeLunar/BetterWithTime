package com.bwt.blocks;

import com.bwt.BetterWithTime;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AxleBlock extends PillarBlock {
    public static final IntProperty MECH_POWER = IntProperty.of("mech_power", 0, 4);

    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6f, 6f, 0f, 10f, 10f, 16f);

    public AxleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Z).with(MECH_POWER, 0));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        updatePowerStates(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(MECH_POWER);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction.Axis axis = state.get(AXIS);
        return switch (axis) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getCollisionShape(state, world, pos, context);
    }

    public static boolean isPowered(BlockState blockState) {
        return blockState.get(MECH_POWER) > 0;
    }

    public BlockState getNextOrientation(BlockState blockState) {
        return blockState.with(AXIS, switch (blockState.get(AXIS)) {
            case X -> Direction.Axis.Z;
            case Z -> Direction.Axis.Y;
            case Y -> Direction.Axis.X;
        });
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getMainHandStack().isEmpty()) {
            return ActionResult.PASS;
        }
        BlockState updatedState = getNextOrientation(state);
        world.setBlockState(pos, updatedState);
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                updatedState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 0.25f, world.random.nextFloat() * 0.25F + 0.25F);
        updatePowerStates(updatedState, world, pos);
        return ActionResult.SUCCESS;
    }

    public BlockState breakAxle(World world, BlockPos pos) {
        BlockState airState = Blocks.AIR.getDefaultState();
        world.setBlockState(pos, airState);
        double d = pos.getX() + 0.5;
        double e = pos.getY() + 0.5;
        double f = pos.getZ() + 0.5;
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                BetterWithTime.MECH_BANG_SOUND, SoundCategory.BLOCKS, 0.5f, 1);
        ItemEntity itemEntity = new ItemEntity(world, d, e, f, Items.STICK.getDefaultStack());
        itemEntity.setVelocity(world.random.nextDouble() * -0.01 + 0.02, 0.2, world.random.nextDouble() * -0.01 + 0.02);
        world.spawnEntity(itemEntity);
        return airState;
    }

    public BlockState updatePowerState(BlockState state, BlockState neighborState, BlockPos neighborPos) {
        BlockState updatedState = state;
        if (neighborState.isOf(BwtBlocks.gearBoxBlock) && ((GearBoxBlock) neighborState.getBlock()).isMechPowered(neighborState)) {
            updatedState = updatedState.with(MECH_POWER, 1);
        }
        else if (neighborState.isOf(BwtBlocks.axlePowerSourceBlock) && neighborState.get(AXIS).equals(updatedState.get(AXIS))) {
            updatedState = updatedState.with(MECH_POWER, 1);
        }
        else if (neighborState.isOf(BwtBlocks.axleBlock) && neighborState.get(AXIS).equals(updatedState.get(AXIS))) {
            int neighborPower = neighborState.get(MECH_POWER);
            if (neighborPower > 0) {
                updatedState = updatedState.with(MECH_POWER, neighborPower + 1);
            }
        }
        return updatedState;
    }

    public void updatePowerStates(BlockState state, World world, BlockPos pos) {
        int currentPower = state.get(MECH_POWER);
        Direction.Axis axis = state.get(AXIS);

        int maxPowerNeighbor = 0;
        int greaterPowerNeighbors = 0;
        for (int i: new int[]{-1, 1}) {
            BlockPos neighborPos = pos.offset(axis, i);
            BlockState neighborState = world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();

            int neighborPower = 0;
            if (
                    // Gear Box
                    neighborState.isOf(BwtBlocks.gearBoxBlock)
                    // Powered
                    && ((GearBoxBlock) neighborBlock).isMechPowered(neighborState)
                    // Not getting power from this axle
                    && !neighborPos.offset(neighborState.get(GearBoxBlock.FACING)).equals(pos)
            ) {
                neighborPower = 4;
            }
            else if (
                    neighborState.isOf(BwtBlocks.axlePowerSourceBlock)
                    && neighborState.get(AXIS).equals(axis)
            ) {
                neighborPower = 4;
            }
            else if (
                    neighborState.isOf(BwtBlocks.axleBlock)
                    && neighborState.get(AXIS).equals(axis)) {
                neighborPower = neighborState.get(MECH_POWER);
            }

            if (neighborPower > maxPowerNeighbor) {
                maxPowerNeighbor = neighborPower;
            }

            if (neighborPower > currentPower) {
                greaterPowerNeighbors++;
            }
        }

        if (greaterPowerNeighbors >= 2) {
            // We're getting power from multiple directions at once
            breakAxle(world, pos);
            return;
        }

        int newPower;

        if (maxPowerNeighbor > currentPower) {
            if (maxPowerNeighbor == 1) {
                // Power has overextended
                breakAxle(world, pos);
                return;
            }
            newPower = maxPowerNeighbor - 1;
        }
        else {
            newPower = 0;
        }

        if (newPower != currentPower) {
            world.setBlockState(pos, state.with(MECH_POWER, newPower));
        }



//        BlockState updatedState = state;
//        for (int i : new int[]{-1, 1}) {
//            BlockPos neighborPos = pos.offset(state.get(AXIS), i);
//            updatedState = updatePowerState(updatedState, world, neighborPos);
//        }
//
//        if (updatedState.get(MECH_POWER) >= 4) {
//            updatedState = breakAxle(world, pos);
//            return updatedState;
//        }
//
//        // If no change, we don't need to update
//        if (!updatedState.getEntries().equals(state.getEntries())) {
//            world.setBlockState(pos, updatedState);
//        }
//
//        return updatedState;
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        updatePowerStates(state, world, pos);
    }
}

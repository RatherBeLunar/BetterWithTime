package com.bwt.blocks;

import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AxleBlock extends PillarBlock implements IMechPowerBlock {
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getMainHandStack().isEmpty()) {
            return ActionResult.PASS;
        }
        BlockState updatedState = getNextOrientation(state);
        world.setBlockState(pos, updatedState);
        world.playSound(null, pos, updatedState.getSoundGroup().getPlaceSound(),
                SoundCategory.BLOCKS, 0.25f, world.random.nextFloat() * 0.25F + 0.25F);
        updatePowerStates(updatedState, world, pos);
        return ActionResult.SUCCESS;
    }

    public void breakAxle(World world, BlockPos pos) {
        world.removeBlock(pos, false);
        world.playSound(null, pos, BwtSoundEvents.MECH_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1);
        dropStack(world, pos, Items.STICK.getDefaultStack());
        dropStack(world, pos, BwtItems.hempFiberItem.getDefaultStack());
    }

    @Override
    public boolean canTransferPower(BlockState blockState, Direction direction) {
        return blockState.contains(AXIS) && blockState.get(AXIS).equals(direction.getAxis());
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return blockState.get(MECH_POWER) > 0;
    }

    @Override
    public int getMechPower(BlockState blockState) {
        return blockState.get(MECH_POWER);
    }

    public void updatePowerStates(BlockState state, World world, BlockPos pos) {
        int currentPower = state.get(MECH_POWER);
        Direction.Axis axis = state.get(AXIS);

        int maxPowerNeighbor = 0;
        int greaterPowerNeighbors = 0;
        for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
            Direction direction = Direction.from(axis, axisDirection);
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            int neighborPower = 0;

            if (neighborState.getBlock() instanceof IMechPowerBlock neighborMechPowerBlock) {
                boolean isMechPowered = neighborMechPowerBlock.isMechPowered(neighborState);
                boolean canRepeatPower = neighborMechPowerBlock.canRepeatPower(neighborState, direction);
                boolean canTransferPower = neighborMechPowerBlock.canTransferPower(neighborState, direction);
                if (isMechPowered && canRepeatPower) {
                    neighborPower = 4;
                } else if (canTransferPower) {
                    neighborPower = neighborMechPowerBlock.getMechPower(neighborState);
                }
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
        } else {
            newPower = 0;
        }

        if (newPower != currentPower) {
            world.setBlockState(pos, state.with(MECH_POWER, newPower));
        }
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        updatePowerStates(state, world, pos);
    }
}

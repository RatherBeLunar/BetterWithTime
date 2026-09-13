package com.bwt.blocks.axles;

import com.bwt.blocks.GearBoxBlock;
import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AxleBlock extends RotatedPillarBlock implements AxlePowerLevelGetter, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty MECH_POWER = IntegerProperty.create("mech_power", 0, 3);

    protected static final VoxelShape X_SHAPE = Block.box(0f, 6f, 6f, 16f, 10f, 10f);
    protected static final VoxelShape Y_SHAPE = Block.box(6f, 0f, 6f, 10f, 16f, 10f);
    protected static final VoxelShape Z_SHAPE = Block.box(6f, 6f, 0f, 10f, 10f, 16f);

    public AxleBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Z).setValue(MECH_POWER, 0).setValue(WATERLOGGED, false));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        updatePowerStates(state, level, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return Optional.ofNullable(super.getStateForPlacement(ctx))
                .map(state -> state.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER))
                .orElse(null);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MECH_POWER).add(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext ctx) {
        Direction.Axis axis = state.getValue(AXIS);
        return switch (axis) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, blockGetter, pos, context);
    }

    public BlockState getNextOrientation(BlockState blockState) {
        return blockState.setValue(AXIS, switch (blockState.getValue(AXIS)) {
            case X -> Direction.Axis.Z;
            case Z -> Direction.Axis.Y;
            case Y -> Direction.Axis.X;
        });
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        BlockState updatedState = getNextOrientation(state);
        level.setBlockAndUpdate(pos, updatedState);
        level.playSound(null, pos, updatedState.getSoundType().getPlaceSound(),
                SoundSource.BLOCKS, 0.25f, level.random.nextFloat() * 0.25F + 0.25F);
        updatePowerStates(updatedState, level, pos);
        return InteractionResult.SUCCESS;
    }

    public void breakAxle(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        level.playSound(null, pos, BwtSoundEvents.MECH_EXPLODE, SoundSource.BLOCKS, 0.5f, 1);
        popResource(level, pos, Items.STICK.getDefaultInstance());
        popResource(level, pos, BwtItems.hempFiberItem.getDefaultInstance());
    }

    public void updatePowerStates(BlockState state, Level level, BlockPos pos) {
        int currentPower = state.getValue(MECH_POWER);
        Direction.Axis axis = state.getValue(AXIS);

        int maxPowerNeighbor = 0;
        int greaterPowerNeighbors = 0;
        for (int i: new int[]{-1, 1}) {
            BlockPos neighborPos = pos.relative(axis, i);
            BlockState neighborState = level.getBlockState(neighborPos);

            int neighborPower = 0;
            if (
                    // Gear Box
                    neighborState.getBlock() instanceof GearBoxBlock gearBoxBlock
                    // Powered
                    && gearBoxBlock.isMechPowered(neighborState)
                    // Not getting power from this axle
                    && !neighborPos.relative(neighborState.getValue(GearBoxBlock.FACING)).equals(pos)
            ) {
                neighborPower = 4;
            }
            else if (neighborState.getBlock() instanceof AxlePowerLevelGetter axlePowerLevelGetter) {
                neighborPower = axlePowerLevelGetter.getMechPowerForNeighbor(neighborState, axis);
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
            breakAxle(level, pos);
            return;
        }

        int newPower;

        if (maxPowerNeighbor > currentPower) {
            if (maxPowerNeighbor == 1) {
                // Power has overextended
                breakAxle(level, pos);
                return;
            }
            newPower = maxPowerNeighbor - 1;
        }
        else {
            newPower = 0;
        }

        if (newPower != currentPower) {
            level.setBlockAndUpdate(pos, state.setValue(MECH_POWER, newPower));
        }
    }

    @Override
    public int getMechPowerForNeighbor(BlockState state, Direction.Axis axis) {
        return state.getValue(AXIS).equals(axis) ? state.getValue(MECH_POWER) : 0;
    }


    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        updatePowerStates(state, level, pos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type.equals(PathComputationType.WATER) && state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}

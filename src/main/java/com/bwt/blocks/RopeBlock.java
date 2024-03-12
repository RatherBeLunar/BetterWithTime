package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RopeBlock extends Block {
    public static final VoxelShape SHAPE = Block.createCuboidShape(7, 0, 7, 9, 16, 9);
    public static final VoxelShape ANCHORED_ABOVE_SHAPE = Block.createCuboidShape(7, 16, 7, 9, 24, 9);
    public static final VoxelShape ANCHORED_BELOW_SHAPE = Block.createCuboidShape(7, 16, 7, 9, 24, 9);

    public static final BooleanProperty ANCHORED_ABOVE = BooleanProperty.of("anchored_above");
    public static final BooleanProperty ANCHORED_BELOW = BooleanProperty.of("anchored_below");

    public RopeBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ANCHORED_ABOVE, ANCHORED_BELOW);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                SHAPE,
                state.get(ANCHORED_ABOVE) ? ANCHORED_ABOVE_SHAPE : VoxelShapes.empty(),
                state.get(ANCHORED_BELOW) ? ANCHORED_BELOW_SHAPE : VoxelShapes.empty()
        );
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (!fluidState.isEmpty()) {
            return null;
        }
        BlockState upState = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
        BlockState downState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        if (stateValid(upState)) {
            return getDefaultState()
                    .with(ANCHORED_ABOVE, upState.isOf(BwtBlocks.anchorBlock) && upState.get(AnchorBlock.FACING) != Direction.UP)
                    .with(ANCHORED_BELOW, downState.isOf(BwtBlocks.anchorBlock) && downState.get(AnchorBlock.FACING) != Direction.DOWN);
        }
        return null;
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.getMainHandStack().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return stateValid(world.getBlockState(pos.up()));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        return switch (direction) {
            case UP -> state.with(ANCHORED_ABOVE, neighborState.isOf(BwtBlocks.anchorBlock) && neighborState.get(AnchorBlock.FACING) != Direction.UP);
            case DOWN -> state.with(ANCHORED_BELOW, neighborState.isOf(BwtBlocks.anchorBlock) && neighborState.get(AnchorBlock.FACING) != Direction.DOWN);
            default -> state;
        };
    }

    public boolean stateValid(BlockState upState) {
        return upState.isOf(BwtBlocks.ropeBlock)
                || (upState.isOf(BwtBlocks.anchorBlock) && !upState.get(AnchorBlock.FACING).equals(Direction.UP)
                || upState.isOf(BwtBlocks.pulleyBlock));
    }

    public static BlockPos getLowestRope(World world, BlockPos attachmentPos) {
        BlockPos.Mutable mutablePos = attachmentPos.mutableCopy();
        while (world.getBlockState(mutablePos.down()).isOf(BwtBlocks.ropeBlock)) {
            mutablePos.move(Direction.DOWN);
        }
        return mutablePos.toImmutable();
    }
}

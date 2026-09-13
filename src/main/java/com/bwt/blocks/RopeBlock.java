package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RopeBlock extends Block {
    public static final VoxelShape SHAPE = Block.box(7, 0, 7, 9, 16, 9);
    public static final VoxelShape ANCHORED_ABOVE_SHAPE = Block.box(7, 16, 7, 9, 26, 9);
    public static final VoxelShape ANCHORED_BELOW_SHAPE = Block.box(7, -10, 7, 9, 0, 9);

    public static final BooleanProperty ANCHORED_ABOVE = BooleanProperty.create("anchored_above");
    public static final BooleanProperty ANCHORED_BELOW = BooleanProperty.create("anchored_below");

    public RopeBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(ANCHORED_BELOW, false).setValue(ANCHORED_ABOVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ANCHORED_ABOVE, ANCHORED_BELOW);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(
                SHAPE,
                state.getValue(ANCHORED_ABOVE) ? ANCHORED_ABOVE_SHAPE : Shapes.empty(),
                state.getValue(ANCHORED_BELOW) ? ANCHORED_BELOW_SHAPE : Shapes.empty()
        );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        if (!fluidState.isEmpty()) {
            return null;
        }
        BlockState upState = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
        BlockState downState = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        if (stateValid(upState)) {
            return defaultBlockState()
                    .setValue(ANCHORED_ABOVE, upState.is(BwtBlocks.anchorBlock) && upState.getValue(AnchorBlock.FACING) != Direction.UP)
                    .setValue(ANCHORED_BELOW, downState.is(BwtBlocks.anchorBlock) && downState.getValue(AnchorBlock.FACING) != Direction.DOWN);
        }
        return null;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player.getMainHandItem().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return stateValid(level.getBlockState(pos.above()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return switch (direction) {
            case UP -> state.setValue(ANCHORED_ABOVE, neighborState.is(BwtBlocks.anchorBlock) && neighborState.getValue(AnchorBlock.FACING) != Direction.UP);
            case DOWN -> state.setValue(ANCHORED_BELOW, neighborState.is(BwtBlocks.anchorBlock) && neighborState.getValue(AnchorBlock.FACING) != Direction.DOWN);
            default -> state;
        };
    }

    public boolean stateValid(BlockState upState) {
        return upState.is(BwtBlocks.ropeBlock)
                || (upState.is(BwtBlocks.anchorBlock) && !upState.getValue(AnchorBlock.FACING).equals(Direction.UP)
                || upState.is(BwtBlocks.pulleyBlock));
    }

    public static BlockPos getBottomRopePos(Level level, BlockPos attachmentPos) {
        BlockPos.MutableBlockPos mutablePos = attachmentPos.mutable();
        while (level.getBlockState(mutablePos.below()).is(BwtBlocks.ropeBlock)) {
            mutablePos.move(Direction.DOWN);
        }
        return mutablePos.immutable();
    }
}

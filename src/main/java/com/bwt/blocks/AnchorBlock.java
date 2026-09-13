package com.bwt.blocks;

import com.bwt.items.BwtItems;
import com.bwt.sounds.BwtSoundEvents;
import com.bwt.utils.BlockUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnchorBlock extends SimpleFacingBlock {
    public static final BooleanProperty CONNECTED_ABOVE = BooleanProperty.create("connected_above");
    public static final BooleanProperty CONNECTED_BELOW = BooleanProperty.create("connected_below");

    public static final AABB baseBox = new AABB(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    protected static final VoxelShape NUB_SHAPE = Block.box(6, 6, 6, 10, 10, 10);
    protected static final List<VoxelShape> SHAPES = Arrays.stream(Direction.values())
            .map(direction -> Shapes.or(BlockUtils.rotateCuboidFromUp(direction, baseBox), NUB_SHAPE))
            .collect(Collectors.toList());

    public AnchorBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED_ABOVE, CONNECTED_BELOW);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING).get3DDataValue());
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState upState = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
        BlockState downState = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        BlockState placementState = defaultBlockState().setValue(FACING, ctx.getClickedFace());
        return placementState
                .setValue(CONNECTED_ABOVE, placementState.getValue(FACING) != Direction.DOWN && (upState.is(BwtBlocks.ropeBlock) || upState.is(BwtBlocks.pulleyBlock)))
                .setValue(CONNECTED_BELOW, placementState.getValue(FACING) != Direction.UP && (downState.is(BwtBlocks.ropeBlock)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return switch (direction) {
            case UP -> state.setValue(CONNECTED_ABOVE,
                    (state.getValue(FACING) != Direction.DOWN && neighborState.is(BwtBlocks.ropeBlock))
                            || (state.getValue(FACING) == Direction.UP && neighborState.is(BwtBlocks.pulleyBlock)));
            case DOWN -> state.setValue(CONNECTED_BELOW,
                    (state.getValue(FACING) != Direction.UP && neighborState.is(BwtBlocks.ropeBlock)));
            default -> state;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.getMainHandItem().is(BwtItems.ropeItem)) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }
        BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.DOWN);
        while (level.getBlockState(mutablePos.below()).is(BwtBlocks.ropeBlock)) {
            mutablePos.move(Direction.DOWN);
        }
        if (!level.getBlockState(mutablePos).is(BwtBlocks.ropeBlock)) {
            return InteractionResult.FAIL;
        }
        player.getInventory().placeItemBackInInventory(BwtItems.ropeItem.getDefaultInstance());
        level.setBlockAndUpdate(mutablePos, Blocks.AIR.defaultBlockState());
        level.playSound(null, pos, BwtSoundEvents.ANCHOR_RETRACT, SoundSource.PLAYERS, 0.2f, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 1.4f + 2.0f);

        return InteractionResult.SUCCESS;
    }
}

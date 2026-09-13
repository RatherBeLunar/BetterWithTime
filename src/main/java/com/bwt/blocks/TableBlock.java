package com.bwt.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TableBlock extends DecorativeBlock {
    public static final BooleanProperty SUPPORT = BooleanProperty.create("support");

    final VoxelShape BASE_SHAPE = Block.box(0, 15, 0, 16, 16, 16);
    final VoxelShape SUPPORT_SHAPE = Block.box(6, 0, 6, 10, 15, 10);

    public TableBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
        registerDefaultState(defaultBlockState().setValue(SUPPORT, true));
    }

    public static TableBlock ofBlock(Block fullBlock) {
        return new TableBlock(Properties.ofFullCopy(fullBlock), fullBlock);
    }

    public static TableBlock ofWoodBlock(Block woodBlock) {
        TableBlock tableBlock = ofBlock(woodBlock);
        tableBlock.isWood = true;
        return tableBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SUPPORT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        return state.getValue(SUPPORT) ? Shapes.or(BASE_SHAPE, SUPPORT_SHAPE) : BASE_SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(SUPPORT,
                Arrays.stream(Direction.Axis.values())
                        .filter(Direction.Axis::isHorizontal)
                        .noneMatch(axis -> ctx.getLevel().getBlockState(ctx.getClickedPos().relative(axis, 1)).is(this) && ctx.getLevel().getBlockState(ctx.getClickedPos().relative(axis, -1)).is(this))
        );
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos).setValue(SUPPORT,
            Arrays.stream(Direction.Axis.values())
                    .filter(Direction.Axis::isHorizontal)
                    .noneMatch(axis -> level.getBlockState(pos.relative(axis, 1)).is(this) && level.getBlockState(pos.relative(axis, -1)).is(this))
        );
    }
}

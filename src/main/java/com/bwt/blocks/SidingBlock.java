package com.bwt.blocks;

import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SidingBlock extends MiniBlock {
    public static final DirectionProperty FACING = Properties.FACING;
    protected static final Box BOTTOM_SHAPE = new Box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, BOTTOM_SHAPE))
            .toList();
    public static final MapCodec<SidingBlock> CODEC = SidingBlock.createCodec(s -> new SidingBlock(s, Blocks.STONE));

    public SidingBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    public static SidingBlock ofBlock(Block fullBlock) {
        return new SidingBlock(Settings.copy(fullBlock), fullBlock);
    }

    public MapCodec<? extends SidingBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES.get(state.get(FACING).getId());
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public static boolean isHorizontal(BlockState state) {
        return state.get(FACING).getAxis().isHorizontal();
    }
}

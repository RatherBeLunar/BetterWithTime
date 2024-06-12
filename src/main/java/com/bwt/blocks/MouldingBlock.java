package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MouldingBlock extends MiniBlock {
    public static final IntProperty ORIENTATION = IntProperty.of("orientation", 0, 11);


    private final List<VoxelShape> COLLISION_SHAPES = List.of(
            // horizontal, bottom - west, north, east, south
            Block.createCuboidShape(0, 0, 0, 16, 8, 8),
            Block.createCuboidShape(8, 0, 0, 16, 8, 16),
            Block.createCuboidShape(0, 0, 8, 16, 8, 16),
            Block.createCuboidShape(0, 0, 0, 8, 8, 16),
            // vertical - west, north, east, south
            Block.createCuboidShape(0, 0, 0, 8, 16, 8),
            Block.createCuboidShape(8, 0, 0, 16, 16, 8),
            Block.createCuboidShape(8, 0, 8, 16, 16, 16),
            Block.createCuboidShape(0, 0, 8, 8, 16, 16),
            // horizontal, top - west, north, east, south
            Block.createCuboidShape(0, 8, 0, 16, 16, 8),
            Block.createCuboidShape(8, 8, 0, 16, 16, 16),
            Block.createCuboidShape(0, 8, 8, 16, 16, 16),
            Block.createCuboidShape(0, 8, 0, 8, 16, 16)
    );

    public static final MapCodec<MouldingBlock> CODEC = MouldingBlock.createCodec(s -> new MouldingBlock(s, Blocks.STONE));

    public MouldingBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
        this.setDefaultState(this.getDefaultState().with(ORIENTATION, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ORIENTATION);
    }

    public static MouldingBlock ofBlock(Block fullBlock, Block slabBlock) {
        return new MouldingBlock(Settings.copy(slabBlock), fullBlock);
    }

    public MapCodec<? extends MouldingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES.get(state.get(ORIENTATION));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        BlockPos hitBlockPos = ctx.getBlockPos();
        Vec3d hitPos = ctx.getHitPos().subtract(hitBlockPos.getX(), hitBlockPos.getY(), hitBlockPos.getZ());
        double xDistFromCenter = hitPos.getX() - 0.5;
        double yDistFromCenter = hitPos.getY() - 0.5;
        double zDistFromCenter = hitPos.getZ() - 0.5;
        double absXDistFromCenter = Math.abs(xDistFromCenter);
        double absYDistFromCenter = Math.abs(yDistFromCenter);
        double absZDistFromCenter = Math.abs(zDistFromCenter);
        double minDist = absXDistFromCenter < absYDistFromCenter
                ? absXDistFromCenter < absZDistFromCenter
                    ? xDistFromCenter : zDistFromCenter
                : absYDistFromCenter < absZDistFromCenter
                    ? yDistFromCenter : zDistFromCenter;

        int orientation =
            minDist == xDistFromCenter ?
                    yDistFromCenter > 0
                        ? zDistFromCenter > 0 ? 10 : 8
                        : zDistFromCenter > 0 ? 2 : 0
            : minDist == yDistFromCenter ?
                    xDistFromCenter > 0
                        ? zDistFromCenter > 0 ? 6 : 5
                        : zDistFromCenter > 0 ? 7 : 4
            : minDist == zDistFromCenter ?
                    yDistFromCenter > 0
                        ? xDistFromCenter > 0 ? 9 : 11
                        : xDistFromCenter > 0 ? 1 : 3
            : 0;

        return state.with(ORIENTATION, orientation);
    }

    @Override
    public BlockState getNextOrientation(BlockState state) {
        return state.with(ORIENTATION, (state.get(ORIENTATION) + 1) % 12);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        int orientation = state.get(ORIENTATION);
        int category = orientation / 4;
        int newOrientation = (orientation + 1) % 4 + (4 * category);
        return state.with(ORIENTATION, newOrientation);
    }

    public static boolean isVertical(BlockState state) {
        int orientation = state.get(ORIENTATION);
        return orientation >= 4 && orientation <= 7;
    }
}

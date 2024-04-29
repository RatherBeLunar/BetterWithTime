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

public class CornerBlock extends MiniBlock {
    public static final IntProperty ORIENTATION = IntProperty.of("orientation", 0, 7);

    private final List<VoxelShape> COLLISION_SHAPES = List.of(
            // bottom - south-west, north-west, north-east, south-east
            Block.createCuboidShape(0, 0, 0, 8, 8, 8),
            Block.createCuboidShape(8, 0, 0, 16, 8, 8),
            Block.createCuboidShape(8, 0, 8, 16, 8, 16),
            Block.createCuboidShape(0, 0, 8, 8, 8, 16),
            // top - south-west, north-west, north-east, south-east
            Block.createCuboidShape(0, 8, 0, 8, 16, 8),
            Block.createCuboidShape(8, 8, 0, 16, 16, 8),
            Block.createCuboidShape(8, 8, 8, 16, 16, 16),
            Block.createCuboidShape(0, 8, 8, 8, 16, 16)
    );

    public static final MapCodec<CornerBlock> CODEC = CornerBlock.createCodec(s -> new CornerBlock(s, Blocks.STONE));

    public CornerBlock(Settings settings, Block fullBlock) {
        super(settings, fullBlock);
        this.setDefaultState(this.getDefaultState().with(ORIENTATION, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ORIENTATION);
    }

    public static CornerBlock ofBlock(Block fullBlock, Block slabBlock) {
        return new CornerBlock(AbstractBlock.Settings.copy(slabBlock), fullBlock);
    }

    public MapCodec<? extends CornerBlock> getCodec() {
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

        int orientation = (yDistFromCenter > 0 ? 4 : 0) +
                xDistFromCenter > 0
                    ? zDistFromCenter > 0 ? 2 : 1
                    : zDistFromCenter > 0 ? 3 : 0;

        return state.with(ORIENTATION, orientation);
    }

    @Override
    public BlockState getNextOrientation(BlockState state) {
        return state.with(ORIENTATION, (state.get(ORIENTATION) + 1) % 8);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        int orientation = state.get(ORIENTATION);
        int category = orientation / 4;
        int newOrientation = (orientation + 1) % 4 + (4 * category);
        return state.with(ORIENTATION, newOrientation);
    }
}

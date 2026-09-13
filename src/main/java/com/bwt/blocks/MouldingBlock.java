package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MouldingBlock extends MiniBlock {
    public static final IntegerProperty ORIENTATION = IntegerProperty.create("orientation", 0, 11);


    private final List<VoxelShape> COLLISION_SHAPES = List.of(
            // horizontal, bottom, long side facing: - north, east, south, west
            Block.box(0, 0, 0, 16, 8, 8),
            Block.box(8, 0, 0, 16, 8, 16),
            Block.box(0, 0, 8, 16, 8, 16),
            Block.box(0, 0, 0, 8, 8, 16),
            // vertical, long side facing: north-west, north-east, south-east, south-west
            Block.box(0, 0, 0, 8, 16, 8),
            Block.box(8, 0, 0, 16, 16, 8),
            Block.box(8, 0, 8, 16, 16, 16),
            Block.box(0, 0, 8, 8, 16, 16),
            // horizontal, top, long side facing: north, east, south, west
            Block.box(0, 8, 0, 16, 16, 8),
            Block.box(8, 8, 0, 16, 16, 16),
            Block.box(0, 8, 8, 16, 16, 16),
            Block.box(0, 8, 0, 8, 16, 16)
    );

    public static final MapCodec<MouldingBlock> CODEC = MouldingBlock.simpleCodec(s -> new MouldingBlock(s, Blocks.STONE));

    public MouldingBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
        this.registerDefaultState(this.defaultBlockState().setValue(ORIENTATION, 0));
    }

    public static MouldingBlock ofBlock(Block fullBlock) {
        return new MouldingBlock(Properties.ofFullCopy(fullBlock), fullBlock);
    }

    public static MouldingBlock ofWoodBlock(Block woodBlock) {
        MouldingBlock mouldingBlock = ofBlock(woodBlock);
        mouldingBlock.isWood = true;
        return mouldingBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION);
    }

    public MapCodec<? extends MouldingBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES.get(state.getValue(ORIENTATION));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
        BlockPos hitBlockPos = ctx.getClickedPos();
        Vec3 hitPos = ctx.getClickLocation().subtract(hitBlockPos.getX(), hitBlockPos.getY(), hitBlockPos.getZ());
        double xDistFromCenter = hitPos.x() - 0.5;
        double yDistFromCenter = hitPos.y() - 0.5;
        double zDistFromCenter = hitPos.z() - 0.5;
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

        return state.setValue(ORIENTATION, orientation);
    }

    @Override
    public BlockState getNextOrientation(BlockState state) {
        return state.setValue(ORIENTATION, (state.getValue(ORIENTATION) + 1) % 12);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        int orientation = state.getValue(ORIENTATION);
        int category = orientation / 4;
        int newOrientation = (orientation + 1) % 4 + (4 * category);
        return state.setValue(ORIENTATION, newOrientation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        int orientation = state.getValue(ORIENTATION);
        int newOrientation = switch (mirror) {
            // Invert Z
            case LEFT_RIGHT -> switch (orientation) {
                case 0 -> 2;
                case 2 -> 0;
                case 4 -> 7;
                case 5 -> 6;
                case 6 -> 5;
                case 7 -> 4;
                case 8 -> 10;
                case 10 -> 8;
                default -> orientation;
            };
            // Invert X
            case FRONT_BACK -> switch (orientation) {
                case 1 -> 3;
                case 3 -> 1;
                case 4 -> 5;
                case 5 -> 4;
                case 6 -> 7;
                case 7 -> 6;
                case 9 -> 11;
                case 11 -> 9;
                default -> orientation;
            };
            default -> orientation;
        };
        return state.setValue(ORIENTATION, newOrientation);
    }

    public static boolean isVertical(BlockState state) {
        int orientation = state.getValue(ORIENTATION);
        return orientation >= 4 && orientation <= 7;
    }
}

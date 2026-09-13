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

public class CornerBlock extends MiniBlock {
    public static final IntegerProperty ORIENTATION = IntegerProperty.create("orientation", 0, 7);

    private final List<VoxelShape> COLLISION_SHAPES = List.of(
            // bottom - south-west, north-west, north-east, south-east
            Block.box(0, 0, 0, 8, 8, 8),
            Block.box(8, 0, 0, 16, 8, 8),
            Block.box(8, 0, 8, 16, 8, 16),
            Block.box(0, 0, 8, 8, 8, 16),
            // top - south-west, north-west, north-east, south-east
            Block.box(0, 8, 0, 8, 16, 8),
            Block.box(8, 8, 0, 16, 16, 8),
            Block.box(8, 8, 8, 16, 16, 16),
            Block.box(0, 8, 8, 8, 16, 16)
    );

    public static final MapCodec<CornerBlock> CODEC = CornerBlock.simpleCodec(s -> new CornerBlock(s, Blocks.STONE));

    public CornerBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
        this.registerDefaultState(this.defaultBlockState().setValue(ORIENTATION, 0));
    }

    public static CornerBlock ofBlock(Block fullBlock) {
        return new CornerBlock(Properties.ofFullCopy(fullBlock), fullBlock);
    }

    public static CornerBlock ofWoodBlock(Block woodBlock) {
        CornerBlock cornerBlock = ofBlock(woodBlock);
        cornerBlock.isWood = true;
        return cornerBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION);
    }

    public MapCodec<? extends CornerBlock> codec() {
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

        int orientation = (yDistFromCenter > 0 ? 4 : 0) +
                (xDistFromCenter > 0
                    ? zDistFromCenter > 0 ? 2 : 1
                    : zDistFromCenter > 0 ? 3 : 0);

        return state.setValue(ORIENTATION, orientation);
    }

    @Override
    public BlockState getNextOrientation(BlockState state) {
        return state.setValue(ORIENTATION, (state.getValue(ORIENTATION) + 1) % 8);
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
                case 0 -> 1;
                case 1 -> 0;
                case 2 -> 3;
                case 3 -> 2;
                case 4 -> 5;
                case 5 -> 4;
                case 6 -> 7;
                case 7 -> 6;
                default -> orientation;
            };
            // Invert X
            case FRONT_BACK -> switch (orientation) {
                case 0 -> 3;
                case 1 -> 2;
                case 2 -> 1;
                case 3 -> 0;
                case 4 -> 7;
                case 5 -> 6;
                case 6 -> 5;
                case 7 -> 4;
                default -> orientation;
            };
            default -> orientation;
        };
        return state.setValue(ORIENTATION, newOrientation);
    }
}

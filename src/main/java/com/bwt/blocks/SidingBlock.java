package com.bwt.blocks;

import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SidingBlock extends MiniBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final AABB BOTTOM_SHAPE = new AABB(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> BlockUtils.rotateCuboidFromUp(direction, BOTTOM_SHAPE))
            .toList();
    public static final MapCodec<SidingBlock> CODEC = SidingBlock.simpleCodec(s -> new SidingBlock(s, Blocks.STONE));

    public SidingBlock(Properties settings, Block fullBlock) {
        super(settings, fullBlock);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public static SidingBlock ofBlock(Block fullBlock) {
        return new SidingBlock(Properties.ofFullCopy(fullBlock), fullBlock);
    }

    public static SidingBlock ofWoodBlock(Block woodBlock) {
        SidingBlock sidingBlock = ofBlock(woodBlock);
        sidingBlock.isWood = true;
        return sidingBlock;
    }

    public MapCodec<? extends SidingBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES.get(state.getValue(FACING).get3DDataValue());
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite())
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    public static boolean isHorizontal(BlockState state) {
        return state.getValue(FACING).getAxis().isHorizontal();
    }
}

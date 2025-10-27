package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Predicate;

public class ScrewPumpBlock extends Block implements MechPowerBlockBase {
    public static final MapCodec<ScrewPumpBlock> CODEC = createCodec(ScrewPumpBlock::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty BOTTOM_CONNECTED = BooleanProperty.of("bottom_connected");
    public static final BooleanProperty LEFT_CONNECTED = BooleanProperty.of("left_connected");
    public static final BooleanProperty RIGHT_CONNECTED = BooleanProperty.of("right_connected");
    public static final BooleanProperty BACK_CONNECTED = BooleanProperty.of("back_connected");

    public ScrewPumpBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(MECH_POWERED, false)
                .with(BOTTOM_CONNECTED, false)
                .with(LEFT_CONNECTED, false)
                .with(RIGHT_CONNECTED, false)
                .with(BACK_CONNECTED, false)
        );
    }

    @Override
    protected MapCodec<ScrewPumpBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING, BOTTOM_CONNECTED, LEFT_CONNECTED, RIGHT_CONNECTED, BACK_CONNECTED);
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return MechPowerBlockBase.super.isMechPowered(blockState);
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> !direction.equals(blockState.getOrEmpty(FACING).orElse(null)) && !direction.equals(Direction.UP);
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return getValidAxleInputFaces(blockState, pos).and(direction -> !direction.equals(Direction.DOWN));
    }
}

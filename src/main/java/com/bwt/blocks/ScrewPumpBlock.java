package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class ScrewPumpBlock extends Block implements MechPowerBlockBase {
    public static final MapCodec<ScrewPumpBlock> CODEC = simpleCodec(ScrewPumpBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty JAMMED = BooleanProperty.create("jammed");

    protected static final int tickRate = 20;

    public ScrewPumpBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(MECH_POWERED, false).setValue(JAMMED, false));
    }

    @Override
    protected MapCodec<ScrewPumpBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(FACING);
        builder.add(JAMMED);
    }

    @Override
    public boolean isMechPowered(BlockState blockState) {
        return MechPowerBlockBase.super.isMechPowered(blockState);
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction.equals(Direction.DOWN);
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction ->
                direction.getAxis().isHorizontal()
                && !direction.equals(blockState.getOptionalValue(FACING).orElse(null));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        schedulePowerUpdate(state, level, pos);
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        if (isReceivingMechPower(level, state, pos) != isMechPowered(state)) {
            level.scheduleTick(pos, this, tickRate);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        schedulePowerUpdate(state, level, pos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}

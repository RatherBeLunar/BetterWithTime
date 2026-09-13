package com.bwt.blocks.turntable;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.MechPowerBlockBase;
import com.bwt.sounds.BwtSoundEvents;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TurntableBlock extends BaseEntityBlock implements MechPowerBlockBase {
    public static final int turntableTickRate = 10;

    public static final IntegerProperty TICK_SETTING = IntegerProperty.create("tick_setting", 0, 3);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public TurntableBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(MECH_POWERED, false).setValue(POWERED, false).setValue(TICK_SETTING, 0));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TICK_SETTING, MECH_POWERED, POWERED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurntableBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        schedulePowerUpdate(state, level, pos);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        level.setBlockAndUpdate(pos, state.setValue(TICK_SETTING, (state.getValue(TICK_SETTING) + 1) % 4));
        level.playSound(null, pos, BwtSoundEvents.TURNTABLE_SETTING_CLICK,
                SoundSource.BLOCKS, 0.25f, 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction == Direction.DOWN;
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return direction -> false;
    }

    public BlockState getPowerStates(BlockState state, Level level, BlockPos pos) {
        boolean redstonePowered = level.hasNeighborSignal(pos);
        boolean mechPowered = isReceivingMechPower(level, state, pos);
        BlockState updatedState = state;
        updatedState = updatedState.setValue(POWERED, redstonePowered);
        updatedState = updatedState.setValue(MECH_POWERED, mechPowered);
        return updatedState;
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, level, pos);
        // If block just turned on
        if (newState.getValue(POWERED) != state.getValue(POWERED) || newState.getValue(MECH_POWERED) != state.getValue(MECH_POWERED)) {
            level.scheduleTick(pos, this, turntableTickRate);
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
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState updatedState = getPowerStates(blockState, level, pos);
        level.setBlockAndUpdate(pos, updatedState);
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(Level level, BlockEntityType<A> givenType) {
        return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(givenType, BwtBlockEntities.turntableBlockEntity, TurntableBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TurntableBlock.validateTicker(level, type);
    }
}

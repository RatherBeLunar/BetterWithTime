package com.bwt.blocks.pulley;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.MechPowerBlockBase;
import com.bwt.sounds.BwtSoundEvents;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.phys.BlockHitResult;

public class PulleyBlock extends BaseEntityBlock implements MechPowerBlockBase {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final int pulleyTickRate = 10;

    public PulleyBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(MECH_POWERED, false).setValue(POWERED, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> !direction.equals(Direction.DOWN);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
//        if (!isMechPowered(state) || state.get(POWERED)) {
//            return;
//        }
//        emitParticles(level, pos, random);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        schedulePowerUpdate(state, level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PulleyBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PulleyBlockEntity pulleyBlockEntity) {
            player.openMenu(pulleyBlockEntity);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(Level level, BlockEntityType<A> givenType) {
        return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(givenType, BwtBlockEntities.pulleyBlockEntity, PulleyBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType) {
        return PulleyBlock.validateTicker(level, givenType);
    }

    public BlockState getPowerStates(BlockState state, Level level, BlockPos pos) {
        return state.setValue(POWERED, level.hasNeighborSignal(pos))
                .setValue(MECH_POWERED, isReceivingMechPower(level, state, pos));
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, level, pos);
        if (newState.getValue(POWERED) != state.getValue(POWERED) || newState.getValue(MECH_POWERED) != state.getValue(MECH_POWERED)) {
            level.scheduleTick(pos, this, pulleyTickRate);
        }
    }



    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        schedulePowerUpdate(state, level, pos);
    }

    public void updatePowerTransfer(Level level, BlockState blockState, BlockPos pos) {
        BlockState updatedState = getPowerStates(blockState, level, pos);
        level.getBlockEntity(pos, BwtBlockEntities.pulleyBlockEntity).ifPresent(pulleyBlockEntity -> pulleyBlockEntity.mechPower = updatedState.getValue(MECH_POWERED) ? 1 : 0);
        level.setBlockAndUpdate(pos, updatedState);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.updatePowerTransfer(level, state, pos);
    }

    private void playMechSound(Level level, BlockPos pos) {
        level.playLocalSound(pos, BwtSoundEvents.MECH_BANG, SoundSource.BLOCKS, 0.125f,  1.25F, false);
    }

    private void emitParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 5; i++) {
            float smokeX = (float)pos.getX() + random.nextFloat();
            float smokeY = (float)pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)pos.getZ() + random.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D );
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }
}

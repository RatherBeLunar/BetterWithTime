package com.bwt.blocks.pulley;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.mechanical.api.ControlledPowerState;
import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.PowerState;
import com.bwt.mechanical.impl.DirectionTools;
import com.bwt.mechanical.impl.MachineBlockWithEntity;
import com.bwt.sounds.BwtSoundEvents;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PulleyBlock extends MachineBlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public static final int pulleyTickRate = 10;

    public PulleyBlock(Settings settings) {
        super(settings, pulleyTickRate);
        setDefaultState(getDefaultState().with(MechPowered.MECH_POWERED, false).with(POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowered.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (!isPowered(state) || state.get(POWERED)) {
            return;
        }
        emitParticles(world, pos, random);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        schedulePowerUpdate(state, world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PulleyBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PulleyBlockEntity pulleyBlockEntity) {
            player.openHandledScreen(pulleyBlockEntity);
        }
        return ActionResult.CONSUME;
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, BwtBlockEntities.pulleyBlockEntity, PulleyBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> givenType) {
        return PulleyBlock.validateTicker(world, givenType);
    }

    @Override
    public PowerState getPowerState(World world, BlockState state, BlockPos pos, Random random) {
        var powerState = super.getPowerState(world, state, pos, random);
        var nowControlled = world.isReceivingRedstonePower(pos);
        var previousControlled = state.get(POWERED);
        return new ControlledPowerState(powerState, previousControlled, nowControlled);
    }

    @Override
    public BlockState asPoweredState(World world, BlockPos pos, BlockState state, PowerState powerState) {
        return super.asPoweredState(world, pos, state, powerState).with(POWERED, ((ControlledPowerState) powerState).nowControlled());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }


    @Override
    public void onPowerChanged(PowerState powerState) {
        super.onPowerChanged(powerState);
        powerState.world().getBlockEntity(powerState.pos(), BwtBlockEntities.pulleyBlockEntity).ifPresent(pulleyBlockEntity -> pulleyBlockEntity.mechPower = powerState.now() ? 1 : 0);
    }


    private void playMechSound(World world, BlockPos pos) {
        world.playSoundAtBlockCenter(pos, BwtSoundEvents.MECH_BANG, SoundCategory.BLOCKS, 0.125f, 1.25F, false);
    }

    private void emitParticles(World world, BlockPos pos, Random random) {
        for (int i = 0; i < 5; i++) {

            float smokeX = (float) pos.getX() + random.nextFloat();
            float smokeY = (float) pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float) pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return DirectionTools.filter(dir -> dir != Direction.DOWN);
    }
}

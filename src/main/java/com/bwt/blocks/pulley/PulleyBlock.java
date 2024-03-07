package com.bwt.blocks.pulley;

import com.bwt.BetterWithTime;
import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.MechPowerBlockBase;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PulleyBlock extends BlockWithEntity implements MechPowerBlockBase {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public static final int pulleyTickRate = 10;

    public PulleyBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MECH_POWERED, false).with(POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
    }

    @Override
    public List<BlockPos> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values()).filter(direction -> !direction.equals(Direction.DOWN)).map(pos::offset).toList();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
//        if (!isMechPowered(state) || state.get(POWERED)) {
//            return;
//        }
//        emitParticles(world, pos, random);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
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

    public BlockState getPowerStates(BlockState state, World world, BlockPos pos) {
        return state.with(POWERED, world.isReceivingRedstonePower(pos))
                .with(MECH_POWERED, isReceivingMechPower(world, state, pos));
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, world, pos);
        if (newState.get(POWERED) != state.get(POWERED) || newState.get(MECH_POWERED) != state.get(MECH_POWERED)) {
            world.scheduleBlockTick(pos, this, pulleyTickRate);
        }
    }



    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    public void updatePowerTransfer(World world, BlockState blockState, BlockPos pos) {
        BlockState updatedState = getPowerStates(blockState, world, pos);
        world.setBlockState(pos, updatedState);
        if (updatedState.get(POWERED).equals(blockState.get(POWERED)) && updatedState.get(MECH_POWERED).equals(blockState.get(MECH_POWERED))) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PulleyBlockEntity pulleyBlockEntity) {
            pulleyBlockEntity.notifyPulleyEntityOfBlockStateChange(world);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.updatePowerTransfer(world, state, pos);
    }

    private void playMechSound(World world, BlockPos pos) {
        world.playSoundAtBlockCenter(pos, BetterWithTime.MECH_BANG_SOUND, SoundCategory.BLOCKS, 0.125f,  1.25F, false);
    }

    private void emitParticles(World world, BlockPos pos, Random random) {
        for (int i = 0; i < 5; i++) {
            float smokeX = (float)pos.getX() + random.nextFloat();
            float smokeY = (float)pos.getY() + random.nextFloat() * 0.5F + 1.0F;
            float smokeZ = (float)pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0D, 0D, 0D );
        }
    }
}

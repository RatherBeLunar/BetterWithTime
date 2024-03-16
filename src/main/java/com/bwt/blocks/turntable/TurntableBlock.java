package com.bwt.blocks.turntable;

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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurntableBlock extends BlockWithEntity implements MechPowerBlockBase {
    public static final int turntableTickRate = 10;

    public static final IntProperty TICK_SETTING = IntProperty.of("tick_setting", 0, 3);
    public static final BooleanProperty POWERED = Properties.POWERED;

    public TurntableBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MECH_POWERED, false).with(POWERED, false).with(TICK_SETTING, 0));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TICK_SETTING, MECH_POWERED, POWERED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TurntableBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        schedulePowerUpdate(state, world, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getMainHandStack().isEmpty()) {
            return ActionResult.PASS;
        }
        world.setBlockState(pos, state.with(TICK_SETTING, (state.get(TICK_SETTING) + 1) % 4));
        world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_DISPENSE,
                SoundCategory.BLOCKS, 0.25f, 1);
        return ActionResult.SUCCESS;
    }

    @Override
    public List<BlockPos> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return List.of(pos.down());
    }

    @Override
    public List<BlockPos> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return List.of();
    }

    public BlockState getPowerStates(BlockState state, World world, BlockPos pos) {
        boolean redstonePowered = world.isReceivingRedstonePower(pos);
        boolean mechPowered = isReceivingMechPower(world, state, pos);
        BlockState updatedState = state;
        updatedState = updatedState.with(POWERED, redstonePowered);
        updatedState = updatedState.with(MECH_POWERED, mechPowered);
        return updatedState;
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        // Compute new state but don't update yet
        BlockState newState = getPowerStates(state, world, pos);
        // If block just turned on
        if (newState.get(POWERED) != state.get(POWERED) || newState.get(MECH_POWERED) != state.get(MECH_POWERED)) {
            world.scheduleBlockTick(pos, this, turntableTickRate);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld world, BlockPos pos, Random random) {
        BlockState updatedState = getPowerStates(blockState, world, pos);
        world.setBlockState(pos, updatedState);
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, BwtBlockEntities.turntableBlockEntity, TurntableBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TurntableBlock.validateTicker(world, type);
    }
}

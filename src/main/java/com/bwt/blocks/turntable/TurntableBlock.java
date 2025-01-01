package com.bwt.blocks.turntable;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.mechanical.api.ControlledPowerState;
import com.bwt.mechanical.api.MechPowered;
import com.bwt.mechanical.api.PowerState;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurntableBlock extends MachineBlockWithEntity {
    public static final int turntableTickRate = 10;

    public static final IntProperty TICK_SETTING = IntProperty.of("tick_setting", 0, 3);
    public static final BooleanProperty POWERED = Properties.POWERED;

    public TurntableBlock(Settings settings) {
        super(settings, turntableTickRate, turntableTickRate);
        setDefaultState(getDefaultState().with(MechPowered.MECH_POWERED, false).with(POWERED, false).with(TICK_SETTING, 0));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TICK_SETTING, POWERED);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getMainHandStack().isEmpty()) {
            return ActionResult.PASS;
        }
        world.setBlockState(pos, state.with(TICK_SETTING, (state.get(TICK_SETTING) + 1) % 4));
        world.playSound(null, pos, BwtSoundEvents.TURNTABLE_SETTING_CLICK,
                SoundCategory.BLOCKS, 0.25f, 1);
        return ActionResult.SUCCESS;
    }

    @Override
    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        super.schedulePowerUpdate(state, world, pos);
    }

    @Override
    public PowerState getPowerState(World world, BlockState state, BlockPos pos, Random random) {
        var powerState = super.getPowerState(world, state, pos, random);
        var nowControlled = world.isReceivingRedstonePower(pos);
        var previousControlled = state.get(POWERED);
        return new ControlledPowerState(powerState, previousControlled, nowControlled){
            @Override
            public boolean isPowered() {
                return this.now();
            }
        };
    }

    @Override
    public BlockState asPoweredState(World world, BlockPos pos, BlockState state, PowerState powerState) {
        return super.asPoweredState(world, pos, state, powerState).with(POWERED, ((ControlledPowerState) powerState).nowControlled());
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

    @Override
    public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return List.of(Direction.DOWN);
    }

}

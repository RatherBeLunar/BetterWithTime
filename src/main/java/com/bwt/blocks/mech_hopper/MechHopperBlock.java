package com.bwt.blocks.mech_hopper;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.MechPowerBlockBase;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MechHopperBlock extends BlockWithEntity implements MechPowerBlockBase {
    public static final MapCodec<MechHopperBlock> CODEC = MechHopperBlock.createCodec(MechHopperBlock::new);

    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.union(
//            Block.createCuboidShape(0, 4, 0, 16, 16, 2),
//            Block.createCuboidShape(0, 4, 0, 2, 16, 16),
//            Block.createCuboidShape(0, 4, 14, 16, 16, 16),
//            Block.createCuboidShape(14, 4, 0, 16, 16, 16),
            Block.createCuboidShape(0, 4, 0, 16, 16, 16),
            Block.createCuboidShape(5, 0, 5, 11, 4, 11)
    );

    public MechHopperBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(MECH_POWERED, false);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MechHopperBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
        boolean isMechPowered = isReceivingMechPower(world, state, pos);
        // If block just turned on
        if (isMechPowered && !isMechPowered(state)) {
            world.scheduleBlockTick(pos, this, MechPowerBlockBase.getTurnOnTickRate());
        }
        // If block just turned off
        else if (!isMechPowered && isMechPowered(state)) {
            world.scheduleBlockTick(pos, this, MechPowerBlockBase.getTurnOffTickRate());
        }
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
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    public BlockState getPowerStates(BlockState state, World world, BlockPos pos) {
        return state.with(MECH_POWERED, isReceivingMechPower(world, state, pos));
    }

    public void updatePowerTransfer(World world, BlockState blockState, BlockPos pos) {
        BlockState updatedState = getPowerStates(blockState, world, pos);
        world.setBlockState(pos, updatedState);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MechHopperBlockEntity hopperBlockEntity) {
            hopperBlockEntity.mechPower = updatedState.get(MechHopperBlock.MECH_POWERED) ? 1 : 0;
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.updatePowerTransfer(world, state, pos);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MechHopperBlockEntity hopperBlockEntity) {
            MechHopperBlockEntity.onEntityCollided(entity, hopperBlockEntity);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MechHopperBlockEntity hopperBlockEntity) {
            player.openHandledScreen(hopperBlockEntity);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public List<BlockPos> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return Arrays.stream(Direction.values())
                .filter(direction -> !direction.getAxis().isVertical())
                .map(pos::offset)
                .toList();
    }

    @Nullable
    protected static <A extends BlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, BwtBlockEntities.mechHopperBlockEntity, MechHopperBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return MechHopperBlock.validateTicker(world, type);
    }

    @Override
    public List<BlockPos> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return MechPowerBlockBase.super.getValidHandCrankFaces(blockState, pos);
    }
}

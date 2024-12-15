package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.mechanical.api.MechPowered;
import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractCookingPotBlock extends BlockWithEntity {
    public static final DirectionProperty TIP_DIRECTION = DirectionProperty.of("tip_direction", direction -> direction != Direction.DOWN);

    public static Box box1 = new Box(1, 0, 1, 15, 16, 15);
    public static Box box2 = new Box(0, 2, 0, 16, 14, 16);
    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> VoxelShapes.union(BlockUtils.rotateCuboidFromUp(direction, box1), BlockUtils.rotateCuboidFromUp(direction, box2)).simplify())
            .toList();

    public AbstractCookingPotBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(TIP_DIRECTION, Direction.UP).with(MechPowered.MECH_POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        MechPowered.appendProperties(builder);
        builder.add(TIP_DIRECTION);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES.get(state.get(TIP_DIRECTION).getId());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

//    @Override
//    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
//        return direction -> direction.getAxis().isHorizontal();
//    }
//
//    @Override
//    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
//        return getValidAxleInputFaces(blockState, pos);
//    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        schedulePowerUpdate(state, world, pos);
    }

    public void schedulePowerUpdate(BlockState state, World world, BlockPos pos) {
//TODO        boolean isMechPowered = getPowerInputFaces(world, pos, state).count() == 1;
//        // If block just turned on
//        if (isMechPowered && !isMechPowered(state)) {
//            world.scheduleBlockTick(pos, this, MechPowerBlockBase.getTurnOnTickRate());
//        }
//        // If block just turned off
//        else if (!isMechPowered && isMechPowered(state)) {
//            world.scheduleBlockTick(pos, this, MechPowerBlockBase.getTurnOffTickRate());
//        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        schedulePowerUpdate(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
//TODO        Optional<Direction> input = getPowerInputFaces(world, pos, state).findFirst();
//        if (input.isPresent() == isMechPowered(state)) {
//            return;
//        }
//        if (input.isEmpty()) {
//            world.setBlockState(pos, state.with(TIP_DIRECTION, Direction.UP).with(MECH_POWERED, false));
//            return;
//        }
//        world.setBlockState(pos, state.with(TIP_DIRECTION, input.get().rotateYClockwise()).with(MECH_POWERED, true));
    }

    @Nullable
    protected static <A extends BlockEntity, E extends AbstractCookingPotBlockEntity> BlockEntityTicker<A> validateTicker(World world, BlockEntityType<A> givenType, BlockEntityType<E> expectedType) {
        return world.isClient ? null : BlockWithEntity.validateTicker(givenType, expectedType, E::tick);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.get(TIP_DIRECTION) != Direction.UP) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractCookingPotBlockEntity cookingPotBlockEntity) {
            AbstractCookingPotBlockEntity.onEntityCollided(entity, cookingPotBlockEntity);
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
}

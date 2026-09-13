package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.blocks.MechPowerBlockBase;
import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractCookingPotBlock extends BaseEntityBlock implements MechPowerBlockBase {
    public static final DirectionProperty TIP_DIRECTION = DirectionProperty.create("tip_direction", direction -> direction != Direction.DOWN);

    public static final AABB box1 = new AABB(1, 0, 1, 15, 16, 15);
    public static final AABB box2 = new AABB(0, 2, 0, 16, 14, 16);
    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> Shapes.or(BlockUtils.rotateCuboidFromUp(direction, box1), BlockUtils.rotateCuboidFromUp(direction, box2)).optimize())
            .toList();
    protected static final VoxelShape SIDES_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public AbstractCookingPotBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(TIP_DIRECTION, Direction.UP).setValue(MECH_POWERED, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        MechPowerBlockBase.super.appendProperties(builder);
        builder.add(TIP_DIRECTION);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPES.get(state.getValue(TIP_DIRECTION).get3DDataValue());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SIDES_SHAPE;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public Predicate<Direction> getValidAxleInputFaces(BlockState blockState, BlockPos pos) {
        return direction -> direction.getAxis().isHorizontal();
    }

    @Override
    public Predicate<Direction> getValidHandCrankFaces(BlockState blockState, BlockPos pos) {
        return getValidAxleInputFaces(blockState, pos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        schedulePowerUpdate(state, level, pos);
    }

    public void schedulePowerUpdate(BlockState state, Level level, BlockPos pos) {
        boolean isMechPowered = getPowerInputFaces(level, pos, state).count() == 1;
        // If block just turned on
        if (isMechPowered && !isMechPowered(state)) {
            level.scheduleTick(pos, this, MechPowerBlockBase.getTurnOnTickRate());
        }
        // If block just turned off
        else if (!isMechPowered && isMechPowered(state)) {
            level.scheduleTick(pos, this, MechPowerBlockBase.getTurnOffTickRate());
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
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Optional<Direction> input = getPowerInputFaces(level, pos, state).findFirst();
        if (input.isPresent() == isMechPowered(state)) {
            return;
        }
        if (input.isEmpty()) {
            level.setBlockAndUpdate(pos, state.setValue(TIP_DIRECTION, Direction.UP).setValue(MECH_POWERED, false));
            return;
        }
        level.setBlockAndUpdate(pos, state.setValue(TIP_DIRECTION, input.get().getClockWise()).setValue(MECH_POWERED, true));
    }

    @Nullable
    protected static <A extends BlockEntity, E extends AbstractCookingPotBlockEntity> BlockEntityTicker<A> validateTicker(Level level, BlockEntityType<A> givenType, BlockEntityType<E> expectedType) {
        return level.isClientSide ? null : BaseEntityBlock.createTickerHelper(givenType, expectedType, E::tick);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        if (state.getValue(TIP_DIRECTION) != Direction.UP) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractCookingPotBlockEntity cookingPotBlockEntity) {
            AbstractCookingPotBlockEntity.onEntityCollided(entity, cookingPotBlockEntity);
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

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(TIP_DIRECTION, rotation.rotate(state.getValue(TIP_DIRECTION)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(TIP_DIRECTION, mirror.mirror(state.getValue(TIP_DIRECTION)));
    }
}

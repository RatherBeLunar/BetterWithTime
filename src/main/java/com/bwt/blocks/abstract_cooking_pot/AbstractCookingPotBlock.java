package com.bwt.blocks.abstract_cooking_pot;

import com.bwt.mechanical.api.IMechPoweredBlock;
import com.bwt.mechanical.api.PowerState;
import com.bwt.mechanical.impl.DirectionTools;
import com.bwt.mechanical.impl.MachineBlockWithEntity;
import com.bwt.utils.BlockUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCookingPotBlock extends MachineBlockWithEntity {
    public static final DirectionProperty TIP_DIRECTION = DirectionProperty.of("tip_direction", direction -> direction != Direction.DOWN);

    public static Box box1 = new Box(1, 0, 1, 15, 16, 15);
    public static Box box2 = new Box(0, 2, 0, 16, 14, 16);
    protected static final List<VoxelShape> COLLISION_SHAPES = Arrays.stream(Direction.values())
            .map(direction -> VoxelShapes.union(BlockUtils.rotateCuboidFromUp(direction, box1), BlockUtils.rotateCuboidFromUp(direction, box2)).simplify())
            .toList();

    public AbstractCookingPotBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(TIP_DIRECTION, Direction.UP).with(IMechPoweredBlock.MECH_POWERED, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        IMechPoweredBlock.appendProperties(builder);
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


    @Override
    public List<Direction> getInputFaces(World world, BlockPos pos, BlockState blockState) {
        return DirectionTools.filter(dir -> dir.getAxis().isHorizontal());
    }

    @Override
    public BlockState asPoweredState(World world, BlockPos pos, BlockState state, PowerState powerState) {
        var tipDirection = powerState.powerDirections().stream().findFirst().map(Direction::rotateYClockwise).orElse(Direction.UP);
        return super.asPoweredState(world, pos, state, powerState).with(TIP_DIRECTION, tipDirection);
    }
}

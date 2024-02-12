package com.bwt.blocks.abstract_cooking_pot;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCookingPotBlock extends BlockWithEntity {
    public static final IntProperty LEVEL = Properties.LEVEL_8;

    public static VoxelShape outlineShape = VoxelShapes.union(
            VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 1, 0.9375),
            VoxelShapes.cuboid(0, 0.125, 0, 1, 0.875, 1)
    ).simplify();

    public AbstractCookingPotBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return outlineShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
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
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractCookingPotBlockEntity cookingPotBlockEntity) {
            AbstractCookingPotBlockEntity.onEntityCollided(entity, cookingPotBlockEntity);
        }
    }
}

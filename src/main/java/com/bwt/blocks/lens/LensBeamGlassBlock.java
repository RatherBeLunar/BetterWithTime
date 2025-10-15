package com.bwt.blocks.lens;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;
import java.util.Map;

public class LensBeamGlassBlock extends LensBeamBlock {
    public static final MapCodec<LensBeamGlassBlock> CODEC = createCodec(s -> new LensBeamGlassBlock(Blocks.GLASS, s));

    public final Block glassBlock;
    protected final BlockState glassState;

    public LensBeamGlassBlock(Block glassBlock, Settings settings) {
        super(settings);
        this.glassBlock = glassBlock;
        this.glassState = glassBlock.getDefaultState();
    }

    @Override
    protected MapCodec<LensBeamGlassBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!(state.getBlock() instanceof LensBeamBlock lensBeamBlock) || state.equals(oldState)) {
            return;
        }
        for (Map.Entry<Direction, BooleanProperty> entry : FACING_PROPERTIES.entrySet()) {
            Direction direction = entry.getKey();
            BooleanProperty facingProperty = entry.getValue();
            BlockState neighborState = world.getBlockState(pos.offset(direction.getOpposite()));
            if (!LensBeamHelper.isValidInputBeamOrLens(neighborState, direction)) {
                state = state.with(facingProperty, false);
            }
            else {
                int range = LensBeamHelper.getRemainingRange(world, pos, direction);
                LensBeamHelper.propagateBeam(world, pos, state, direction, range);
            }
        }
        if (LensBeamHelper.streamFacingDirections(state).findAny().isEmpty()) {
            world.setBlockState(pos, lensBeamBlock.getStateLeftOverWhenEmpty(world, pos));
        }
    }

    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return glassState.getOutlineShape(world, pos, context);
    }

    @Override
    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this)
                || stateFrom.isOf(this.glassBlock)
                || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        glassState.onEntityCollision(world, pos, entity);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return glassState.getDroppedStacks(builder);
    }

    @Override
    public BlockState getStateLeftOverWhenEmpty(WorldAccess world, BlockPos pos) {
        return glassState;
    }
}

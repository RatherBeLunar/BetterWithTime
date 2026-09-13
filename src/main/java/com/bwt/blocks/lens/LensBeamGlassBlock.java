package com.bwt.blocks.lens;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;
import java.util.Map;

public class LensBeamGlassBlock extends LensBeamBlock {
    public static final MapCodec<LensBeamGlassBlock> CODEC = simpleCodec(s -> new LensBeamGlassBlock(Blocks.GLASS, s));

    public final Block glassBlock;
    protected final BlockState glassState;

    public LensBeamGlassBlock(Block glassBlock, Properties settings) {
        super(settings);
        this.glassBlock = glassBlock;
        this.glassState = glassBlock.defaultBlockState();
    }

    @Override
    protected MapCodec<LensBeamGlassBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        if (!(state.getBlock() instanceof LensBeamBlock lensBeamBlock) || state.equals(oldState)) {
            return;
        }
        for (Map.Entry<Direction, BooleanProperty> entry : FACING_PROPERTIES.entrySet()) {
            Direction direction = entry.getKey();
            BooleanProperty facingProperty = entry.getValue();
            BlockState neighborState = level.getBlockState(pos.relative(direction.getOpposite()));
            if (!LensBeamHelper.isValidInputBeamOrLens(neighborState, direction)) {
                state = state.setValue(facingProperty, false);
            }
            else {
                int range = LensBeamHelper.getRemainingRange(level, pos, direction);
                LensBeamHelper.propagateBeam(level, pos, state, direction, range);
            }
        }
        if (LensBeamHelper.streamFacingDirections(state).findAny().isEmpty()) {
            level.setBlockAndUpdate(pos, lensBeamBlock.getStateLeftOverWhenEmpty(level, pos));
        }
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return glassState.getShape(level, pos, context);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.is(this)
                || stateFrom.is(this.glassBlock)
                || super.skipRendering(state, stateFrom, direction);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        glassState.entityInside(level, pos, entity);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return glassState.getDrops(builder);
    }

    @Override
    public BlockState getStateLeftOverWhenEmpty(LevelAccessor level, BlockPos pos) {
        return glassState;
    }
}

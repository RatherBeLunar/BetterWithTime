package com.bwt.blocks.turntable;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashMap;
import java.util.Map;

public interface RotationProcessHelper {
    interface RotationProcessor {
        RotationProcessor DEFAULT = RotationProcessHelper::defaultRotationProcessor;

        void accept(Level level, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity);
    }

    HashMap<Class<? extends Block>, RotationProcessor> processors = new HashMap<>();

    static void register(Class<? extends Block> blockClass, RotationProcessor statePostProcessor) {
        processors.put(blockClass, statePostProcessor);
    }

    static void processRotation(Level level, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity) {
        Block block = rotatedState.getBlock();
        processors.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(RotationProcessor.DEFAULT)
                .accept(level, pos, originalState, rotatedState, rotatingBlockEntity);
    }

    static void registerDefaults() {
        register(RailBlock.class, (level, pos, originalState, rotatedState, rotatingBlockEntity) -> setBlockStateWithForcedUpdates(level, pos, rotatedState));
        register(DiodeBlock.class, (level, pos, originalState, rotatedState, rotatingBlockEntity) -> {
            rotatedState = Block.updateFromNeighbourShapes(rotatedState, level, pos);
            setBlockStateWithForcedUpdates(level, pos, rotatedState);
            rotatedState.handleNeighborChanged(level, pos, BwtBlocks.turntableBlock, pos.below(), true);
        });
        register(DoorBlock.class, (level, pos, originalState, rotatedState, rotatingBlockEntity) -> {
            setBlockStateWithForcedUpdates(level, pos, rotatedState);
            rotatedState.handleNeighborChanged(level, pos, BwtBlocks.turntableBlock, pos.below(), true);
        });
    }

    static void defaultRotationProcessor(Level level, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity) {
        rotatedState = Block.updateFromNeighbourShapes(rotatedState, level, pos);
        if (rotatedState.is(BlockTags.AIR)) {
            Block.dropResources(originalState, level, pos, rotatingBlockEntity, null, ItemStack.EMPTY);
            return;
        }
        setBlockStateWithForcedUpdates(level, pos, rotatedState);
        rotatedState.getBlock().setPlacedBy(level, pos, rotatedState, null, rotatedState.getBlock().getCloneItemStack(level, pos, rotatedState));
        if (rotatingBlockEntity != null) {
            level.setBlockEntity(rotatingBlockEntity);
        }
        rotatedState.handleNeighborChanged(level, pos, BwtBlocks.turntableBlock, pos.below(), true);
    }

    static void setBlockStateWithForcedUpdates(Level level, BlockPos pos, BlockState state) {
        setBlockStateWithForcedUpdates(level, pos, state, Block.UPDATE_ALL);
    }

    static void setBlockStateWithForcedUpdates(Level level, BlockPos pos, BlockState state, int flags) {
        setBlockStateWithForcedUpdates(level, pos, state, flags, 512);
    }

    static void setBlockStateWithForcedUpdates(Level level, BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        boolean stateWasChanged = level.setBlockAndUpdate(pos, state);
        if (stateWasChanged) {
            return;
        }
        forceUpdates(level, pos, state, flags, maxUpdateDepth);
    }

    static void forceUpdates(Level level, BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        Block block = state.getBlock();

        if ((flags & Block.UPDATE_NEIGHBORS) != 0) {
            level.blockUpdated(pos, block);
            if (!level.isClientSide && state.hasAnalogOutputSignal()) {
                level.updateNeighbourForOutputSignal(pos, block);
            }
        }

        if ((flags & Block.UPDATE_KNOWN_SHAPE) == 0 && maxUpdateDepth > 0) {
            int i = flags & ~(Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_NEIGHBORS);
            state.updateIndirectNeighbourShapes(level, pos, i, maxUpdateDepth - 1);
            state.updateNeighbourShapes(level, pos, i, maxUpdateDepth - 1);
            state.updateIndirectNeighbourShapes(level, pos, i, maxUpdateDepth - 1);
        }
    }
}

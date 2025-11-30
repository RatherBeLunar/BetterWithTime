package com.bwt.blocks.turntable;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public interface RotationProcessHelper {
    interface RotationProcessor {
        RotationProcessor DEFAULT = RotationProcessHelper::defaultRotationProcessor;

        void accept(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity);
    }

    HashMap<Class<? extends Block>, RotationProcessor> processors = new HashMap<>();

    static void register(Class<? extends Block> blockClass, RotationProcessor statePostProcessor) {
        processors.put(blockClass, statePostProcessor);
    }

    static void processRotation(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity) {
        Block block = rotatedState.getBlock();
        processors.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(RotationProcessor.DEFAULT)
                .accept(world, pos, originalState, rotatedState, rotatingBlockEntity);
    }

    static void registerDefaults() {
        register(RailBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity) -> setBlockStateWithForcedUpdates(world, pos, rotatedState));
        register(AbstractRedstoneGateBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity) -> {
            rotatedState = Block.postProcessState(rotatedState, world, pos);
            setBlockStateWithForcedUpdates(world, pos, rotatedState);
            rotatedState.neighborUpdate(world, pos, BwtBlocks.turntableBlock, pos.down(), true);
        });
        register(DoorBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity) -> {
            setBlockStateWithForcedUpdates(world, pos, rotatedState);
            rotatedState.neighborUpdate(world, pos, BwtBlocks.turntableBlock, pos.down(), true);
        });
    }

    static void defaultRotationProcessor(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity) {
        rotatedState = Block.postProcessState(rotatedState, world, pos);
        if (rotatedState.isIn(BlockTags.AIR)) {
            Block.dropStacks(originalState, world, pos, rotatingBlockEntity, null, ItemStack.EMPTY);
            return;
        }
        setBlockStateWithForcedUpdates(world, pos, rotatedState);
        rotatedState.getBlock().onPlaced(world, pos, rotatedState, null, rotatedState.getBlock().getPickStack(world, pos, rotatedState));
        if (rotatingBlockEntity != null) {
            world.addBlockEntity(rotatingBlockEntity);
        }
        rotatedState.neighborUpdate(world, pos, BwtBlocks.turntableBlock, pos.down(), true);
    }

    static void setBlockStateWithForcedUpdates(World world, BlockPos pos, BlockState state) {
        setBlockStateWithForcedUpdates(world, pos, state, Block.NOTIFY_ALL);
    }

    static void setBlockStateWithForcedUpdates(World world, BlockPos pos, BlockState state, int flags) {
        setBlockStateWithForcedUpdates(world, pos, state, flags, 512);
    }

    static void setBlockStateWithForcedUpdates(World world, BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        boolean stateWasChanged = world.setBlockState(pos, state);
        if (stateWasChanged) {
            return;
        }
        forceUpdates(world, pos, state, flags, maxUpdateDepth);
    }

    static void forceUpdates(World world, BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        Block block = state.getBlock();

        if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
            world.updateNeighbors(pos, block);
            if (!world.isClient && state.hasComparatorOutput()) {
                world.updateComparators(pos, block);
            }
        }

        if ((flags & Block.FORCE_STATE) == 0 && maxUpdateDepth > 0) {
            int i = flags & ~(Block.SKIP_DROPS | Block.NOTIFY_NEIGHBORS);
            state.prepare(world, pos, i, maxUpdateDepth - 1);
            state.updateNeighbors(world, pos, i, maxUpdateDepth - 1);
            state.prepare(world, pos, i, maxUpdateDepth - 1);
        }
    }
}

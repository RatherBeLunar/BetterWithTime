package com.bwt.blocks.turntable;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
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
        register(RailBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity) -> world.setBlockState(pos, rotatedState));
        register(AbstractRedstoneGateBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity) -> {
            rotatedState = Block.postProcessState(rotatedState, world, pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            world.setBlockState(pos, rotatedState);
            rotatedState.getBlock().neighborUpdate(rotatedState, world, pos, BwtBlocks.turntableBlock, pos.down(), true);
        });
        register(DoorBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity) -> {
            world.setBlockState(pos, rotatedState);
            rotatedState.getBlock().neighborUpdate(rotatedState, world, pos, BwtBlocks.turntableBlock, pos.down(), true);
        });
    }

    static void defaultRotationProcessor(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity) {
        rotatedState = Block.postProcessState(rotatedState, world, pos);
        if (rotatedState.isAir()) {
            Block.dropStacks(originalState, world, pos, rotatingBlockEntity, null, ItemStack.EMPTY);
            return;
        }
        world.setBlockState(pos, rotatedState, Block.NOTIFY_ALL);
        rotatedState.getBlock().onPlaced(world, pos, rotatedState, null, rotatedState.getBlock().getPickStack(world, pos, rotatedState));
        if (rotatingBlockEntity != null) {
            world.addBlockEntity(rotatingBlockEntity);
        }
        rotatedState.getBlock().neighborUpdate(rotatedState, world, pos, BwtBlocks.turntableBlock, pos.down(), true);
    }
}

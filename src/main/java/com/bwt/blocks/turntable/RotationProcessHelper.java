package com.bwt.blocks.turntable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public interface RotationProcessHelper {
    interface RotationProcessor {
        RotationProcessor DEFAULT = RotationProcessHelper::defaultRotationProcessor;

        void accept(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity, BlockState turntableState);
    }

    HashMap<Class<? extends Block>, RotationProcessor> processors = new HashMap<>();

    static void register(Class<? extends Block> blockClass, RotationProcessor statePostProcessor) {
        processors.put(blockClass, statePostProcessor);
    }

    static void processRotation(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity, BlockState turntableState) {
        Block block = rotatedState.getBlock();
        processors.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(block))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(RotationProcessor.DEFAULT)
                .accept(world, pos, originalState, rotatedState, rotatingBlockEntity, turntableState);
    }

    static void registerDefaults() {
        register(RailBlock.class, (world, pos, originalState, rotatedState, rotatingBlockEntity, turntableState) -> world.setBlockState(pos, rotatedState));
    }

    static void defaultRotationProcessor(World world, BlockPos pos, BlockState originalState, BlockState rotatedState, BlockEntity rotatingBlockEntity, BlockState turntableState) {
        rotatedState = Block.postProcessState(rotatedState, world, pos);
        if (rotatedState.isAir()) {
            Block.dropStacks(originalState, world, pos, rotatingBlockEntity, null, ItemStack.EMPTY);
        }
        ItemStack placementStack = originalState.getBlock().getPickStack(world, pos, originalState);
        if (rotatingBlockEntity != null) {
            NbtCompound nbtCompound = rotatingBlockEntity.createNbt();
            BlockItem.setBlockEntityNbt(placementStack, rotatingBlockEntity.getType(), nbtCompound);
            world.removeBlockEntity(pos);
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        world.setBlockState(pos, rotatedState, Block.NOTIFY_LISTENERS);
        if (rotatingBlockEntity != null) {
            world.addBlockEntity(rotatingBlockEntity);
        }
        rotatedState.getBlock().neighborUpdate(rotatedState, world, pos, turntableState.getBlock(), pos.down(), true);
    }
}

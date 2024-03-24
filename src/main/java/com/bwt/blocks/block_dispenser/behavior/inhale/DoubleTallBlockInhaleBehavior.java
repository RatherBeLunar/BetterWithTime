package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Comparator;

public class DoubleTallBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        ServerWorld world = blockPointer.world();
        BlockPos pos = blockPointer.pos();
        BlockState state = blockPointer.state();
        BlockPos firstHalfPos = blockPointer.pos().offset(state.get(BlockDispenserBlock.FACING));
        BlockState firstHalfState = world.getBlockState(firstHalfPos);
        DispenserBlockEntity dispenserBlockEntity = blockPointer.blockEntity();

        if (!(firstHalfState.contains(Properties.DOUBLE_BLOCK_HALF))) {
            return ItemStack.EMPTY;
        }
        ArrayList<ItemStack> drops = new ArrayList<>(2);
        drops.add(BlockInhaleBehavior.DEFAULT.getInhaledItems(blockPointer));
        Direction otherHalfDirection = firstHalfState.get(Properties.DOUBLE_BLOCK_HALF).getOppositeDirection();
        BlockState otherHalfState = world.getBlockState(firstHalfPos.offset(otherHalfDirection));
        if (otherHalfState.isOf(firstHalfState.getBlock())) {
            drops.add(BlockInhaleBehavior.DEFAULT.getInhaledItems(new BlockPointer(world, pos.offset(otherHalfDirection), state, dispenserBlockEntity)));
        }
        return drops.stream().filter(drop -> !drop.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public void inhale(BlockPointer blockPointer) {
        ServerWorld world = blockPointer.world();
        BlockPos firstHalfPos = blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING));
        BlockState firstHalfState = world.getBlockState(firstHalfPos);
        Direction otherHalfDirection = firstHalfState.get(Properties.DOUBLE_BLOCK_HALF).getOppositeDirection();
        BlockPos otherHalfPos = firstHalfPos.offset(otherHalfDirection);
        BlockState otherHalfState = world.getBlockState(otherHalfPos);

        ArrayList<BlockPos> halfPositions = new ArrayList<>();
        halfPositions.add(firstHalfPos);
        if (otherHalfState.isOf(firstHalfState.getBlock())) {
            halfPositions.add(otherHalfPos);
        }
        halfPositions.stream().sorted(Comparator.comparingInt(Vec3i::getY)).forEach(pos -> breakBlockNoItems(world, pos));
    }
}

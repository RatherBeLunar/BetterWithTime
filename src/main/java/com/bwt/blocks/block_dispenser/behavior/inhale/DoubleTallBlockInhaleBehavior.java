package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DoubleTallBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockSource blockPointer) {
        ServerLevel level = blockPointer.level();
        BlockPos pos = blockPointer.pos();
        BlockState state = blockPointer.state();
        BlockPos firstHalfPos = blockPointer.pos().relative(state.getValue(BlockDispenserBlock.FACING));
        BlockState firstHalfState = level.getBlockState(firstHalfPos);
        DispenserBlockEntity dispenserBlockEntity = blockPointer.blockEntity();

        if (!(firstHalfState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF))) {
            return ItemStack.EMPTY;
        }
        ArrayList<ItemStack> drops = new ArrayList<>(2);
        drops.add(BlockInhaleBehavior.DEFAULT.getInhaledItems(blockPointer));
        Direction otherHalfDirection = firstHalfState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).getDirectionToOther();
        BlockState otherHalfState = level.getBlockState(firstHalfPos.relative(otherHalfDirection));
        if (otherHalfState.is(firstHalfState.getBlock())) {
            drops.add(BlockInhaleBehavior.DEFAULT.getInhaledItems(new BlockSource(level, pos.relative(otherHalfDirection), state, dispenserBlockEntity)));
        }
        return drops.stream().filter(drop -> !drop.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public void inhale(BlockSource blockPointer) {
        ServerLevel level = blockPointer.level();
        BlockPos firstHalfPos = blockPointer.pos().relative(blockPointer.state().getValue(BlockDispenserBlock.FACING));
        BlockState firstHalfState = level.getBlockState(firstHalfPos);
        Direction otherHalfDirection = firstHalfState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF).getDirectionToOther();
        BlockPos otherHalfPos = firstHalfPos.relative(otherHalfDirection);
        BlockState otherHalfState = level.getBlockState(otherHalfPos);

        ArrayList<BlockPos> halfPositions = new ArrayList<>();
        halfPositions.add(firstHalfPos);
        if (otherHalfState.is(firstHalfState.getBlock())) {
            halfPositions.add(otherHalfPos);
        }
        halfPositions.stream().sorted(Comparator.comparingInt(Vec3i::getY)).forEach(pos -> breakBlockNoItems(level, pos));
    }
}

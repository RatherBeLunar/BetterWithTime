package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class BedBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockPointer blockPointer) {
        ServerWorld world = blockPointer.world();
        BlockPos pos = blockPointer.pos();
        BlockState state = blockPointer.state();
        BlockPos firstHalfPos = blockPointer.pos().offset(state.get(BlockDispenserBlock.FACING));
        BlockState firstHalfState = world.getBlockState(firstHalfPos);
        DispenserBlockEntity dispenserBlockEntity = blockPointer.blockEntity();

        if (!(firstHalfState.getBlock() instanceof BedBlock)) {
            return ItemStack.EMPTY;
        }
        BedPart bedPart = firstHalfState.get(BedBlock.PART);
        Direction bedFacing = firstHalfState.get(BedBlock.FACING);

        ArrayList<ItemStack> drops = new ArrayList<>(2);
        drops.add(BlockInhaleBehavior.DEFAULT.getInhaledItems(blockPointer));
        Direction otherHalfDirection = bedPart == BedPart.FOOT ? bedFacing : bedFacing.getOpposite();
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

        if (!(firstHalfState.getBlock() instanceof BedBlock)) {
            return;
        }
        BedPart bedPart = firstHalfState.get(BedBlock.PART);
        Direction bedFacing = firstHalfState.get(BedBlock.FACING);

        Direction otherHalfDirection = bedPart == BedPart.FOOT ? bedFacing : bedFacing.getOpposite();
        BlockPos otherHalfPos = firstHalfPos.offset(otherHalfDirection);
        BlockState otherHalfState = world.getBlockState(otherHalfPos);
        if (!otherHalfState.isOf(firstHalfState.getBlock())) {
            breakBlockNoItems(world, firstHalfPos);
        }

        if (bedPart.equals(BedPart.HEAD)) {
            breakBlockNoItems(world, firstHalfPos);
            breakBlockNoItems(world, otherHalfPos);
        }
        else {
            breakBlockNoItems(world, otherHalfPos);
            breakBlockNoItems(world, firstHalfPos);
        }
    }
}

package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedBlockInhaleBehavior implements BlockInhaleBehavior {
    @Override
    public ItemStack getInhaledItems(BlockSource blockPointer) {
        ServerLevel level = blockPointer.level();
        BlockPos pos = blockPointer.pos();
        BlockState state = blockPointer.state();
        BlockPos firstHalfPos = blockPointer.pos().relative(state.getValue(BlockDispenserBlock.FACING));
        BlockState firstHalfState = level.getBlockState(firstHalfPos);
        DispenserBlockEntity dispenserBlockEntity = blockPointer.blockEntity();

        if (!(firstHalfState.getBlock() instanceof BedBlock)) {
            return ItemStack.EMPTY;
        }
        BedPart bedPart = firstHalfState.getValue(BedBlock.PART);
        Direction bedFacing = firstHalfState.getValue(BedBlock.FACING);

        ArrayList<ItemStack> drops = new ArrayList<>(2);
        drops.add(BlockInhaleBehavior.DEFAULT.getInhaledItems(blockPointer));
        Direction otherHalfDirection = bedPart == BedPart.FOOT ? bedFacing : bedFacing.getOpposite();
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

        if (!(firstHalfState.getBlock() instanceof BedBlock)) {
            return;
        }
        BedPart bedPart = firstHalfState.getValue(BedBlock.PART);
        Direction bedFacing = firstHalfState.getValue(BedBlock.FACING);

        Direction otherHalfDirection = bedPart == BedPart.FOOT ? bedFacing : bedFacing.getOpposite();
        BlockPos otherHalfPos = firstHalfPos.relative(otherHalfDirection);
        BlockState otherHalfState = level.getBlockState(otherHalfPos);
        if (!otherHalfState.is(firstHalfState.getBlock())) {
            breakBlockNoItems(level, firstHalfPos);
        }

        if (bedPart.equals(BedPart.HEAD)) {
            breakBlockNoItems(level, firstHalfPos);
            breakBlockNoItems(level, otherHalfPos);
        }
        else {
            breakBlockNoItems(level, otherHalfPos);
            breakBlockNoItems(level, firstHalfPos);
        }
    }
}

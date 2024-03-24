package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

public interface BlockInhaleBehavior {
    BlockInhaleBehavior NOOP = new BlockInhaleBehavior() {
        @Override
        public ItemStack getInhaledItems(BlockPointer blockPointer) {
            return ItemStack.EMPTY;
        }
        @Override
        public void inhale(BlockPointer blockPointer) {}
    };

    BlockInhaleBehavior DEFAULT = new DefaultBlockInhaleBehavior();

    ItemStack getInhaledItems(BlockPointer blockPointer);

    void inhale(BlockPointer blockPointer);

    default void breakBlockNoItems(ServerWorld world, BlockPos pos) {
        world.breakBlock(pos, false);
    }

    static void registerBehaviors() {
        BlockDispenserBlock.registerBlockInhaleBehavior(CropBlock.class, new CropInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(NetherPortalBlock.class, new VoidInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(DoorBlock.class, new DoubleTallBlockInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(TallPlantBlock.class, new DoubleTallBlockInhaleBehavior());
    }
}

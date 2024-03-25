package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

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

    default void breakBlockNoItems(ServerWorld world, BlockState state, BlockPos pos) {
        if (state.isAir()) {
            return;
        }
        world.removeBlock(pos, false);
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(null, state));
        BlockSoundGroup soundGroup = state.getSoundGroup();
        world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0f) / 2.0f, soundGroup.getPitch() * 0.8f);
    }

    default void breakBlockNoItems(ServerWorld world, BlockPos pos) {
        breakBlockNoItems(world, world.getBlockState(pos), pos);
    }

    default void breakBlockNoItems(BlockPointer blockPointer) {
        BlockPos facingPos = blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING));
        BlockState facingState = blockPointer.world().getBlockState(facingPos);
        breakBlockNoItems(blockPointer.world(), facingState, facingPos);
    }

    static void registerBehaviors() {
        BlockDispenserBlock.registerBlockInhaleBehavior(CropBlock.class, new CropInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(AmethystClusterBlock.class, new AmethystInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(NetherPortalBlock.class, new VoidInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(DoorBlock.class, new DoubleTallBlockInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(TallPlantBlock.class, new DoubleTallBlockInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(BlockDispenserBlock.class, new BlockInhaleBehavior() {
            @Override
            public ItemStack getInhaledItems(BlockPointer blockPointer) {
                return BwtBlocks.blockDispenserBlock.asItem().getDefaultStack();
            }

            @Override
            public void inhale(BlockPointer blockPointer) {
                BlockPos blockDispenserPos = blockPointer.pos().offset(blockPointer.state().get(BlockDispenserBlock.FACING));
                BlockEntity blockEntity = blockPointer.world().getBlockEntity(blockDispenserPos);
                if (blockEntity instanceof BlockDispenserBlockEntity blockDispenserBlockEntity) {
                    blockDispenserBlockEntity.clear();
                }
                breakBlockNoItems(blockPointer.world(), blockDispenserPos);
            }
        });
    }
}

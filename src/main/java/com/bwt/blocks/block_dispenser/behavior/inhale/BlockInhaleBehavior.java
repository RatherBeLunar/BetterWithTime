package com.bwt.blocks.block_dispenser.behavior.inhale;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.block_dispenser.BlockDispenserBlock;
import com.bwt.blocks.block_dispenser.BlockDispenserBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface BlockInhaleBehavior {
    BlockInhaleBehavior NOOP = new BlockInhaleBehavior() {
        @Override
        public ItemStack getInhaledItems(BlockSource blockPointer) {
            return ItemStack.EMPTY;
        }
        @Override
        public void inhale(BlockSource blockPointer) {}
    };
    BlockInhaleBehavior VOID = new VoidInhaleBehavior();

    BlockInhaleBehavior DEFAULT = new DefaultBlockInhaleBehavior();

    ItemStack getInhaledItems(BlockSource blockPointer);

    void inhale(BlockSource blockPointer);

    default void breakBlockNoItems(ServerLevel level, BlockState state, BlockPos pos) {
        if (state.is(BlockTags.AIR)) {
            return;
        }
        level.removeBlock(pos, false);
        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(null, state));
        SoundType soundGroup = state.getSoundType();
        level.playSound(null, pos, soundGroup.getBreakSound(), SoundSource.BLOCKS, (soundGroup.getVolume() + 1.0f) / 2.0f, soundGroup.getPitch() * 0.8f);
    }

    default void breakBlockNoItems(ServerLevel level, BlockPos pos) {
        breakBlockNoItems(level, level.getBlockState(pos), pos);
    }

    default void breakBlockNoItems(BlockSource blockPointer) {
        BlockPos facingPos = blockPointer.pos().relative(blockPointer.state().getValue(BlockDispenserBlock.FACING));
        BlockState facingState = blockPointer.level().getBlockState(facingPos);
        breakBlockNoItems(blockPointer.level(), facingState, facingPos);
    }

    static void registerBehaviors() {
        BlockDispenserBlock.registerBlockInhaleBehavior(CropBlock.class, new CropInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(CocoaBlock.class, new CocoaBeanInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(AmethystClusterBlock.class, new AmethystInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(DoorBlock.class, new DoubleTallBlockInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(DoublePlantBlock.class, new DoubleTallBlockInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(BedBlock.class, new BedBlockInhaleBehavior());
        BlockDispenserBlock.registerBlockInhaleBehavior(BlockDispenserBlock.class, new BlockInhaleBehavior() {
            @Override
            public ItemStack getInhaledItems(BlockSource blockPointer) {
                return BwtBlocks.blockDispenserBlock.asItem().getDefaultInstance();
            }

            @Override
            public void inhale(BlockSource blockPointer) {
                BlockPos blockDispenserPos = blockPointer.pos().relative(blockPointer.state().getValue(BlockDispenserBlock.FACING));
                BlockEntity blockEntity = blockPointer.level().getBlockEntity(blockDispenserPos);
                if (blockEntity instanceof BlockDispenserBlockEntity blockDispenserBlockEntity) {
                    blockDispenserBlockEntity.clearContent();
                }
                breakBlockNoItems(blockPointer.level(), blockDispenserPos);
            }
        });
    }
}

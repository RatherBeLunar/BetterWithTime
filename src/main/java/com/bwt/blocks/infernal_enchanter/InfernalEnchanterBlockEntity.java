package com.bwt.blocks.infernal_enchanter;

import com.bwt.block_entities.BwtBlockEntities;
import com.bwt.blocks.BwtBlocks;
import com.google.common.collect.Queues;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class InfernalEnchanterBlockEntity extends BlockEntity {

    public InfernalEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.infernalEnchanterBlockEntity, pos, state);
    }


    public static final List<BlockPos> CANDLE_OFFSETS = BlockPos.stream(-8, -1, -8, 8, 1, 8).map(BlockPos::toImmutable).toList();

    private final Queue<AnimationFrame> ANIMATION_QUEUE = Queues.newConcurrentLinkedQueue();

    public void onFirstEnter(World world, BlockPos blockPos, BlockState blockState) {

        var rings = getRings(blockPos, true);

        ANIMATION_QUEUE.clear();
        for (var ring : rings) {
            var frame = new EntranceAnimationFrame(ring.getValue());
            ANIMATION_QUEUE.add(frame);
            ANIMATION_QUEUE.add(w -> {});
            ANIMATION_QUEUE.add(w -> {});
        }

    }

    private static @NotNull List<Map.Entry<Integer, List<BlockPos>>> getRings(BlockPos blockPos, boolean reverse) {
        var comparator = Comparator.<Map.Entry<Integer, List<BlockPos>>>comparingInt(Map.Entry::getKey);
        if(reverse) {
            comparator = comparator.reversed();
        }
        var rings = CANDLE_OFFSETS.stream().map(blockPos::add).collect(Collectors.groupingBy(detectPos -> {
            int distX = Math.abs(detectPos.getX() - blockPos.getX());
            int distZ = Math.abs(detectPos.getZ() - blockPos.getZ());
            return Math.max(distX, distZ);
        })).entrySet().stream().sorted(comparator).toList();
        return rings;
    }

    public void onLeave(World world, BlockPos blockPos, BlockState blockState) {
        var rings = getRings(blockPos, false);

        ANIMATION_QUEUE.clear();
        for (var ring : rings) {
            var frame = new LeavingAnimationFrame(ring.getValue());
            ANIMATION_QUEUE.add(frame);
        }
    }

    protected int playersInRange = 0, previousPlayersInRange = 0;

    public void findPlayersNearby() {
        if (this.world == null) {
            return;
        }
        var box = Box.of(getPos().toCenterPos(), 17, 17, 17);

        this.previousPlayersInRange = this.playersInRange;
        this.playersInRange = world.getEntitiesByClass(PlayerEntity.class, box, p -> true).size();
    }

    public boolean isFirstEnter() {
        if (this.world == null) {
            return false;
        }
        BlockState state = this.world.getBlockState(this.getPos());
        if (state.get(InfernalEnchanterBlock.LIT)) {
            return false;
        }
        return this.playersInRange > 0 && this.previousPlayersInRange == 0;
    }

    public boolean isLeaving() {
        if (this.world == null) {
            return false;
        }
        BlockState state = this.world.getBlockState(this.getPos());
        var lit = state.get(InfernalEnchanterBlock.LIT);
        return lit && this.playersInRange == 0;
    }

    public void tick(World world, BlockPos blockPos, BlockState blockState) {
        if(world.getTime() % 5 != 0) {
            return;
        }
        if(!this.ANIMATION_QUEUE.isEmpty()) {
            AnimationFrame frame = this.ANIMATION_QUEUE.poll();
            if(frame != null) {
                frame.animate(world);
            }
            return;
        }

        this.findPlayersNearby();
        if (this.isFirstEnter()) {
            world.setBlockState(getPos(), blockState.with(InfernalEnchanterBlock.LIT, true));
            onFirstEnter(world, blockPos, blockState);
        } else if (isLeaving()) {
            onLeave(world, blockPos, blockState);
        }

    }

    public static <E extends InfernalEnchanterBlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, E e) {
        e.tick(world, blockPos, blockState);
    }

    @FunctionalInterface
    interface AnimationFrame {
        void animate(World world);
    }

    public record EntranceAnimationFrame(List<BlockPos> blockPosIterator) implements AnimationFrame {
        @Override
        public void animate(World world) {
            blockPosIterator.forEach(detectPos -> {
                BlockState detectState = world.getBlockState(detectPos);
                if(detectState.getBlock() == BwtBlocks.infernalEnchanterBlock) {
                    world.playSound(null, detectPos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.setBlockState(detectPos, detectState.with(InfernalEnchanterBlock.LIT, true), 11);
                    world.markDirty(detectPos);
                }
                if (detectState.isIn(BlockTags.CANDLES) && detectState.contains(CandleBlock.LIT)) {
                    world.playSound(null, detectPos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.setBlockState(detectPos, detectState.with(CandleBlock.LIT, true), 11);
                    world.markDirty(detectPos);

                }
            });
        }
    }

    public record LeavingAnimationFrame(List<BlockPos> blockPosIterator) implements AnimationFrame {
        @Override
        public void animate(World world) {
            blockPosIterator.forEach(detectPos -> {
                BlockState detectState = world.getBlockState(detectPos);
                if(detectState.getBlock() == BwtBlocks.infernalEnchanterBlock) {
                    world.playSound(null, detectPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                    world.setBlockState(detectPos, detectState.with(InfernalEnchanterBlock.LIT, false), 11);
                    world.markDirty(detectPos);
                }

                if (detectState.isIn(BlockTags.CANDLES) && detectState.contains(CandleBlock.LIT)) {
                    world.setBlockState(detectPos, detectState.with(CandleBlock.LIT, false), 11);
                    world.markDirty(detectPos);
                }
            });
        }
    }
}

package com.bwt.blocks.infernal_enchanter;

import com.google.common.collect.Queues;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class InfernalEnchanterAnimationsController {


    public InfernalEnchanterAnimationsController() {
    }

    static @NotNull List<Map.Entry<Integer, List<BlockPos>>> getRings(BlockPos blockPos, boolean reverse) {
        var comparator = Comparator.<Map.Entry<Integer, List<BlockPos>>>comparingInt(Map.Entry::getKey);
        if (reverse) {
            comparator = comparator.reversed();
        }
        var rings = InfernalEnchanterBlockEntity.EXTERNAL_CANDLE_OFFSETS.stream().map(blockPos::add).collect(Collectors.groupingBy(detectPos -> {
            int distX = Math.abs(detectPos.getX() - blockPos.getX());
            int distZ = Math.abs(detectPos.getZ() - blockPos.getZ());
            return Math.max(distX, distZ);
        })).entrySet().stream().sorted(comparator).toList();
        return rings;
    }

    private final Queue<Animations.AnimationFrame> ANIMATION_QUEUE = Queues.newConcurrentLinkedQueue();

    protected int playersInRange = 0, previousPlayersInRange = 0;


    public void tick(@NotNull World world, BlockPos blockPos, BlockState blockState) {
        if (world.getTime() % 5 != 0) {
            return;
        }
        if (!this.ANIMATION_QUEUE.isEmpty()) {
            Animations.AnimationFrame frame = this.ANIMATION_QUEUE.poll();
            if (frame != null) {
                frame.animate(world, blockPos);
            }
            return;
        }

        this.findPlayersNearby(world, blockPos);
        if (this.isFirstEnter(world, blockPos)) {
            onFirstEnter(world, blockPos, blockState);
        } else if (isLeaving(world, blockPos)) {
            onLeave(world, blockPos, blockState);
        }

    }


    public void onFirstEnter(World world, BlockPos blockPos, BlockState blockState) {

        var rings = InfernalEnchanterAnimationsController.getRings(blockPos, true);
        ANIMATION_QUEUE.clear();
        InfernalEnchanterBlock.spawnCandleParticles(blockState, world, blockPos, world.random);
        for (var ring : rings) {
            var frame = new Animations.EntranceAnimationFrame(ring.getValue());
            ANIMATION_QUEUE.add(frame);
            ANIMATION_QUEUE.add(Animations.PAUSE_FRAME);
            ANIMATION_QUEUE.add(Animations.PAUSE_FRAME);
        }

    }

    public void onLeave(World world, BlockPos blockPos, BlockState blockState) {
        var rings = InfernalEnchanterAnimationsController.getRings(blockPos, false);

        ANIMATION_QUEUE.clear();
        for (var ring : rings) {
            var frame = new Animations.LeavingAnimationFrame(ring.getValue());
            ANIMATION_QUEUE.add(frame);
        }
    }


    public void findPlayersNearby(World world, BlockPos blockPos) {
        if (world == null) {
            return;
        }
        var box = Box.of(blockPos.toCenterPos(), 17, 17, 17);

        this.previousPlayersInRange = this.playersInRange;
        this.playersInRange = world.getEntitiesByClass(PlayerEntity.class, box, p -> true).size();
    }

    public boolean isFirstEnter(World world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        if (state.get(InfernalEnchanterBlock.LIT)) {
            return false;
        }
        return this.playersInRange > 0 && this.previousPlayersInRange == 0;
    }

    public boolean isLeaving(World world, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        var lit = state.get(InfernalEnchanterBlock.LIT);
        return lit && this.playersInRange == 0;
    }
}

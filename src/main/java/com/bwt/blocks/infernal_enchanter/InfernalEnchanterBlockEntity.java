package com.bwt.blocks.infernal_enchanter;

import com.bwt.block_entities.BwtBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class InfernalEnchanterBlockEntity extends BlockEntity {
    public InfernalEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(BwtBlockEntities.infernalEnchanterBlockEntity, pos, state);
    }


    public static final List<BlockPos> CANDLE_OFFSETS = BlockPos.stream(-8, -1, -8, 8, 1, 8).map(BlockPos::toImmutable).toList();


    public static void onFirstEnter(World world, BlockPos blockPos, BlockState blockState) {
        var newState = blockState.with(InfernalEnchanterBlock.LIT, true);
        world.setBlockState(blockPos, newState);
        markDirty(world, blockPos, newState);

        CANDLE_OFFSETS.forEach(posOffset -> {
            BlockPos detectPos = blockPos.add(posOffset);

            BlockState detectState = world.getBlockState(detectPos);
            if(detectState.isIn(BlockTags.CANDLES) && detectState.contains(CandleBlock.LIT)) {
                world.setBlockState(detectPos, detectState.with(CandleBlock.LIT, true));
            }
        });

    }

    public static void onLeave(World world, BlockPos blockPos, BlockState blockState) {
        var newState = blockState.with(InfernalEnchanterBlock.LIT, false);
        world.setBlockState(blockPos, newState, 3);
        markDirty(world, blockPos, newState);
        CANDLE_OFFSETS.forEach(posOffset -> {
            BlockPos detectPos = blockPos.add(posOffset);

            BlockState detectState = world.getBlockState(detectPos);
            if(detectState.isIn(BlockTags.CANDLES) && detectState.contains(CandleBlock.LIT)) {
                world.setBlockState(detectPos, detectState.with(CandleBlock.LIT, false), 3);
                world.markDirty(detectPos);
            }
        });

//
    }

    protected int playersInRange = 0, previousPlayersInRange = 0;

    public void findPlayersNearby() {
        if(this.world == null) {
            return;
        }
        var box = Box.of(getPos().toCenterPos(), 10, 10, 10);

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

    public static <E extends InfernalEnchanterBlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, E e) {
        e.findPlayersNearby();

        if (e.isFirstEnter()) {
            world.setBlockState(blockPos, blockState.with(InfernalEnchanterBlock.LIT, true));
            onFirstEnter(world, blockPos, blockState);
        } else if (e.isLeaving()) {
            onLeave(world, blockPos, blockState);
        }

    }
}

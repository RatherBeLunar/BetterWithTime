package com.bwt.blocks.blood_wood;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class BloodWoodSaplingBlock extends SaplingBlock {

    public BloodWoodSaplingBlock(TreeGrower generator, Properties settings) {
        super(generator, settings);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (!(placer instanceof Player playerEntity) || !level.dimensionType().ultraWarm()) {
            return;
        }
        List<ZombifiedPiglin> list = level.getEntitiesOfClass(ZombifiedPiglin.class, playerEntity.getBoundingBox().inflate(16.0));
        list.forEach(piglin -> piglin.setTarget(playerEntity));
        PiglinAi.angerNearbyPiglins(playerEntity, false);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return super.isBonemealSuccess(level, random, pos, state) && level.dimensionType().ultraWarm();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.dimensionType().ultraWarm() && random.nextInt(7) == 0) {
            this.advanceTree(level, pos, state, random);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
        return floor.is(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), Block.UPDATE_INVISIBLE);
            return;
        }
        if (!this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random)) {
            return;
        }
        level.playSound(null, pos, BwtSoundEvents.BLOOD_WOOD_MOAN,
                SoundSource.BLOCKS, 0.5F, 2F);
    }
}

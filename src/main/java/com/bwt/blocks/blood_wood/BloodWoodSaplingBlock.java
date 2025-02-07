package com.bwt.blocks.blood_wood;

import com.bwt.sounds.BwtSoundEvents;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodWoodSaplingBlock extends SaplingBlock {

    public BloodWoodSaplingBlock(SaplingGenerator generator, Settings settings) {
        super(generator, settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!(placer instanceof PlayerEntity playerEntity) || !world.getDimension().ultrawarm()) {
            return;
        }
        List<ZombifiedPiglinEntity> list = world.getNonSpectatingEntities(ZombifiedPiglinEntity.class, playerEntity.getBoundingBox().expand(16.0));
        list.forEach(piglin -> piglin.setTarget(playerEntity));
        PiglinBrain.onGuardedBlockInteracted(playerEntity, false);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return super.canGrow(world, random, pos, state) && world.getDimension().ultrawarm();
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getDimension().ultrawarm() && random.nextInt(7) == 0) {
            this.generate(world, pos, state, random);
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON);
    }

    @Override
    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        if (state.get(STAGE) == 0) {
            world.setBlockState(pos, state.cycle(STAGE), Block.NO_REDRAW);
            return;
        }
        if (!this.generator.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random)) {
            return;
        }
        world.playSound(null, pos, BwtSoundEvents.BLOOD_WOOD_MOAN,
                SoundCategory.BLOCKS, 0.5F, 2F);
    }
}

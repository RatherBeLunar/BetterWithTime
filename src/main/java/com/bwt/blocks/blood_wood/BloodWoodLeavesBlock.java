package com.bwt.blocks.blood_wood;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.BloodWoodSaplingItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BloodWoodLeavesBlock extends LeavesBlock {
    public BloodWoodLeavesBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.decaying(state)) {
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        }
    }

    public static void dropResources(BlockState state, Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        getDrops(state, serverLevel, pos, null).forEach(stack -> popResource(level, pos, stack));
        state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
    }

    public static void popResource(Level level, BlockPos pos, ItemStack stack) {
        if (level.isClientSide || stack.isEmpty() || !level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            return;
        }
        double d = (double) EntityType.ITEM.getHeight() / 2.0;
        double e = (double)pos.getX() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
        double f = (double)pos.getY() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25) - d;
        double g = (double)pos.getZ() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
        ItemEntity itemEntity = stack.is(BwtBlocks.bloodWoodBlocks.saplingItem) ? new BloodWoodSaplingItemEntity(level, e, f, g, stack) : new ItemEntity(level, e, f, g, stack);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}

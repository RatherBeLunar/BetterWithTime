package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BloodWoodSaplingItemEntity extends ItemEntity {
    public BloodWoodSaplingItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
    }

    @Override
    public void resetFallDistance() {
        super.resetFallDistance();
        if (mainSupportingBlockPos.isEmpty()) {
            return;
        }
        BlockPos belowPos = mainSupportingBlockPos.get();
        if (level().getBlockState(belowPos.above()).is(BlockTags.AIR) && level().getBlockState(belowPos).is(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON)) {
            this.discard();
            level().setBlockAndUpdate(belowPos.above(), BwtBlocks.bloodWoodBlocks.saplingBlock.defaultBlockState());
        }
    }
}

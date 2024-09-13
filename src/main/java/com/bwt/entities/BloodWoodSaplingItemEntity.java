package com.bwt.entities;

import com.bwt.blocks.BwtBlocks;
import com.bwt.tags.BwtBlockTags;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BloodWoodSaplingItemEntity extends ItemEntity {
    public BloodWoodSaplingItemEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    @Override
    public void onLanding() {
        super.onLanding();
        if (supportingBlockPos.isEmpty()) {
            return;
        }
        BlockPos belowPos = supportingBlockPos.get();
        if (getWorld().getBlockState(belowPos.up()).isIn(BlockTags.AIR) && getWorld().getBlockState(belowPos).isIn(BwtBlockTags.BLOOD_WOOD_PLANTABLE_ON)) {
            this.discard();
            getWorld().setBlockState(belowPos.up(), BwtBlocks.bloodWoodBlocks.saplingBlock.getDefaultState());
        }
    }
}

package com.bwt.items;

import com.bwt.blocks.AxleBlock;
import com.bwt.blocks.AxlePowerSourceBlock;
import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.WindmillEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WindmillItem extends Item {

    public WindmillItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(BwtBlocks.axleBlock)) {
            context.getStack().decrement(1);
            world.setBlockState(blockPos, BwtBlocks.axlePowerSourceBlock.getDefaultState().with(AxlePowerSourceBlock.AXIS, blockState.get(AxleBlock.AXIS)));
            WindmillEntity.spawnAtPos(world, blockPos);
        }
        return ActionResult.CONSUME;
    }
}

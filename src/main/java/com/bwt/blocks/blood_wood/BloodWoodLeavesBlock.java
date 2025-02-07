package com.bwt.blocks.blood_wood;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.BloodWoodSaplingItemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class BloodWoodLeavesBlock extends LeavesBlock {
    public BloodWoodLeavesBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldDecay(state)) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    public static void dropStacks(BlockState state, World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        getDroppedStacks(state, serverWorld, pos, null).forEach(stack -> dropStack(world, pos, stack));
        state.onStacksDropped(serverWorld, pos, ItemStack.EMPTY, true);
    }

    public static void dropStack(World world, BlockPos pos, ItemStack stack) {
        if (world.isClient || stack.isEmpty() || !world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            return;
        }
        double d = (double) EntityType.ITEM.getHeight() / 2.0;
        double e = (double)pos.getX() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
        double f = (double)pos.getY() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25) - d;
        double g = (double)pos.getZ() + 0.5 + MathHelper.nextDouble(world.random, -0.25, 0.25);
        ItemEntity itemEntity = stack.isOf(BwtBlocks.bloodWoodBlocks.saplingItem) ? new BloodWoodSaplingItemEntity(world, e, f, g, stack) : new ItemEntity(world, e, f, g, stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
    }
}

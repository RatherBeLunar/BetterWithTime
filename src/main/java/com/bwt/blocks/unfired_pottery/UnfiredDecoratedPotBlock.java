package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class UnfiredDecoratedPotBlock extends UnfiredPotteryBlock {
    public UnfiredDecoratedPotBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Blocks.DECORATED_POT.getDefaultState().getOutlineShape(world, pos, context);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isIn(ItemTags.DECORATED_POT_SHERDS)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        BlockState replacementState = BwtBlocks.unfiredDecoratedPotBlockWithSherds.getDefaultState();
        world.setBlockState(pos, replacementState, Block.NOTIFY_LISTENERS, 0);
        return replacementState.onUseWithItem(stack, world, player, hand, hit);
    }
}

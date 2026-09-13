package com.bwt.blocks.unfired_pottery;

import com.bwt.blocks.BwtBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UnfiredDecoratedPotBlock extends UnfiredPotteryBlock {
    public UnfiredDecoratedPotBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Blocks.DECORATED_POT.defaultBlockState().getShape(level, pos, context);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(ItemTags.DECORATED_POT_SHERDS)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        BlockState replacementState = BwtBlocks.unfiredDecoratedPotBlockWithSherds.defaultBlockState();
        level.setBlock(pos, replacementState, Block.UPDATE_CLIENTS, 0);
        return replacementState.useItemOn(stack, level, player, hand, hit);
    }
}

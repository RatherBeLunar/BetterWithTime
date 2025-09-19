package com.bwt.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class NetherrackGrothedBlock extends Block {
    public NetherrackGrothedBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !neighborState.isOf(BwtBlocks.netherGroth)) {
            return Blocks.NETHERRACK.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getWorld().getBlockState(ctx.getBlockPos().up()).isOf(BwtBlocks.netherGroth)) {
            return this.getDefaultState();
        }
        return Blocks.NETHERRACK.getDefaultState();
    }
}

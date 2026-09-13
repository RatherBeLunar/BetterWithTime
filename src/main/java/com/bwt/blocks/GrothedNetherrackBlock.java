package com.bwt.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GrothedNetherrackBlock extends Block {
    public GrothedNetherrackBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !neighborState.is(BwtBlocks.netherGroth)) {
            return Blocks.NETHERRACK.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (ctx.getLevel().getBlockState(ctx.getClickedPos().above()).is(BwtBlocks.netherGroth)) {
            return this.defaultBlockState();
        }
        return Blocks.NETHERRACK.defaultBlockState();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return Blocks.NETHERRACK.getCloneItemStack(level, pos, state);
    }
}

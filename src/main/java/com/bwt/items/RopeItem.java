package com.bwt.items;

import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.BwtBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RopeItem extends BlockItem {
    public RopeItem(Item.Properties settings) {
        super(BwtBlocks.ropeBlock, settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if ( context.getItemInHand().getCount() == 0 ) {
            return InteractionResult.FAIL;
        }
        BlockPlaceContext placementContext = new BlockPlaceContext(context);
        Level level = placementContext.getLevel();
        // This is the original context's blockpos on purpose, since it gives us the hit result target, not air
        BlockPos.MutableBlockPos mutablePos = context.getClickedPos().mutable();
        BlockState state = level.getBlockState(mutablePos);
        Direction anchorFacing;
        // rope can only be attached to anchors or other ropes
        if (
            state.is(BwtBlocks.ropeBlock)
            || (
                state.is(BwtBlocks.anchorBlock)
                && !(anchorFacing = state.getValue(AnchorBlock.FACING)).equals(Direction.UP)
                && !placementContext.getClickedFace().equals(anchorFacing.getOpposite())
            )
        ) {
            do {
                mutablePos.move(Direction.DOWN);
                placementContext = BlockPlaceContext.at(placementContext, mutablePos, Direction.DOWN);
            } while (level.getBlockState(mutablePos).is(BwtBlocks.ropeBlock) && mutablePos.getY() > level.getMinBuildHeight());

            return this.place(placementContext);
        }
        return super.useOn(context);
    }
}

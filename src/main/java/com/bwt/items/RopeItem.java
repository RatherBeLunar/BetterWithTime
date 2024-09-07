package com.bwt.items;

import com.bwt.blocks.AnchorBlock;
import com.bwt.blocks.BwtBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RopeItem extends BlockItem {
    public RopeItem(Item.Settings settings) {
        super(BwtBlocks.ropeBlock, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if ( context.getStack().getCount() == 0 ) {
            return ActionResult.FAIL;
        }
        ItemPlacementContext placementContext = new ItemPlacementContext(context);
        World world = placementContext.getWorld();
        // This is the original context's blockpos on purpose, since it gives us the hit result target, not air
        BlockPos.Mutable mutablePos = context.getBlockPos().mutableCopy();
        BlockState state = world.getBlockState(mutablePos);
        Direction anchorFacing;
        // rope can only be attached to anchors or other ropes
        if (
            state.isOf(BwtBlocks.ropeBlock)
            || (
                state.isOf(BwtBlocks.anchorBlock)
                && !(anchorFacing = state.get(AnchorBlock.FACING)).equals(Direction.UP)
                && !placementContext.getSide().equals(anchorFacing.getOpposite())
            )
        ) {
            do {
                mutablePos.move(Direction.DOWN);
                placementContext = ItemPlacementContext.offset(placementContext, mutablePos, Direction.DOWN);
            } while (world.getBlockState(mutablePos).isOf(BwtBlocks.ropeBlock) && mutablePos.getY() > world.getBottomY());

            return this.place(placementContext);
        }
        return super.useOnBlock(context);
    }
}

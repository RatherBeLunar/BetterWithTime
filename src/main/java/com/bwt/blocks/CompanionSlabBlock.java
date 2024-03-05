package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.ItemPlacementContext;
import org.jetbrains.annotations.Nullable;

public class CompanionSlabBlock extends SlabBlock {
    public CompanionSlabBlock(Settings settings) {
        super(settings);
    }

    public static final MapCodec<SlabBlock> CODEC = SlabBlock.createCodec(CompanionSlabBlock::new);

    @Override
    public MapCodec<? extends SlabBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getWorld().getBlockState(ctx.getBlockPos()).isOf(this)) {
            return BwtBlocks.companionCubeBlock.getDefaultState();
        }
        return super.getPlacementState(ctx);
    }
}

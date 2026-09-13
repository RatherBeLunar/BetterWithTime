package com.bwt.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CompanionSlabBlock extends SlabBlock {
    public CompanionSlabBlock(Properties settings) {
        super(settings);
    }

    public static final MapCodec<SlabBlock> CODEC = SlabBlock.simpleCodec(CompanionSlabBlock::new);

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (ctx.getLevel().getBlockState(ctx.getClickedPos()).is(this)) {
            return BwtBlocks.companionCubeBlock.defaultBlockState();
        }
        return super.getStateForPlacement(ctx);
    }
}

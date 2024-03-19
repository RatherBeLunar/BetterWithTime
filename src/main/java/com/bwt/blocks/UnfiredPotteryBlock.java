package com.bwt.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public abstract class UnfiredPotteryBlock extends Block {
    public static final BooleanProperty COOKING = BooleanProperty.of("cooking");

    public UnfiredPotteryBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(COOKING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COOKING);
    }
}

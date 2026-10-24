package com.bwt.rendering.unfired_decorated_pot;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.PotDecorations;

import java.util.ArrayList;

public class UnfiredDecoratedPotRenderState extends BlockEntityRenderState {
    public Direction horizontalFacing;
    public PotDecorations potDecorations;
    public ArrayList<ItemStackRenderState> sherdRenderStates;
}

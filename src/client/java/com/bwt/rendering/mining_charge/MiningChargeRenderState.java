package com.bwt.rendering.mining_charge;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class MiningChargeRenderState extends EntityRenderState {
    public BlockState blockState;
    public float ticksBeforeExplosion;
}

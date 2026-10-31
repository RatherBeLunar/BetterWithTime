package com.bwt.rendering.moving_rope_platform;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class MovingRopeRenderState extends EntityRenderState {
    public BlockPos pulleyPos;
    public double boundingBoxMaxY;
    public BlockPos blockPosition;
    public HashMap<Vec3i, BlockState> blockMap;
    public HashMap<Vec3i, Integer> lightMap = new HashMap<>();
}

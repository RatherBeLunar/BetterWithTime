/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.bwt.blocks.mech_hopper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class MechHopperBlockEntityRenderer implements BlockEntityRenderer<MechHopperBlockEntity> {
    private final BlockRenderManager manager;

    public MechHopperBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    @Override
    public void render(MechHopperBlockEntity hopperBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int uv) {
        World world = hopperBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        BlockPos pos = hopperBlockEntity.getPos();
        BlockState state = world.getBlockState(pos);

        matrixStack.push();
        this.renderModel(pos, state, matrixStack, vertexConsumerProvider, world, false, uv);
        matrixStack.pop();
    }

    private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, boolean cull, int overlay) {
        RenderLayer renderLayer = RenderLayers.getBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.manager.getModelRenderer().render(world, this.manager.getModel(state), state, pos, matrices, vertexConsumer, cull, Random.create(), state.getRenderingSeed(pos), overlay);
    }

    @Override
    public int getRenderDistance() {
        return 68;
    }
}


/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.bwt;

import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MechHopperBlockEntityRenderer implements BlockEntityRenderer<MechHopperBlockEntity> {
    private final BlockRenderManager manager;
    private static final Identifier FILL_TEXTURE = new Identifier("bwt", "textures/block/hopper_fill.png");
    protected MechHopperFillModel model;


    @Nullable
    protected RenderLayer getRenderLayer(MechHopperBlockEntity entity) {
        return this.model.getLayer(FILL_TEXTURE);
    }

    public MechHopperBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
        this.model = new MechHopperFillModel(ctx.getLayerModelPart(BetterWithTimeClient.MECH_HOPPER_FILL_LAYER));
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
        if (hopperBlockEntity.slotsOccupied > 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(getRenderLayer(hopperBlockEntity));
            matrixStack.push();
            matrixStack.translate(0, (hopperBlockEntity.slotsOccupied * (14f - 7f) / (MechHopperBlockEntity.INVENTORY_SIZE - 1f) + 7f) / 16f, 0);
            this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.getUv(0.0f, false), 1, 1, 1, 1);
            matrixStack.pop();
        }
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


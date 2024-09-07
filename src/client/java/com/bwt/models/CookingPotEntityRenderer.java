/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
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
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class CookingPotEntityRenderer implements BlockEntityRenderer<AbstractCookingPotBlockEntity> {
    private final BlockRenderManager manager;
    private final Identifier fillTexture;
    protected MechHopperFillModel model;

    public CookingPotEntityRenderer(BlockEntityRendererFactory.Context ctx, Identifier fillTexture) {
        this.manager = ctx.getRenderManager();
        this.model = new MechHopperFillModel(ctx.getLayerModelPart(BetterWithTimeClient.MECH_HOPPER_FILL_LAYER));
        this.fillTexture = fillTexture;
    }

    @Override
    public void render(AbstractCookingPotBlockEntity cookingPotBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int uv) {
        World world = cookingPotBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        BlockPos pos = cookingPotBlockEntity.getPos();
        BlockState state = world.getBlockState(pos);

        // Render the block itself
        matrixStack.push();
        this.renderModel(pos, state, matrixStack, vertexConsumerProvider, world, uv);
        matrixStack.pop();
        // Render the fill texture
        if (cookingPotBlockEntity.slotsOccupied > 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(fillTexture));
            matrixStack.push();
            matrixStack.translate(0.5f, 0.5f, 0.5f);
            if (state.contains(AbstractCookingPotBlock.TIP_DIRECTION)) {
                matrixStack.multiply(state.get(AbstractCookingPotBlock.TIP_DIRECTION).getRotationQuaternion());
            }
            matrixStack.translate(-0.5f, -0.5f, -0.5f);
            matrixStack.scale(0.99f, 1, 0.99f);
            matrixStack.translate(0.01f, (cookingPotBlockEntity.slotsOccupied * (13f - 2f) / (cookingPotBlockEntity.inventory.size() - 1f) + 2f) / 16f, 0.01f);
            this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.getUv(0.0f, false), -1);
            matrixStack.pop();
        }
    }

    private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int overlay) {
        RenderLayer renderLayer = RenderLayers.getBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        matrices.push();
        this.manager.getModelRenderer().render(world, this.manager.getModel(state), state, pos, matrices, vertexConsumer, false, Random.create(), state.getRenderingSeed(pos), overlay);
        matrices.pop();
    }

    @Override
    public int getRenderDistance() {
        return 68;
    }
}


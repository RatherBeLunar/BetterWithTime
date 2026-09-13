/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Environment(value=EnvType.CLIENT)
public class CookingPotEntityRenderer implements BlockEntityRenderer<AbstractCookingPotBlockEntity> {
    private final BlockRenderDispatcher manager;
    private final ResourceLocation fillTexture;
    protected final MechHopperFillModel model;

    public CookingPotEntityRenderer(BlockEntityRendererProvider.Context ctx, ResourceLocation fillTexture) {
        this.manager = ctx.getBlockRenderDispatcher();
        this.model = new MechHopperFillModel(ctx.bakeLayer(BetterWithTimeClient.MECH_HOPPER_FILL_LAYER));
        this.fillTexture = fillTexture;
    }

    @Override
    public void render(AbstractCookingPotBlockEntity cookingPotBlockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int light, int uv) {
        Level level = cookingPotBlockEntity.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = cookingPotBlockEntity.getBlockPos();
        BlockState state = level.getBlockState(pos);

        // Render the block itself
        poseStack.pushPose();
        this.renderModel(pos, state, poseStack, vertexConsumerProvider, level, uv);
        poseStack.popPose();
        // Render the fill texture
        if (cookingPotBlockEntity.slotsOccupied > 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.renderType(fillTexture));
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);
            if (state.hasProperty(AbstractCookingPotBlock.TIP_DIRECTION)) {
                poseStack.mulPose(state.getValue(AbstractCookingPotBlock.TIP_DIRECTION).getRotation());
            }
            poseStack.translate(-0.5f, -0.5f, -0.5f);
            poseStack.scale(0.99f, 1, 0.99f);
            poseStack.translate(0.01f, (cookingPotBlockEntity.slotsOccupied * (13f - 2f) / (cookingPotBlockEntity.inventory.getContainerSize() - 1f) + 2f) / 16f, 0.01f);
            this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.pack(0.0f, false), -1);
            poseStack.popPose();
        }
    }

    private void renderModel(BlockPos pos, BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, Level level, int overlay) {
        RenderType renderLayer = ItemBlockRenderTypes.getChunkRenderType(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        matrices.pushPose();
        this.manager.getModelRenderer().tesselateBlock(level, this.manager.getBlockModel(state), state, pos, matrices, vertexConsumer, false, RandomSource.create(), state.getSeed(pos), overlay);
        matrices.popPose();
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}


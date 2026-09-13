package com.bwt.models;

import com.bwt.entities.HorizontalMechPowerSourceEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public abstract class HorizontalMechPowerSourceEntityRenderer<T extends HorizontalMechPowerSourceEntity> extends EntityRenderer<T> {
    protected HorizontalMechPowerSourceEntityModel<T> model;

    protected HorizontalMechPowerSourceEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nullable
    protected RenderType getRenderLayer(T entity) {
        ResourceLocation identifier = this.getTextureLocation(entity);
        return this.model.renderType(identifier);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(getRenderLayer(entity));
        matrices.pushPose();
        matrices.mulPose(Axis.YN.rotationDegrees(entity.getYRot()));
        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(tickDelta, entity.getPrevRotation(), entity.getRotation())));
        float p = (float)entity.getDamageWobbleTicks() - tickDelta;
        float q = Math.max(entity.getDamageWobbleStrength() - tickDelta, 0);
        if (p > 0.0f) {
            matrices.mulPose(Axis.XP.rotationDegrees(Mth.sin(p) * p * q / 10.0f * (float)entity.getDamageWobbleSide()));
        }
        this.model.render(entity, matrices, vertexConsumer, light, OverlayTexture.pack(0.0f, false), -1);
        matrices.popPose();
        matrices.popPose();
    }
}

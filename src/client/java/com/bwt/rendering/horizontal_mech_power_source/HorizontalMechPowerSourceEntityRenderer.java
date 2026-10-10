package com.bwt.rendering.horizontal_mech_power_source;

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

public abstract class HorizontalMechPowerSourceEntityRenderer<E extends HorizontalMechPowerSourceEntity, S extends HorizontalMechPowerSourceEntityRenderState> extends EntityRenderer<E, S> {
    protected HorizontalMechPowerSourceEntityModel<S> model;

    protected HorizontalMechPowerSourceEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Nullable
    protected RenderType getRenderLayer() {
        ResourceLocation identifier = this.getTextureLocation();
        return this.model.renderType(identifier);
    }

    protected abstract ResourceLocation getTextureLocation();

    @Override
    public void render(S renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        super.render(renderState, poseStack, multiBufferSource, light);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(getRenderLayer());
        poseStack.pushPose();
        poseStack.mulPose(Axis.YN.rotationDegrees(renderState.yRot));
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.rotation));
        if (renderState.hurtTime > 0.0f) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(renderState.hurtTime) * renderState.hurtTime * renderState.damageTime / 10.0f * renderState.hurtDir));
        }
        this.model.render(renderState, poseStack, vertexConsumer, light, OverlayTexture.pack(0.0f, false), -1);
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public void extractRenderState(E entity, S renderState, float tickDelta) {
        super.extractRenderState(entity, renderState, tickDelta);
        renderState.yRot = entity.getYRot();
        renderState.rotation = Mth.rotLerp(tickDelta, entity.getPrevRotation(), entity.getRotation());
        renderState.hurtTime = entity.getDamageWobbleTicks() - tickDelta;
        renderState.hurtDir = entity.getDamageWobbleSide();
        renderState.damageTime = Math.max(entity.getDamageWobbleStrength() - tickDelta, 0.0F);
    }
}

package com.bwt.models;

import com.bwt.entities.HorizontalMechPowerSourceEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

public abstract class HorizontalMechPowerSourceEntityRenderer<T extends HorizontalMechPowerSourceEntity> extends EntityRenderer<T> {
    protected HorizontalMechPowerSourceEntityModel<T> model;

    protected HorizontalMechPowerSourceEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Nullable
    protected RenderLayer getRenderLayer(T entity) {
        Identifier identifier = this.getTexture(entity);
        return this.model.getLayer(identifier);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(getRenderLayer(entity));
        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getYaw()));
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerpAngleDegrees(tickDelta, entity.getPrevRotation(), entity.getRotation())));
        float p = (float)entity.getDamageWobbleTicks() - tickDelta;
        float q = Math.max(entity.getDamageWobbleStrength() - tickDelta, 0);
        if (p > 0.0f) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(p) * p * q / 10.0f * (float)entity.getDamageWobbleSide()));
        }
        this.model.render(entity, matrices, vertexConsumer, light, OverlayTexture.getUv(0.0f, false), -1);
        matrices.pop();
        matrices.pop();
    }
}

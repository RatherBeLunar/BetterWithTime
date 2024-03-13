package com.bwt.models;

import com.bwt.entities.MovingAnchorEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MovingAnchorEntityRenderer extends EntityRenderer<MovingAnchorEntity> {
    protected MovingAnchorEntityModel model;

    public MovingAnchorEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Nullable
    protected RenderLayer getRenderLayer(MovingAnchorEntity entity) {
        Identifier identifier = this.getTexture(entity);
        return this.model.getLayer(identifier);
    }

    @Override
    public void render(MovingAnchorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(MovingAnchorEntity entity) {
        return null;
    }
}

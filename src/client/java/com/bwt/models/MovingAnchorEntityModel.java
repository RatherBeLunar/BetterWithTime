package com.bwt.models;

import com.bwt.entities.MovingAnchorEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class MovingAnchorEntityModel extends EntityModel<MovingAnchorEntity> {
    public MovingAnchorEntityModel() {}

    @Override
    public void setAngles(MovingAnchorEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    public void render(MovingAnchorEntity entity, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, float red, float green, float blue, float alpha) {

    }

    public final void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {}
}

package com.bwt.rendering.horizontal_mech_power_source;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class HorizontalMechPowerSourceEntityModel<S extends HorizontalMechPowerSourceEntityRenderState> extends EntityModel<S> {
    public HorizontalMechPowerSourceEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setupAnim(S entityRenderState) {
        super.setupAnim(entityRenderState);
    }

    public abstract void render(S renderState, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int uv, int color);
}

package com.bwt.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class CookingPotFillModel extends Model {
    protected final ModelPart root;
    protected final ModelPart flat;

    public CookingPotFillModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucentCull);
        this.root = root;
        this.flat = root.getChild("flat");
    }

    public static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("flat", ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(0, 0, 0, 16f, 0.1f, 16f), ModelTransform.NONE);
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = getModelData();
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}

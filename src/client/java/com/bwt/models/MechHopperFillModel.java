package com.bwt.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class MechHopperFillModel extends Model {
    protected final ModelPart root;
    protected final ModelPart flat;

    public MechHopperFillModel(ModelPart root) {
        super(RenderType::entityTranslucentCull);
        this.root = root;
        this.flat = root.getChild("flat");
    }

    public static MeshDefinition getModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("flat", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0, 0, 0, 16f, 0.1f, 16f), PartPose.ZERO);
        return modelData;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = getModelData();
        return LayerDefinition.create(modelData, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}

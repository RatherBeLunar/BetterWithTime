package com.bwt.models;

import com.bwt.entities.WaterWheelEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import java.util.ArrayList;
import java.util.List;

public class WaterWheelEntityModel extends HorizontalMechPowerSourceEntityModel<WaterWheelEntity> {
    protected static final int numBlades = 8;
    protected static final float strutDistanceFromCenter = 30.0f;
    private static final int strutLength = (int)( ( WaterWheelEntity.height * 8.0f ) - (strutDistanceFromCenter / 2.0f));
    private static final int bladeWidth = 2;

    private static final float bladeOffsetFromCenter = 2.5f;
    private static final int bladeLength = (int)( ( WaterWheelEntity.height * 8.0f ) - bladeOffsetFromCenter) + 1;
    private static final int strutWidth = 2;
    private static final int bladeDepth = 14;
    private static final int strutDepth = 12;

    private final List<ModelPart> blades = new ArrayList<>();
    private final List<ModelPart> struts = new ArrayList<>();

    public WaterWheelEntityModel(ModelPart modelPart) {
        super();
        for (int bladeIdx = 0; bladeIdx < numBlades; bladeIdx++) {
            blades.add(modelPart.getChild("blade" + bladeIdx));
            struts.add(modelPart.getChild("strut" + bladeIdx));
        }
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        float localPi = 3.141593F;
        for (int i = 0; i < numBlades; i++) {
            modelPartData.addOrReplaceChild("blade" + i,
                CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(bladeOffsetFromCenter, -(float) bladeWidth / 2.0f, -(float) bladeDepth / 2.0f,
                            bladeLength, bladeWidth, bladeDepth),
                PartPose.rotation(0F, 0F, localPi * (float) i / 4.0F));
        }
        for (int i = 0; i < numBlades; i++ ) {
            float rotation = localPi * 0.25f * i;

            modelPartData.addOrReplaceChild("strut" + i,
                CubeListBuilder.create()
                    .texOffs(0, 15)
                    .addBox(0, -(float) strutWidth / 2.0f, -(float) strutDepth / 2.0f,
                            strutLength, strutWidth, strutDepth),
                PartPose.offsetAndRotation(
                        ((float) (strutDistanceFromCenter * Math.cos(rotation))),
                        ((float) (strutDistanceFromCenter * Math.sin(rotation))),
                        0f,
                        0f,
                        0f,
                        (localPi * 0.625f) + (rotation)
                )
            );
        }
        return LayerDefinition.create(modelData, 64, 32);

    }

    @Override
    public void render(WaterWheelEntity entity, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int uv, int color) {
        blades.forEach(blade -> blade.render(poseStack, vertexConsumer, light, uv, color));
        struts.forEach(blade -> blade.render(poseStack, vertexConsumer, light, uv, color));
    }
}

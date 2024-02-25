package com.bwt;

import com.bwt.entities.WaterWheelEntity;
import com.bwt.entities.WindmillEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class WaterWheelEntityModel extends EntityModel<WindmillEntity> {
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
        for (int bladeIdx = 0; bladeIdx < numBlades; bladeIdx++) {
            blades.add(modelPart.getChild("blade" + bladeIdx));
            struts.add(modelPart.getChild("strut" + bladeIdx));
        }
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float localPi = 3.141593F;
        for (int i = 0; i < numBlades; i++) {
            modelPartData.addChild("blade" + i,
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(bladeOffsetFromCenter, -(float) bladeWidth / 2.0f, -(float) bladeDepth / 2.0f,
                            bladeLength, bladeWidth, bladeDepth),
                ModelTransform.rotation(0F, 0F, localPi * (float) i / 4.0F));
        }
        for (int i = 0; i < numBlades; i++ ) {
            float rotation = localPi * 0.25f * i;

            modelPartData.addChild("strut" + i,
                ModelPartBuilder.create()
                    .uv(0, 15)
                    .cuboid(0, -(float) strutWidth / 2.0f, -(float) strutDepth / 2.0f,
                            strutLength, strutWidth, strutDepth),
                ModelTransform.of(
                        ((float) (strutDistanceFromCenter * Math.cos(rotation))),
                        ((float) (strutDistanceFromCenter * Math.sin(rotation))),
                        0f,
                        0f,
                        0f,
                        (localPi * 0.625f) + (rotation)
                )
            );
        }
        return TexturedModelData.of(modelData, 64, 32);

    }

    @Override
    public void setAngles(WindmillEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        blades.forEach(blade -> blade.render(matrices, vertices, light, overlay));
        struts.forEach(blade -> blade.render(matrices, vertices, light, overlay));
    }
}

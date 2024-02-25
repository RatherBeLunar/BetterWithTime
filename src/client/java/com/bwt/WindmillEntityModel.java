package com.bwt;

import com.bwt.entities.WindmillEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class WindmillEntityModel extends HorizontalMechPowerSourceEntityModel<WindmillEntity> {

    private static final float bladeOffsetFromCenter = 15.0f;
    private static final int bladeLength = (int)( ( WindmillEntity.height * 8.0f ) - bladeOffsetFromCenter) - 3;
    private static final int bladeWidth = 16;

    private static final float shaftOffsetFromCenter = 2.5f;
    private static final int shaftLength = (int)( ( WindmillEntity.height * 8.0f ) - shaftOffsetFromCenter) - 2;
    private static final int shaftWidth = 4;

    private final ModelPart sail0;
    private final ModelPart sail1;
    private final ModelPart sail2;
    private final ModelPart sail3;
    private final ModelPart shaft0;
    private final ModelPart shaft1;
    private final ModelPart shaft2;
    private final ModelPart shaft3;

    public WindmillEntityModel(ModelPart modelPart) {
        super();
        this.shaft0 = modelPart.getChild("shaft0");
        this.shaft1 = modelPart.getChild("shaft1");
        this.shaft2 = modelPart.getChild("shaft2");
        this.shaft3 = modelPart.getChild("shaft3");
        this.sail0 = modelPart.getChild("sail0");
        this.sail1 = modelPart.getChild("sail1");
        this.sail2 = modelPart.getChild("sail2");
        this.sail3 = modelPart.getChild("sail3");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float localPi = 3.141593F;
        for (int i = 0; i < 4; i++) {
            modelPartData.addChild("shaft" + i,
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(shaftOffsetFromCenter, -(float) shaftWidth / 2.0f, -(float) shaftWidth / 2.0f,
                            shaftLength, shaftWidth, shaftWidth),
                ModelTransform.rotation(0F, 0F, localPi * (float)( i - 4 ) / 2.0F));
        }
        for (int i = 4; i < 8; i++ ) {
            modelPartData.addChild("sail" + (i - 4),
                ModelPartBuilder.create()
                    .uv(0, 15)
                    .cuboid(bladeOffsetFromCenter, 1.75f/*-(float)iBladeWidth / 2.0f*/, 1.0F, bladeLength, bladeWidth, 1 ),
                ModelTransform.rotation(-localPi / 12.0F, 0F, localPi * (float)i / 2.0F)
            );
        }
        return TexturedModelData.of(modelData, 64, 32);

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        shaft0.render(matrices, vertices, light, overlay);
        shaft1.render(matrices, vertices, light, overlay);
        shaft2.render(matrices, vertices, light, overlay);
        shaft3.render(matrices, vertices, light, overlay);
        sail0.render(matrices, vertices, light, overlay);
        sail1.render(matrices, vertices, light, overlay);
        sail2.render(matrices, vertices, light, overlay);
        sail3.render(matrices, vertices, light, overlay);
    }
}

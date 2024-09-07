package com.bwt.models;

import com.bwt.entities.WindmillEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WindmillEntityModel extends HorizontalMechPowerSourceEntityModel<WindmillEntity> {

    private static final float bladeOffsetFromCenter = 15.0f;
    private static final int bladeLength = (int)( ( WindmillEntity.height * 8.0f ) - bladeOffsetFromCenter) - 3;
    private static final int bladeWidth = 16;

    private static final float shaftOffsetFromCenter = 2.5f;
    private static final int shaftLength = (int)( ( WindmillEntity.height * 8.0f ) - shaftOffsetFromCenter) - 2;
    private static final int shaftWidth = 4;

    private final List<ModelPart> sails;
    private final List<ModelPart> shafts;


    public WindmillEntityModel(ModelPart modelPart) {
        super();
        shafts = IntStream.range(0, WindmillEntity.NUM_SAILS).mapToObj(i -> modelPart.getChild("shaft" + i)).collect(Collectors.toList());
        sails = IntStream.range(0, WindmillEntity.NUM_SAILS).mapToObj(i -> modelPart.getChild("sail" + i)).collect(Collectors.toList());
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float localPi = 3.141593F;
        for (int i = 0; i < WindmillEntity.NUM_SAILS; i++) {
            modelPartData.addChild("shaft" + i,
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(shaftOffsetFromCenter, -(float) shaftWidth / 2.0f, -(float) shaftWidth / 2.0f,
                            shaftLength, shaftWidth, shaftWidth),
                ModelTransform.rotation(0F, 0F, 2 * localPi * (float)i / (float)WindmillEntity.NUM_SAILS));
        }
        for (int i = WindmillEntity.NUM_SAILS; i < WindmillEntity.NUM_SAILS * 2; i++ ) {
            modelPartData.addChild("sail" + (i - WindmillEntity.NUM_SAILS),
                ModelPartBuilder.create()
                    .uv(0, 15)
                    .cuboid(bladeOffsetFromCenter, 1.75f/*-(float)iBladeWidth / 2.0f*/, 1.0F, bladeLength, bladeWidth, 1 ),
                ModelTransform.rotation(-localPi / 12.0F, 0F, 2 * localPi * (float)(i - 4) / (float)WindmillEntity.NUM_SAILS));
        }
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void render(WindmillEntity entity, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        shafts.forEach(shaft -> shaft.render(matrices, vertices, light, overlay));
        for (int i = 0; i < WindmillEntity.NUM_SAILS; i++) {
            ModelPart sail = sails.get(i);
            DyeColor sailColor = entity.getSailColor(i);
            sail.render(matrices, vertices, light, overlay, ColorHelper.Argb.mixColor(color, sailColor.getEntityColor()));
        }
    }
}

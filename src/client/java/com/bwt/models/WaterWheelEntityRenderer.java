package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.entities.WaterWheelEntity;
import com.bwt.utils.Id;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WaterWheelEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WaterWheelEntity> {
    public WaterWheelEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WaterWheelEntityModel(context.bakeLayer(BetterWithTimeClient.MODEL_WATER_WHEEL_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(WaterWheelEntity entity) {
        return Id.of("textures/entity/water_wheel.png");
    }
}

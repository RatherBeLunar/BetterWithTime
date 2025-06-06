package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.entities.WaterWheelEntity;
import com.bwt.utils.Id;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class WaterWheelEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WaterWheelEntity> {
    public WaterWheelEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new WaterWheelEntityModel(context.getPart(BetterWithTimeClient.MODEL_WATER_WHEEL_LAYER));
    }

    @Override
    public Identifier getTexture(WaterWheelEntity entity) {
        return Id.of("textures/entity/water_wheel.png");
    }
}

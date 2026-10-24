package com.bwt.rendering.water_wheel;

import com.bwt.BetterWithTimeClient;
import com.bwt.entities.WaterWheelEntity;
import com.bwt.rendering.horizontal_mech_power_source.HorizontalMechPowerSourceEntityRenderer;
import com.bwt.utils.Id;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class WaterWheelEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WaterWheelEntity, WaterWheelRenderState> {
    public WaterWheelEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WaterWheelEntityModel(context.bakeLayer(BetterWithTimeClient.MODEL_WATER_WHEEL_LAYER));
    }

    @Override
    protected Identifier getTextureLocation() {
        return Id.of("textures/entity/water_wheel.png");
    }

    @Override
    public WaterWheelRenderState createRenderState() {
        return new WaterWheelRenderState();
    }
}

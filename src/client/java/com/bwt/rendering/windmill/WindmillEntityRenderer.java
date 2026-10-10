package com.bwt.rendering.windmill;

import com.bwt.BetterWithTimeClient;
import com.bwt.entities.WindmillEntity;
import com.bwt.rendering.horizontal_mech_power_source.HorizontalMechPowerSourceEntityRenderer;
import com.bwt.utils.Id;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class WindmillEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WindmillEntity, WindmillRenderState> {
    public WindmillEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WindmillEntityModel(context.bakeLayer(BetterWithTimeClient.MODEL_WINDMILL_LAYER));
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return Id.of("textures/entity/windmill.png");
    }

    @Override
    public WindmillRenderState createRenderState() {
        return new WindmillRenderState();
    }

    @Override
    public void extractRenderState(WindmillEntity entity, WindmillRenderState renderState, float tickDelta) {
        super.extractRenderState(entity, renderState, tickDelta);
        renderState.dyeColors = entity.getSailColors();
    }
}

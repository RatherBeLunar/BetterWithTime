package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.entities.WindmillEntity;
import com.bwt.utils.Id;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WindmillEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WindmillEntity> {
    public WindmillEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WindmillEntityModel(context.bakeLayer(BetterWithTimeClient.MODEL_WINDMILL_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(WindmillEntity entity) {
        return Id.of("textures/entity/windmill.png");
    }
}

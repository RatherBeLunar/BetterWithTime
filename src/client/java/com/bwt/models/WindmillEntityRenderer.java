package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.entities.WindmillEntity;
import com.bwt.utils.Id;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class WindmillEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WindmillEntity> {
    public WindmillEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new WindmillEntityModel(context.getPart(BetterWithTimeClient.MODEL_WINDMILL_LAYER));
    }

    @Override
    public Identifier getTexture(WindmillEntity entity) {
        return Id.of("textures/entity/windmill.png");
    }
}

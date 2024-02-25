package com.bwt;

import com.bwt.entities.WindmillEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class WindmillEntityRenderer extends HorizontalMechPowerSourceEntityRenderer<WindmillEntity> {
    public WindmillEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new WindmillEntityModel(context.getPart(BetterWithTimeClient.MODEL_WINDMILL_LAYER));
    }

    @Override
    public Identifier getTexture(WindmillEntity entity) {
        return new Identifier("bwt", "textures/entity/windmill.png");
    }
}

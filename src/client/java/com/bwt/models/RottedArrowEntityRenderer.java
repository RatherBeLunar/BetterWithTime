package com.bwt.models;

import com.bwt.entities.BroadheadArrowEntity;
import com.bwt.entities.RottedArrowEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class RottedArrowEntityRenderer extends ProjectileEntityRenderer<RottedArrowEntity> {
    public static final Identifier TEXTURE = Id.of("textures/entity/rotted_arrows.png");

    public RottedArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(RottedArrowEntity arrowEntity) {
        return TEXTURE;
    }
}
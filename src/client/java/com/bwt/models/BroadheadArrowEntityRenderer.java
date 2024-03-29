package com.bwt.models;

import com.bwt.entities.BroadheadArrowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class BroadheadArrowEntityRenderer extends ProjectileEntityRenderer<BroadheadArrowEntity> {
    public static final Identifier TEXTURE = new Identifier("bwt", "textures/entity/broadhead_arrows.png");

    public BroadheadArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BroadheadArrowEntity arrowEntity) {
        return TEXTURE;
    }
}
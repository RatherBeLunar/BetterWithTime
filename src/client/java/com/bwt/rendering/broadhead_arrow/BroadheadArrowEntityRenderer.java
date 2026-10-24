package com.bwt.rendering.broadhead_arrow;

import com.bwt.entities.BroadheadArrowEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

@Environment(value= EnvType.CLIENT)
public class BroadheadArrowEntityRenderer extends ArrowRenderer<BroadheadArrowEntity, BroadheadArrowRenderState> {
    public static final Identifier TEXTURE = Id.of("textures/entity/broadhead_arrows.png");

    public BroadheadArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BroadheadArrowRenderState createRenderState() {
        return new BroadheadArrowRenderState();
    }

    @Override
    protected Identifier getTextureLocation(BroadheadArrowRenderState arrowRenderState) {
        return TEXTURE;
    }
}
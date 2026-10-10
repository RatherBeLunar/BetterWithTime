package com.bwt.rendering.rotted_arrow;

import com.bwt.entities.RottedArrowEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

@Environment(value= EnvType.CLIENT)
public class RottedArrowEntityRenderer extends ArrowRenderer<RottedArrowEntity, RottedArrowRenderState> {
    public static final ResourceLocation TEXTURE = Id.of("textures/entity/rotted_arrows.png");

    public RottedArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public RottedArrowRenderState createRenderState() {
        return new RottedArrowRenderState();
    }

    @Override
    protected ResourceLocation getTextureLocation(RottedArrowRenderState arrowRenderState) {
        return TEXTURE;
    }
}
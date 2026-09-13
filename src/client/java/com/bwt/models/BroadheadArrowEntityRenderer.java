package com.bwt.models;

import com.bwt.entities.BroadheadArrowEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(value= EnvType.CLIENT)
public class BroadheadArrowEntityRenderer extends ArrowRenderer<BroadheadArrowEntity> {
    public static final ResourceLocation TEXTURE = Id.of("textures/entity/broadhead_arrows.png");

    public BroadheadArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BroadheadArrowEntity arrowEntity) {
        return TEXTURE;
    }
}
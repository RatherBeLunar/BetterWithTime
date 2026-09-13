package com.bwt.models;

import com.bwt.entities.DynamiteEntity;
import com.bwt.utils.Id;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

@Environment(value= EnvType.CLIENT)
public class DynamiteEntityRenderer extends EntityRenderer<DynamiteEntity> {
    public static final ResourceLocation TEXTURE = Id.of("textures/item/dynamite.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public DynamiteEntityRenderer(EntityRendererProvider.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public DynamiteEntityRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLightLevel(DynamiteEntity entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(DynamiteEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (entity.tickCount < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25) {
            return;
        }
        matrices.pushPose();
        matrices.scale(this.scale, this.scale, this.scale);
        matrices.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrices.mulPose(Axis.YP.rotationDegrees(180.0f));
        int overlay = (entity.getFuse() / 5 % 2 == 0) ? OverlayTexture.pack(OverlayTexture.u(1.0f), 10) : OverlayTexture.NO_OVERLAY;
        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.level(), entity.getId());
        matrices.popPose();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DynamiteEntity entity) {
        return TEXTURE;
    }
}
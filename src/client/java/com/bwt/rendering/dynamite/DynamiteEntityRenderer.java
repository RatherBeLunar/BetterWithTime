package com.bwt.rendering.dynamite;

import com.bwt.entities.DynamiteEntity;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(value= EnvType.CLIENT)
public class DynamiteEntityRenderer extends EntityRenderer<DynamiteEntity, DynamiteRenderState> {
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

    public void render(DynamiteRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        if (renderState.ageInTicks < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr(renderState.x, renderState.y, renderState.z) < 12.25) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(this.scale, this.scale, this.scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        int overlay = (renderState.fuse / 5 % 2 == 0) ? OverlayTexture.pack(OverlayTexture.u(1.0f), 10) : OverlayTexture.NO_OVERLAY;
        if (renderState.itemModel != null) {
            this.itemRenderer.render(
                    renderState.itemStack,
                    ItemDisplayContext.GROUND,
                    false,
                    poseStack,
                    multiBufferSource,
                    light,
                    overlay,
                    renderState.itemModel
            );
        }
        poseStack.popPose();
        super.render(renderState, poseStack, multiBufferSource, light);
    }

    @Override
    public DynamiteRenderState createRenderState() {
        return new DynamiteRenderState();
    }

    @Override
    public void extractRenderState(DynamiteEntity entity, DynamiteRenderState entityRenderState, float f) {
        super.extractRenderState(entity, entityRenderState, f);
        entityRenderState.fuse = entity.getFuse();
        ItemStack itemStack = entity.getItem();
        entityRenderState.itemStack = itemStack;
        entityRenderState.itemModel = !itemStack.isEmpty() ? this.itemRenderer.getModel(itemStack, entity.level(), null, entity.getId()) : null;
    }
}
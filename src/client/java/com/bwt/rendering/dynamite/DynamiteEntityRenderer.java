package com.bwt.rendering.dynamite;

import com.bwt.entities.DynamiteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(value= EnvType.CLIENT)
public class DynamiteEntityRenderer extends EntityRenderer<DynamiteEntity, DynamiteRenderState> {
    private final float scale;
    private final boolean lit;
    private final ItemModelResolver itemModelResolver;

    public DynamiteEntityRenderer(EntityRendererProvider.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelResolver = ctx.getItemModelResolver();
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
    public void submit(DynamiteRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.scale(this.scale, this.scale, this.scale);
        poseStack.mulPose(cameraRenderState.orientation);
//        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        int overlay = (renderState.fuse / 5 % 2 == 0) ? OverlayTexture.pack(OverlayTexture.u(1.0f), 10) : OverlayTexture.NO_OVERLAY;
        renderState.item.submit(
                poseStack,
                submitNodeCollector,
                renderState.lightCoords,
                overlay,
                renderState.outlineColor
        );
        poseStack.popPose();
        super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public DynamiteRenderState createRenderState() {
        return new DynamiteRenderState();
    }

    @Override
    public void extractRenderState(DynamiteEntity entity, DynamiteRenderState entityRenderState, float f) {
        super.extractRenderState(entity, entityRenderState, f);
        this.itemModelResolver.updateForNonLiving(entityRenderState.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
        entityRenderState.fuse = entity.getFuse();
    }
}
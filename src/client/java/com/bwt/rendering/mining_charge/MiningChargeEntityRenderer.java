package com.bwt.rendering.mining_charge;

import com.bwt.entities.MiningChargeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.util.Mth;

@Environment(value=EnvType.CLIENT)
public class MiningChargeEntityRenderer extends EntityRenderer<MiningChargeEntity, MiningChargeRenderState> {
    private final BlockRenderDispatcher blockRenderManager;

    public MiningChargeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(MiningChargeRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0f, 0.5f, 0.0f);
        float fuse = renderState.ticksBeforeExplosion;
        if (fuse < 10.0) {
            float blowingUpScale = (float)(1.0f + Math.pow(Mth.clamp(1.0f - fuse / 10.0f, 0.0f, 1.0f), 3) * 0.3f);
            poseStack.scale(blowingUpScale, blowingUpScale, blowingUpScale);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
        poseStack.translate(-0.5f, -0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderManager, renderState.blockState, poseStack, multiBufferSource, light,(int) fuse / 5 % 2 == 0);
        poseStack.popPose();
        super.render(renderState, poseStack, multiBufferSource, light);
    }

    @Override
    public MiningChargeRenderState createRenderState() {
        return new MiningChargeRenderState();
    }

    @Override
    public void extractRenderState(MiningChargeEntity entity, MiningChargeRenderState renderState, float tickDelta) {
        super.extractRenderState(entity, renderState, tickDelta);
        renderState.blockState = entity.getBlockState();
        renderState.ticksBeforeExplosion = entity.getFuse() - tickDelta + 1.0f;
    }
}


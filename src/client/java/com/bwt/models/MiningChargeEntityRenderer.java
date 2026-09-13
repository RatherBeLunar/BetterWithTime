/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package com.bwt.models;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

@Environment(value=EnvType.CLIENT)
public class MiningChargeEntityRenderer extends EntityRenderer<MiningChargeEntity> {
    private final BlockRenderDispatcher blockRenderManager;

    public MiningChargeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(MiningChargeEntity miningChargeEntity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.translate(0.0f, 0.5f, 0.0f);
        int fuse = miningChargeEntity.getFuse();
        double ticksBeforeExplosion = fuse - tickDelta + 1.0f;
        if (ticksBeforeExplosion < 10.0) {
            float blowingUpScale = (float)(1.0f + Math.pow(Mth.clamp(1.0f - ticksBeforeExplosion / 10.0f, 0.0f, 1.0f), 3) * 0.3f);
            matrices.scale(blowingUpScale, blowingUpScale, blowingUpScale);
        }
        matrices.mulPose(Axis.YP.rotationDegrees(-90.0f));
        matrices.translate(-0.5f, -0.5f, 0.5f);
        matrices.mulPose(Axis.YP.rotationDegrees(90.0f));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderManager, miningChargeEntity.getBlockState(), matrices, vertexConsumers, light, fuse / 5 % 2 == 0);
        matrices.popPose();
        super.render(miningChargeEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(MiningChargeEntity tntEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}


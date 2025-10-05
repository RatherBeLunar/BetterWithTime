/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package com.bwt.models;

import com.bwt.entities.MiningChargeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class MiningChargeEntityRenderer extends EntityRenderer<MiningChargeEntity> {
    private final BlockRenderManager blockRenderManager;

    public MiningChargeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(MiningChargeEntity miningChargeEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0f, 0.5f, 0.0f);
        int fuse = miningChargeEntity.getFuse();
        double ticksBeforeExplosion = fuse - tickDelta + 1.0f;
        if (ticksBeforeExplosion < 10.0) {
            float blowingUpScale = (float)(1.0f + Math.pow(MathHelper.clamp(1.0f - ticksBeforeExplosion / 10.0f, 0.0f, 1.0f), 3) * 0.3f);
            matrices.scale(blowingUpScale, blowingUpScale, blowingUpScale);
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.translate(-0.5f, -0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        TntMinecartEntityRenderer.renderFlashingBlock(this.blockRenderManager, miningChargeEntity.getBlockState(), matrices, vertexConsumers, light, fuse / 5 % 2 == 0);
        matrices.pop();
        super.render(miningChargeEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(MiningChargeEntity tntEntity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}


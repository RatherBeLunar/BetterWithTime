package com.bwt.mixin.client;

import com.bwt.render_layers.KilnBlockCookingRenderLayer;
import com.bwt.utils.KilnBlockCookProgressSetter;
import com.bwt.utils.kiln_block_cook_overlay.KilnBlockCookingInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements KilnBlockCookProgressSetter {
    @Unique
    private final Long2ObjectMap<KilnBlockCookingInfo> kilnBlockCookingInfos = new Long2ObjectOpenHashMap<>();

    @Accessor
    public abstract RenderBuffers getRenderBuffers();

    @Accessor
    public abstract Minecraft getMinecraft();

    @Accessor
    public abstract ClientLevel getLevel();

    @Unique
    public void bwt$setKilnBlockCookingInfo(BlockPos pos, int stage) {
        if (stage >= 0 && stage < 10) {
            KilnBlockCookingInfo kilnBlockCookingInfo = this.kilnBlockCookingInfos.get(pos.asLong());

            if (kilnBlockCookingInfo == null
                    || kilnBlockCookingInfo.getPos().getX() != pos.getX()
                    || kilnBlockCookingInfo.getPos().getY() != pos.getY()
                    || kilnBlockCookingInfo.getPos().getZ() != pos.getZ()) {
                kilnBlockCookingInfo = new KilnBlockCookingInfo(pos);
                this.kilnBlockCookingInfos.put(pos.asLong(), kilnBlockCookingInfo);
            }

            kilnBlockCookingInfo.setStage(stage);
        } else {
            this.removeKilnBlockCookingInfo(pos);
        }
    }

    @Unique
    private void removeKilnBlockCookingInfo(BlockPos pos) {
        this.kilnBlockCookingInfos.remove(pos.asLong());
    }
    
    @Inject(method = "renderBlockDestroyAnimation", at = @At(value = "HEAD"))
    public void bwt$renderKilnCookingProgressAnimation(PoseStack poseStack, Camera camera, MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        Vec3 cameraPos = camera.getPosition();
        double cameraX = cameraPos.x();
        double cameraY = cameraPos.y();
        double cameraZ = cameraPos.z();

        for (Long2ObjectMap.Entry<KilnBlockCookingInfo> entry : this.kilnBlockCookingInfos.long2ObjectEntrySet()) {
            BlockPos blockPos = BlockPos.of(entry.getLongKey());
            double distX = blockPos.getX() - cameraX;
            double distY = blockPos.getY() - cameraY;
            double distZ = blockPos.getZ() - cameraZ;
            if (cameraPos.distanceToSqr(Vec3.atLowerCornerOf(blockPos)) > 1024.0) {
                continue;
            }
            KilnBlockCookingInfo kilnBlockCookingInfo = entry.getValue();
            if (kilnBlockCookingInfo != null) {
                int stage = kilnBlockCookingInfo.getStage();
                poseStack.pushPose();
                poseStack.translate(distX, distY, distZ);
                PoseStack.Pose poseStackEntry = poseStack.last();
                VertexConsumer vertexConsumer2 = new SheetedDecalTextureGenerator(
                        getRenderBuffers().crumblingBufferSource().getBuffer(KilnBlockCookingRenderLayer.KILN_COOKING_RENDER_LAYERS.get(stage)),
                        poseStackEntry,
                        1.0F
                );
                getMinecraft().getBlockRenderer().renderBreakingTexture(getLevel().getBlockState(blockPos), blockPos, getLevel(), poseStack, vertexConsumer2);
                poseStack.popPose();
            }
        }
    }

}

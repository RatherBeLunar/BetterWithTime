package com.bwt.rendering.canvas;

import com.bwt.entities.CanvasEntity;
import com.bwt.utils.Id;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CanvasRenderer extends EntityRenderer<CanvasEntity, CanvasRenderState> {
    private static final PaintingVariant BACK = new PaintingVariant(
            1,
            1,
            Id.of("back"),
            Optional.empty(),
            Optional.empty()
    );

    public CanvasRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CanvasRenderState paintingRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        PaintingVariant paintingVariant = paintingRenderState.variant;
        if (paintingVariant == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - paintingRenderState.direction.get2DDataValue() * 90));
        PaintingTextureManager paintingTextureManager = Minecraft.getInstance().getPaintingTextures();
        TextureAtlasSprite backSprite = paintingTextureManager.get(BACK);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolidZOffsetForward(backSprite.atlasLocation()));
        this.renderCanvas(
                poseStack,
                vertexConsumer,
                paintingRenderState.lightCoords,
                paintingVariant.width(),
                paintingVariant.height(),
                paintingTextureManager.get(paintingVariant),
                backSprite
        );
        poseStack.popPose();
        super.render(paintingRenderState, poseStack, multiBufferSource, light);
    }

    @Override
    public CanvasRenderState createRenderState() {
        return new CanvasRenderState();
    }

    @Override
    public void extractRenderState(CanvasEntity canvasEntity, CanvasRenderState renderState, float f) {
        super.extractRenderState(canvasEntity, renderState, f);
        Direction direction = canvasEntity.getDirection();
        PaintingVariant paintingVariant = canvasEntity.getVariant().value();
        renderState.direction = direction;
        renderState.variant = paintingVariant;
        int width = paintingVariant.width();
        int height = paintingVariant.height();
        if (renderState.lightCoords.length != width * height) {
            renderState.lightCoords = new int[width * height];
        }

        Level level = canvasEntity.level();

        for (int k = 0; k < height; k++) {
            for (int l = 0; l < width; l++) {
                float horizontalOffset = l - (width / 2.0F) + 0.5F;
                float verticalOffset = k - (height / 2.0F) + 0.5F;
                int posX = canvasEntity.getBlockX();
                int posY = Mth.floor(canvasEntity.getY() + verticalOffset);
                int posZ = canvasEntity.getBlockZ();
                switch (direction) {
                    case NORTH:
                        posX = Mth.floor(canvasEntity.getX() + horizontalOffset);
                        break;
                    case WEST:
                        posZ = Mth.floor(canvasEntity.getZ() - horizontalOffset);
                        break;
                    case SOUTH:
                        posX = Mth.floor(canvasEntity.getX() - horizontalOffset);
                        break;
                    case EAST:
                        posZ = Mth.floor(canvasEntity.getZ() + horizontalOffset);
                }

                renderState.lightCoords[l + k * width] = LevelRenderer.getLightColor(level, new BlockPos(posX, posY, posZ));
            }
        }
    }

    private void renderCanvas(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int[] lightCoords,
            int width,
            int height,
            TextureAtlasSprite frontSprite,
            TextureAtlasSprite backSprite
    ) {
        PoseStack.Pose pose = poseStack.last();
        float f = -width / 2.0F;
        float g = -height / 2.0F;
        float h = 0.03125F;
        float r = backSprite.getV(2 * h);
        float t = backSprite.getU(2 * h);

        for (int w = 0; w < width; w++) {
            for (int x = 0; x < height; x++) {
                float y = f + (w + 1);
                float z = f + w;
                float aa = g + (x + 1);
                float ab = g + x;
                int ac = lightCoords[w + x * width];
                float ad = frontSprite.getU((float)((1.0 / width) * (width - w)));
                float ae = frontSprite.getU((float)((1.0 / width) * (width - (w + 1))));
                float af = frontSprite.getV((float)((1.0 / height) * (height - x)));
                float ag = frontSprite.getV((float)((1.0 / height) * (height - (x + 1))));
                this.vertex(pose, vertexConsumer, y, ab, ae, af, -h, 0, 0, -1, ac);
                this.vertex(pose, vertexConsumer, z, ab, ad, af, -h, 0, 0, -1, ac);
                this.vertex(pose, vertexConsumer, z, aa, ad, ag, -h, 0, 0, -1, ac);
                this.vertex(pose, vertexConsumer, y, aa, ae, ag, -h, 0, 0, -1, ac);
                this.vertex(pose, vertexConsumer, y, aa, backSprite.getU1(), backSprite.getV0(), h, 0, 0, 1, ac);
                this.vertex(pose, vertexConsumer, z, aa, backSprite.getU0(), backSprite.getV0(), h, 0, 0, 1, ac);
                this.vertex(pose, vertexConsumer, z, ab, backSprite.getU0(), backSprite.getV1(), h, 0, 0, 1, ac);
                this.vertex(pose, vertexConsumer, y, ab, backSprite.getU1(), backSprite.getV1(), h, 0, 0, 1, ac);
                this.vertex(pose, vertexConsumer, y, aa, backSprite.getU0(), backSprite.getV0(), -h, 0, 1, 0, ac);
                this.vertex(pose, vertexConsumer, z, aa, backSprite.getU1(), backSprite.getV0(), -h, 0, 1, 0, ac);
                this.vertex(pose, vertexConsumer, z, aa, backSprite.getU1(), r, h, 0, 1, 0, ac);
                this.vertex(pose, vertexConsumer, y, aa, backSprite.getU0(), r, h, 0, 1, 0, ac);
                this.vertex(pose, vertexConsumer, y, ab, backSprite.getU0(), backSprite.getV0(), h, 0, -1, 0, ac);
                this.vertex(pose, vertexConsumer, z, ab, backSprite.getU1(), backSprite.getV0(), h, 0, -1, 0, ac);
                this.vertex(pose, vertexConsumer, z, ab, backSprite.getU1(), r, -h, 0, -1, 0, ac);
                this.vertex(pose, vertexConsumer, y, ab, backSprite.getU0(), r, -h, 0, -1, 0, ac);
                this.vertex(pose, vertexConsumer, y, aa, t, backSprite.getV0(), h, -1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, y, ab, t, backSprite.getV1(), h, -1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, y, ab, backSprite.getU0(), backSprite.getV1(), -h, -1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, y, aa, backSprite.getU0(), backSprite.getV0(), -h, -1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, z, aa, t, backSprite.getV0(), -h, 1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, z, ab, t, backSprite.getV1(), -h, 1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, z, ab, backSprite.getU0(), backSprite.getV1(), h, 1, 0, 0, ac);
                this.vertex(pose, vertexConsumer, z, aa, backSprite.getU0(), backSprite.getV0(), h, 1, 0, 0, ac);
            }
        }
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, int k, int l, int m, int n) {
        vertexConsumer
                .addVertex(pose, f, g, j)
                .setColor(-1)
                .setUv(h, i)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(n)
                .setNormal(pose, k, l, m);
    }
}

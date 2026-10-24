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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;

public class CanvasRenderer extends EntityRenderer<CanvasEntity, CanvasRenderState> {
    private static final ResourceLocation BACK_SPRITE_LOCATION = Id.of("back");
    private final TextureAtlas paintingsAtlas;

    public CanvasRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.paintingsAtlas = context.getAtlas(AtlasIds.PAINTINGS);
    }

    @Override
    public void submit(CanvasRenderState paintingRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        PaintingVariant paintingVariant = paintingRenderState.variant;
        if (paintingVariant == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - paintingRenderState.direction.get2DDataValue() * 90));
        TextureAtlasSprite frontSprite = this.paintingsAtlas.getSprite(paintingVariant.assetId());
        TextureAtlasSprite backSprite = this.paintingsAtlas.getSprite(BACK_SPRITE_LOCATION);
        this.renderCanvas(
                poseStack,
                submitNodeCollector,
                RenderType.entitySolidZOffsetForward(backSprite.atlasLocation()),
                paintingRenderState.lightCoordsPerBlock,
                paintingVariant.width(),
                paintingVariant.height(),
                frontSprite,
                backSprite
        );
        poseStack.popPose();
        super.submit(paintingRenderState, poseStack, submitNodeCollector, cameraRenderState);
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
        if (renderState.lightCoordsPerBlock.length != width * height) {
            renderState.lightCoordsPerBlock = new int[width * height];
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

                renderState.lightCoordsPerBlock[l + k * width] = LevelRenderer.getLightColor(level, new BlockPos(posX, posY, posZ));
            }
        }
    }

    private void renderCanvas(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            RenderType renderType,
            int[] lightCoords,
            int width,
            int height,
            TextureAtlasSprite frontSprite,
            TextureAtlasSprite backSprite
    ) {
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
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
        });
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

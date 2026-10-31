package com.bwt.rendering.moving_rope_platform;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.MovingRopeEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class MovingRopeEntityRenderer extends EntityRenderer<MovingRopeEntity, MovingRopeRenderState> {
    private final BlockRenderDispatcher blockRenderManager;
    protected static BlockAndTintGetter tintGetter = EmptyBlockAndTintGetter.INSTANCE;

    public MovingRopeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderDispatcher();
    }

    @Override
    public void submit(MovingRopeRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
        BlockPos pulleyPos = renderState.pulleyPos;
        if (pulleyPos == null) {
            return;
        }

        poseStack.pushPose();
        BlockPos blockPos = BlockPos.containing(renderState.x, renderState.boundingBoxMaxY, renderState.z);

        poseStack.pushPose();

        poseStack.translate(-0.5, 0, -0.5);

        BlockState ropeState = BwtBlocks.ropeBlock.defaultBlockState();
        for (int i = 0; pulleyPos.getY() - renderState.y > i && i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate(0, i, 0);
            poseStack.scale(1.001f, 1.001f, 1.001f);
            submitNodeCollector.submitBlock(
                    poseStack,
                    ropeState,
                    renderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    renderState.outlineColor
            );
            poseStack.popPose();
        }

        for (Map.Entry<Vec3i, BlockState> entry : renderState.blockMap.entrySet()) {
            poseStack.pushPose();
            Vec3i offset = entry.getKey();
            BlockState connectedBlockState = entry.getValue();
            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
            submitNodeCollector.submitBlock(
                    poseStack,
                    connectedBlockState,
                    renderState.lightMap.get(offset),
                    OverlayTexture.NO_OVERLAY,
                    renderState.outlineColor
            );
            poseStack.popPose();
        }
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public MovingRopeRenderState createRenderState() {
        return new MovingRopeRenderState();
    }

    @Override
    public void extractRenderState(MovingRopeEntity entity, MovingRopeRenderState renderState, float tickDelta) {
        super.extractRenderState(entity, renderState, tickDelta);
        renderState.pulleyPos = entity.getPulleyPos();
        renderState.boundingBoxMaxY = entity.getBoundingBox().maxY;
        renderState.blockPosition = entity.blockPosition();
        renderState.blockMap = entity.getBlockMap();
        renderState.lightMap.clear();
        renderState.blockMap.keySet()
                .forEach(offset -> renderState.lightMap.put(
                        offset,
                        this.getPackedLightCoords(
                                entity,
                                renderState.blockPosition.offset(offset)
                        )
                ));
    }

    public final int getPackedLightCoords(MovingRopeEntity entity, BlockPos blockPos) {
        return LightTexture.pack(
                this.getBlockLightLevel(entity, blockPos),
                this.getSkyLightLevel(entity, blockPos)
        );
    }
}

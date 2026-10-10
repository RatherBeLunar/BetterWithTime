package com.bwt.rendering.moving_rope_platform;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.MovingRopeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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
    public void render(MovingRopeRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        super.render(renderState, poseStack, multiBufferSource, light);
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
            this.blockRenderManager.getModelRenderer().tesselateBlock(
                    tintGetter,
                    this.blockRenderManager.getBlockModel(ropeState),
                    ropeState,
                    blockPos.above(i),
                    poseStack,
                    multiBufferSource.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(ropeState)),
                    false,
                    RandomSource.create(),
                    ropeState.getSeed(renderState.blockPosition),
                    OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
        }

        for (Map.Entry<Vec3i, BlockState> entry : renderState.blockMap.entrySet()) {
            poseStack.pushPose();
            Vec3i offset = entry.getKey();
            BlockState connectedBlockState = entry.getValue();
            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
            this.blockRenderManager.getModelRenderer().tesselateBlock(
                    tintGetter,
                    this.blockRenderManager.getBlockModel(connectedBlockState),
                    connectedBlockState,
                    blockPos.offset(offset),
                    poseStack,
                    multiBufferSource.getBuffer(RenderType.cutout()),
                    false,
                    RandomSource.create(),
                    connectedBlockState.getSeed(renderState.blockPosition),
                    OverlayTexture.NO_OVERLAY
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
    public void extractRenderState(MovingRopeEntity entity, MovingRopeRenderState renderState, float f) {
        super.extractRenderState(entity, renderState, f);
        renderState.pulleyPos = entity.getPulleyPos();
        renderState.boundingBoxMaxY = entity.getBoundingBox().maxY;
        renderState.blockPosition = entity.blockPosition();
        renderState.blockMap = entity.getBlockMap();
    }
}

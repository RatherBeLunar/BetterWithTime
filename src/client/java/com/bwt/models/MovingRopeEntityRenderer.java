package com.bwt.models;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MovingRopeEntityRenderer extends EntityRenderer<MovingRopeEntity> {
    private final BlockRenderDispatcher blockRenderManager;

    public MovingRopeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(MovingRopeEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        BlockPos pulleyPos = entity.getPulleyPos();
        if (pulleyPos == null) {
            return;
        }

        Level level = entity.level();
        matrices.pushPose();
        BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());

        matrices.pushPose();

        matrices.translate(-0.5, 0, -0.5);

        BlockState ropeState = BwtBlocks.ropeBlock.defaultBlockState();
        for (int i = 0; pulleyPos.getY() - entity.getY() > i && i < 2; i++) {
            matrices.pushPose();
            matrices.translate(0, i, 0);
            matrices.scale(1.001f, 1.001f, 1.001f);
            this.blockRenderManager.getModelRenderer().tesselateBlock(
                    level,
                    this.blockRenderManager.getBlockModel(ropeState),
                    ropeState,
                    blockPos.above(i),
                    matrices,
                    vertexConsumers.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(ropeState)),
                    false,
                    RandomSource.create(),
                    ropeState.getSeed(entity.blockPosition()),
                    OverlayTexture.NO_OVERLAY
            );
            matrices.popPose();
        }

        for (Map.Entry<Vec3i, BlockState> entry : entity.getBlockMap().entrySet()) {
            matrices.pushPose();
            Vec3i offset = entry.getKey();
            BlockState connectedBlockState = entry.getValue();
            matrices.translate(offset.getX(), offset.getY(), offset.getZ());
            this.blockRenderManager.getModelRenderer().tesselateBlock(
                    level,
                    this.blockRenderManager.getBlockModel(connectedBlockState),
                    connectedBlockState,
                    blockPos.offset(offset),
                    matrices,
                    vertexConsumers.getBuffer(RenderType.cutout()),
                    false,
                    RandomSource.create(),
                    connectedBlockState.getSeed(entity.blockPosition()),
                    OverlayTexture.NO_OVERLAY
            );
            matrices.popPose();
        }
        matrices.popPose();

        matrices.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MovingRopeEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}

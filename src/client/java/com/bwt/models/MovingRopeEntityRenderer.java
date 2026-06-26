package com.bwt.models;

import com.bwt.blocks.BwtBlocks;
import com.bwt.entities.MovingRopeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Map;

public class MovingRopeEntityRenderer extends EntityRenderer<MovingRopeEntity> {
    private final BlockRenderManager blockRenderManager;

    public MovingRopeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(MovingRopeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        BlockPos pulleyPos = entity.getPulleyPos();
        if (pulleyPos == null) {
            return;
        }

        World world = entity.getWorld();
        matrices.push();
        BlockPos blockPos = BlockPos.ofFloored(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());

        matrices.push();

        matrices.translate(-0.5, 0, -0.5);

        BlockState ropeState = BwtBlocks.ropeBlock.getDefaultState();
        for (int i = 0; pulleyPos.getY() - entity.getY() > i && i < 2; i++) {
            matrices.push();
            matrices.translate(0, i, 0);
            matrices.scale(1.001f, 1.001f, 1.001f);
            this.blockRenderManager.getModelRenderer().render(
                    world,
                    this.blockRenderManager.getModel(ropeState),
                    ropeState,
                    blockPos.up(i),
                    matrices,
                    vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(ropeState)),
                    false,
                    Random.create(),
                    ropeState.getRenderingSeed(entity.getBlockPos()),
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }

        for (Map.Entry<Vec3i, BlockState> entry : entity.getBlockMap().entrySet()) {
            matrices.push();
            Vec3i offset = entry.getKey();
            BlockState connectedBlockState = entry.getValue();
            matrices.translate(offset.getX(), offset.getY(), offset.getZ());
            this.blockRenderManager.getModelRenderer().render(
                    world,
                    this.blockRenderManager.getModel(connectedBlockState),
                    connectedBlockState,
                    blockPos.add(offset),
                    matrices,
                    vertexConsumers.getBuffer(RenderLayer.getCutout()),
                    false,
                    Random.create(),
                    connectedBlockState.getRenderingSeed(entity.getBlockPos()),
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
        }
        matrices.pop();

        matrices.pop();
    }

    @Override
    public Identifier getTexture(MovingRopeEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}

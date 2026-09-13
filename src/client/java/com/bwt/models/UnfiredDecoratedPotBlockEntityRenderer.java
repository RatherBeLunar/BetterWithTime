package com.bwt.models;

import com.bwt.blocks.unfired_pottery.UnfiredDecoratedPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(value= EnvType.CLIENT)
public class UnfiredDecoratedPotBlockEntityRenderer implements BlockEntityRenderer<UnfiredDecoratedPotBlockEntity> {
    protected final BlockRenderDispatcher manager;
    protected final ItemRenderer itemRenderer;

    public UnfiredDecoratedPotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.manager = context.getBlockRenderDispatcher();
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int light, int uv
    ) {
        Level level = unfiredDecoratedPotBlockEntity.getLevel();
        BlockState state = unfiredDecoratedPotBlockEntity.getBlockState();
        BlockPos pos = unfiredDecoratedPotBlockEntity.getBlockPos();

        // Render the block itself
        poseStack.pushPose();
        this.renderModel(level, state, pos, poseStack, vertexConsumerProvider, light, uv);
        poseStack.popPose();

        // Decorated pot code
        poseStack.pushPose();
        Direction facing = unfiredDecoratedPotBlockEntity.getHorizontalFacing();
        poseStack.translate(0.5, 0.5, 0.5);

        var rotationalSherds = unfiredDecoratedPotBlockEntity.getRotationalSherds();
        for (Optional<Item> sherd : rotationalSherds) {
            if (sherd.isEmpty()) {
                facing = facing.getClockWise();
                continue;
            }
            ItemStack sherdStack = sherd.get().getDefaultInstance();
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
            poseStack.translate(0, 0, -0.41);
            this.renderDecoratedSide(level, sherdStack, poseStack, vertexConsumerProvider, light, uv);
            poseStack.popPose();
            facing = facing.getClockWise();
        }
        poseStack.translate(-0.5, 0.0, -0.5);

        poseStack.popPose();
    }

    private void renderModel(@Nullable Level level, BlockState state, BlockPos pos, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int uv) {
        ModelManager bakedModelManager = manager.getBlockModelShaper().getModelManager();
        matrices.pushPose();
        if (level == null) {
            manager.getModelRenderer()
                    .renderModel(
                            matrices.last(),
                            vertexConsumers.getBuffer(Sheets.translucentItemSheet()),
                            state,
                            bakedModelManager.getBlockModelShaper().getBlockModel(state),
                            1.0F,
                            1.0F,
                            1.0F,
                            light,
                            uv
                    );
        }
        else {
            RenderType renderLayer = ItemBlockRenderTypes.getChunkRenderType(state);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            manager.getModelRenderer().tesselateBlock(
                    level,
                    manager.getBlockModel(state),
                    state,
                    pos,
                    matrices,
                    vertexConsumer,
                    true,
                    level.random,
                    state.getSeed(pos),
                    uv
            );
        }
        matrices.popPose();
    }

    @Override
    public int getViewDistance() {
        return 68;
    }

    private void renderDecoratedSide(
            @Nullable Level level, ItemStack itemStack, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay
    ) {
        matrices.pushPose();
        matrices.scale(0.9f, 0.9f, 0.9f);
        this.itemRenderer.renderStatic(
                itemStack,
                ItemDisplayContext.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                level,
                0
        );
        matrices.popPose();
    }
}

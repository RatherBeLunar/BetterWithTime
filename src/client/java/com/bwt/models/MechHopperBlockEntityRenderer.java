/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.utils.Id;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

@Environment(value=EnvType.CLIENT)
public class MechHopperBlockEntityRenderer implements BlockEntityRenderer<MechHopperBlockEntity> {
    private final BlockRenderDispatcher manager;
    private static final ResourceLocation FILL_TEXTURE = Id.of("textures/block/hopper_fill.png");
    protected final MechHopperFillModel model;

    public MechHopperBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.manager = ctx.getBlockRenderDispatcher();
        this.model = new MechHopperFillModel(ctx.bakeLayer(BetterWithTimeClient.MECH_HOPPER_FILL_LAYER));
    }

    protected ResourceLocation getFilterTexture(Item filterItem) {
        Optional<ResourceLocation> identifier = BuiltInRegistries.ITEM.wrapAsHolder(filterItem).unwrapKey().map(ResourceKey::location);
        return identifier.map(value -> value.withPrefix("textures/block/").withSuffix(".png"))
                .orElseGet(() -> Id.mc("textures/block/air.png"));

    }

    @Override
    public void render(MechHopperBlockEntity hopperBlockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int light, int uv) {
        Level level = hopperBlockEntity.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = hopperBlockEntity.getBlockPos();
        BlockState state = level.getBlockState(pos);

        // Render the hopper itself
        poseStack.pushPose();
        this.renderModel(pos, state, poseStack, vertexConsumerProvider, level, false, uv);
        poseStack.popPose();
        // Render the fill texture
        if (hopperBlockEntity.slotsOccupied > 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.renderType(FILL_TEXTURE));
            poseStack.pushPose();
            poseStack.scale(0.99f, 1, 0.99f);
            poseStack.translate(0.01f, (hopperBlockEntity.slotsOccupied * (14f - 7f) / (MechHopperBlockEntity.INVENTORY_SIZE - 1f) + 7f) / 16f, 0.01f);
            this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.pack(0.0f, false), -1);
            poseStack.popPose();
        }
        // Render the filter
        if (!hopperBlockEntity.filterInventory.isEmpty()) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.renderType(getFilterTexture(hopperBlockEntity.filterInventory.getTheItem().getItem())));
            poseStack.pushPose();
            poseStack.scale(0.99f, 1, 0.99f);
            poseStack.translate(0.01f, 15f / 16f, 0.01f);
            this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.pack(0.0f, false), -1);
            poseStack.popPose();
        }
    }

    private void renderModel(BlockPos pos, BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, Level level, boolean cull, int overlay) {
        RenderType renderLayer = ItemBlockRenderTypes.getChunkRenderType(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.manager.getModelRenderer().tesselateBlock(level, this.manager.getBlockModel(state), state, pos, matrices, vertexConsumer, cull, RandomSource.create(), state.getSeed(pos), overlay);
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}


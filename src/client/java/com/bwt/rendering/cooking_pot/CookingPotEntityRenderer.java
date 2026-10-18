package com.bwt.rendering.cooking_pot;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.bwt.rendering.ContainerFillLayerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Environment(value=EnvType.CLIENT)
public class CookingPotEntityRenderer implements BlockEntityRenderer<AbstractCookingPotBlockEntity> {
    private final ResourceLocation fillTexture;
    protected final ContainerFillLayerModel fillModel;

    public CookingPotEntityRenderer(BlockEntityRendererProvider.Context ctx, ResourceLocation fillTexture) {
        this.fillModel = new ContainerFillLayerModel(ctx.bakeLayer(BetterWithTimeClient.CONTAINER_FILL_LAYER));
        this.fillTexture = fillTexture;
    }

    @Override
    public void render(AbstractCookingPotBlockEntity cookingPotBlockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int light, int uv, Vec3 vec3) {
        Level level = cookingPotBlockEntity.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = cookingPotBlockEntity.getBlockPos();
        BlockState state = level.getBlockState(pos);

        // Render the fill texture
        if (cookingPotBlockEntity.slotsOccupied > 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.fillModel.renderType(fillTexture));
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);
            if (state.hasProperty(AbstractCookingPotBlock.TIP_DIRECTION)) {
                poseStack.mulPose(state.getValue(AbstractCookingPotBlock.TIP_DIRECTION).getRotation());
            }
            poseStack.translate(-0.5f, -0.5f, -0.5f);
            poseStack.scale(0.99f, 1, 0.99f);
            poseStack.translate(0.01f, (cookingPotBlockEntity.slotsOccupied * (13f - 2f) / (cookingPotBlockEntity.inventory.getContainerSize() - 1f) + 2f) / 16f, 0.01f);
            this.fillModel.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.pack(0.0f, false), -1);
            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}


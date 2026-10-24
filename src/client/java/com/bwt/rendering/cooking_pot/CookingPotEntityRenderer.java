package com.bwt.rendering.cooking_pot;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlock;
import com.bwt.blocks.abstract_cooking_pot.AbstractCookingPotBlockEntity;
import com.bwt.rendering.ContainerFillLayerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CookingPotEntityRenderer implements BlockEntityRenderer<AbstractCookingPotBlockEntity, AbstractCookingPotRenderState> {
    private final Identifier fillTexture;
    protected final ContainerFillLayerModel fillModel;

    public CookingPotEntityRenderer(BlockEntityRendererProvider.Context ctx, Identifier fillTexture) {
        this.fillModel = new ContainerFillLayerModel(ctx.bakeLayer(BetterWithTimeClient.CONTAINER_FILL_LAYER));
        this.fillTexture = fillTexture;
    }

    @Override
    public AbstractCookingPotRenderState createRenderState() {
        return new AbstractCookingPotRenderState();
    }

    @Override
    public void extractRenderState(AbstractCookingPotBlockEntity blockEntity, AbstractCookingPotRenderState renderState, float tickDelta, Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, tickDelta, vec3, crumblingOverlay);
        renderState.slotsOccupied = blockEntity.slotsOccupied;
        renderState.containerSize = blockEntity.inventory.getContainerSize();
    }

    private void submitLayer(
            AbstractCookingPotRenderState renderState,
            PoseStack poseStack,
            BlockState blockState,
            SubmitNodeCollector submitNodeCollector
    ) {
        if (renderState.slotsOccupied <= 0) {
            return;
        }
        poseStack.pushPose();
        // Rotate if tipped to the side
        poseStack.translate(0.5f, 0.5f, 0.5f);
        blockState.getOptionalValue(AbstractCookingPotBlock.TIP_DIRECTION)
                .map(Direction::getRotation)
                .ifPresent(poseStack::mulPose);
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        poseStack.pushPose();
        poseStack.translate(0.01f, (renderState.slotsOccupied * (13f - 2f) / renderState.containerSize + 2f) / 16f, 0.01f);
        submitNodeCollector.submitModel(
                this.fillModel,
                Unit.INSTANCE,
                poseStack,
                this.fillModel.renderType(this.fillTexture),
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null
        );
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public void submit(AbstractCookingPotRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        BlockState state = renderState.blockState;
        submitLayer(renderState, poseStack, state, submitNodeCollector);
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}


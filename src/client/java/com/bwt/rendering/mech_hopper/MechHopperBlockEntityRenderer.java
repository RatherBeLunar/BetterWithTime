package com.bwt.rendering.mech_hopper;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.rendering.ContainerFillLayerModel;
import com.bwt.utils.Id;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(value=EnvType.CLIENT)
public class MechHopperBlockEntityRenderer implements BlockEntityRenderer<MechHopperBlockEntity, MechHopperRenderState> {
    private static final ResourceLocation FILL_TEXTURE = Id.of("textures/block/hopper_fill.png");
    protected final ContainerFillLayerModel fillModel;

    public MechHopperBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.fillModel = new ContainerFillLayerModel(ctx.bakeLayer(BetterWithTimeClient.CONTAINER_FILL_LAYER));
    }

    protected @Nullable ResourceLocation getFilterTexture(Item filterItem) {
        if (filterItem.equals(Items.AIR)) {
            return null;
        }
        Optional<ResourceLocation> identifier = BuiltInRegistries.ITEM.wrapAsHolder(filterItem).unwrapKey().map(ResourceKey::location);
        return identifier.map(value -> value.withPrefix("textures/block/").withSuffix(".png"))
                .orElseGet(() -> Id.mc("textures/block/air.png"));

    }

    @Override
    public MechHopperRenderState createRenderState() {
        return new MechHopperRenderState();
    }

    @Override
    public void extractRenderState(MechHopperBlockEntity blockEntity, MechHopperRenderState renderState, float f, Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, f, vec3, crumblingOverlay);
        renderState.slotsOccupied = blockEntity.slotsOccupied;
        renderState.filterTextureLocation = getFilterTexture(blockEntity.filterInventory.getTheItem().getItem());
    }

    @Override
    public void submit(MechHopperRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        // Render the fill texture
        if (renderState.slotsOccupied > 0) {
            poseStack.pushPose();
            poseStack.scale(0.99f, 1, 0.99f);
            poseStack.translate(0.01f, (renderState.slotsOccupied * (14f - 7f) / (MechHopperBlockEntity.INVENTORY_SIZE - 1f) + 7f) / 16f, 0.01f);
            submitNodeCollector.submitModel(
                    this.fillModel,
                    Unit.INSTANCE,
                    poseStack,
                    this.fillModel.renderType(FILL_TEXTURE),
                    renderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    -1,
                    null
            );
            poseStack.popPose();
        }
        // Render the filter
        if (renderState.filterTextureLocation != null) {
            poseStack.pushPose();
            poseStack.scale(0.99f, 1, 0.99f);
            poseStack.translate(0.01f, 15f / 16f, 0.01f);
            submitNodeCollector.submitModel(
                    this.fillModel,
                    Unit.INSTANCE,
                    poseStack,
                    this.fillModel.renderType(renderState.filterTextureLocation),
                    renderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    -1,
                    null
            );
            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}


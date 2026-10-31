package com.bwt.rendering.unfired_decorated_pot;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.unfired_pottery.UnfiredDecoratedPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(value= EnvType.CLIENT)
public class UnfiredDecoratedPotBlockEntityRenderer implements BlockEntityRenderer<UnfiredDecoratedPotBlockEntity, UnfiredDecoratedPotRenderState> {
    private final ItemModelResolver itemModelResolver;

    public UnfiredDecoratedPotBlockEntityRenderer() {
        this.itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    public UnfiredDecoratedPotBlockEntityRenderer(ItemModelResolver itemModelResolver) {
        this.itemModelResolver = itemModelResolver;
    }

    public UnfiredDecoratedPotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this(context.itemModelResolver());
    }

    @Override
    public UnfiredDecoratedPotRenderState createRenderState() {
        return new UnfiredDecoratedPotRenderState();
    }

    @Override
    public void extractRenderState(
            UnfiredDecoratedPotBlockEntity blockEntity,
            UnfiredDecoratedPotRenderState renderState,
            float tickDelta,
            Vec3 vec3,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, tickDelta, vec3, crumblingOverlay);
        renderState.horizontalFacing = blockEntity.getHorizontalFacing();
        renderState.sherdRenderStates = new ArrayList<>();
        int i = (int)blockEntity.getBlockPos().asLong();

        List<Optional<Item>> rotationalSherds = blockEntity.getRotationalSherds();
        for (int j = 0; j < rotationalSherds.size(); j++) {
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            renderState.sherdRenderStates.add(itemStackRenderState);
            ItemStack sherd = rotationalSherds.get(j).orElse(Items.AIR).getDefaultInstance();
            if (sherd.is(Items.AIR)) {
                continue;
            }
            this.itemModelResolver.updateForTopItem(
                    itemStackRenderState,
                    sherd,
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(),
                    null,
                    i + j
            );
        }
    }

    @Override
    public void submit(UnfiredDecoratedPotRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        submitPotDecorations(
                renderState.horizontalFacing,
                poseStack,
                submitNodeCollector,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                renderState.sherdRenderStates
        );
    }

    public void submitPotWithDecorations(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            ItemDisplayContext itemDisplayContext,
            int light,
            int overlay,
            PotDecorations potDecorations,
            int outlineColor
    ) {
        poseStack.pushPose();
        submitNodeCollector.submitBlock(
                poseStack,
                BwtBlocks.unfiredDecoratedPotBlock.defaultBlockState(),
                light,
                overlay,
                outlineColor
        );
        submitPotDecorations(
                Direction.NORTH,
                poseStack,
                submitNodeCollector,
                itemDisplayContext,
                light,
                overlay,
                potDecorations
        );
        poseStack.popPose();
    }

    public void submitPotDecorations(Direction facing, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, int overlay, List<ItemStackRenderState> sherdRenderStates) {
        // Decorated pot code
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        for (ItemStackRenderState sherdRenderstate : sherdRenderStates) {
            if (sherdRenderstate.isEmpty()) {
                facing = facing.getClockWise();
                continue;
            }
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
            poseStack.translate(0, 0, -0.41);
            poseStack.pushPose();
            poseStack.scale(0.9f, 0.9f, 0.9f);
            sherdRenderstate.submit(
                    poseStack,
                    submitNodeCollector,
                    light,
                    overlay,
                    0
            );
            poseStack.popPose();
            poseStack.popPose();
            facing = facing.getClockWise();
        }
        poseStack.translate(-0.5, 0.0, -0.5);

        poseStack.popPose();
    }

    public void submitPotDecorations(
            Direction facing,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            ItemDisplayContext itemDisplayContext,
            int light,
            int overlay,
            PotDecorations potDecorations
    ) {
        List<Optional<Item>> rotationalSherds = List.of(potDecorations.front(), potDecorations.right(), potDecorations.back(), potDecorations.left());
        ArrayList<ItemStackRenderState> sherdRenderStates = new ArrayList<>();
        for (int j = 0; j < rotationalSherds.size(); j++) {
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            sherdRenderStates.add(itemStackRenderState);
            ItemStack sherd = rotationalSherds.get(j).orElse(Items.AIR).getDefaultInstance();
            if (sherd.is(Items.AIR)) {
                continue;
            }
            this.itemModelResolver.updateForTopItem(
                    itemStackRenderState,
                    sherd,
                    ItemDisplayContext.NONE,
                    null,
                    null,
                    j
            );
        }
        submitPotDecorations(
                facing,
                poseStack,
                submitNodeCollector,
                light,
                overlay,
                sherdRenderStates
        );
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}

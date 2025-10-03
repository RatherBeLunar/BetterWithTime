package com.bwt.models;

import com.bwt.blocks.BwtBlocks;
import com.bwt.blocks.unfired_pottery.UnfiredDecoratedPotBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.stream.Stream;

@Environment(value= EnvType.CLIENT)
public class UnfiredDecoratedPotBlockEntityRenderer implements BlockEntityRenderer<UnfiredDecoratedPotBlockEntity> {
    protected final BlockRenderManager manager;
    protected final ItemRenderer itemRenderer;

    public UnfiredDecoratedPotBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.manager = context.getRenderManager();
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            UnfiredDecoratedPotBlockEntity unfiredDecoratedPotBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int uv
    ) {
        World world = unfiredDecoratedPotBlockEntity.getWorld();
        BlockState state = unfiredDecoratedPotBlockEntity.getCachedState();

        // Render the block itself
        matrixStack.push();
        this.renderModel(state, matrixStack, vertexConsumerProvider, light, uv);
        matrixStack.pop();

        // Decorated pot code
        matrixStack.push();
        Direction facing = unfiredDecoratedPotBlockEntity.getHorizontalFacing();
        matrixStack.translate(0.5, 0.5, 0.5);

        var rotationalSherds = unfiredDecoratedPotBlockEntity.getRotationalSherds();
        for (Optional<Item> sherd : rotationalSherds) {
            if (sherd.isEmpty()) {
                facing = facing.rotateYClockwise();
                continue;
            }
            ItemStack sherdStack = sherd.get().getDefaultStack();
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - facing.asRotation()));
            matrixStack.translate(0, 0, -0.41);
            this.renderDecoratedSide(world, sherdStack, matrixStack, vertexConsumerProvider, light, uv);
            matrixStack.pop();
            facing = facing.rotateYClockwise();
        }
        matrixStack.translate(-0.5, 0.0, -0.5);

        matrixStack.pop();
    }

    private void renderModel(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int uv) {
        BakedModelManager bakedModelManager = manager.getModels().getModelManager();
        matrices.push();
        manager.getModelRenderer()
                .render(
                        matrices.peek(),
                        vertexConsumers.getBuffer(TexturedRenderLayers.getItemEntityTranslucentCull()),
                        state,
                        bakedModelManager.getBlockModels().getModel(state),
                        1.0F,
                        1.0F,
                        1.0F,
                        light,
                        uv
                );
        matrices.pop();
    }

    @Override
    public int getRenderDistance() {
        return 68;
    }

    private void renderDecoratedSide(
            World world, ItemStack itemStack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay
    ) {
        matrices.push();
        matrices.scale(0.9f, 0.9f, 0.9f);
        this.itemRenderer.renderItem(
                itemStack,
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                world,
                0
        );
        matrices.pop();
    }
}

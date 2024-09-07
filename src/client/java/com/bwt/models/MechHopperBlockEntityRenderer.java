/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.bwt.models;

import com.bwt.BetterWithTimeClient;
import com.bwt.blocks.mech_hopper.MechHopperBlockEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

@Environment(value=EnvType.CLIENT)
public class MechHopperBlockEntityRenderer implements BlockEntityRenderer<MechHopperBlockEntity> {
    private final BlockRenderManager manager;
    private static final Identifier FILL_TEXTURE = Id.of("textures/block/hopper_fill.png");
    protected MechHopperFillModel model;

    public MechHopperBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
        this.model = new MechHopperFillModel(ctx.getLayerModelPart(BetterWithTimeClient.MECH_HOPPER_FILL_LAYER));
    }

    protected Identifier getFilterTexture(Item filterItem) {
        Optional<Identifier> identifier = Registries.ITEM.getEntry(filterItem).getKey().map(RegistryKey::getValue);
        return identifier.map(value -> value.withPrefixedPath("textures/block/").withSuffixedPath(".png"))
                .orElseGet(() -> Id.mc("textures/block/air.png"));

    }

    @Override
    public void render(MechHopperBlockEntity hopperBlockEntity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int uv) {
        World world = hopperBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        BlockPos pos = hopperBlockEntity.getPos();
        BlockState state = world.getBlockState(pos);

        // Render the hopper itself
        matrixStack.push();
        this.renderModel(pos, state, matrixStack, vertexConsumerProvider, world, false, uv);
        matrixStack.pop();
        // Render the fill texture
        if (hopperBlockEntity.slotsOccupied > 0) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(FILL_TEXTURE));
            matrixStack.push();
            matrixStack.scale(0.99f, 1, 0.99f);
            matrixStack.translate(0.01f, (hopperBlockEntity.slotsOccupied * (14f - 7f) / (MechHopperBlockEntity.INVENTORY_SIZE - 1f) + 7f) / 16f, 0.01f);
            this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.getUv(0.0f, false), -1);
            matrixStack.pop();
        }
        // Render the filter
        if (!hopperBlockEntity.filterInventory.isEmpty()) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(getFilterTexture(hopperBlockEntity.filterInventory.getStack().getItem())));
            matrixStack.push();
            matrixStack.scale(0.99f, 1, 0.99f);
            matrixStack.translate(0.01f, 15f / 16f, 0.01f);
            this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.getUv(0.0f, false), -1);
            matrixStack.pop();
        }
    }

    private void renderModel(BlockPos pos, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, boolean cull, int overlay) {
        RenderLayer renderLayer = RenderLayers.getBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.manager.getModelRenderer().render(world, this.manager.getModel(state), state, pos, matrices, vertexConsumer, cull, Random.create(), state.getRenderingSeed(pos), overlay);
    }

    @Override
    public int getRenderDistance() {
        return 68;
    }
}


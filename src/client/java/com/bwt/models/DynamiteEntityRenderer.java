package com.bwt.models;

import com.bwt.entities.BroadheadArrowEntity;
import com.bwt.entities.DynamiteEntity;
import com.bwt.utils.Id;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

@Environment(value= EnvType.CLIENT)
public class DynamiteEntityRenderer extends EntityRenderer<DynamiteEntity> {
    public static final Identifier TEXTURE = Id.of("textures/item/dynamite.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public DynamiteEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public DynamiteEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLight(DynamiteEntity entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    @Override
    public void render(DynamiteEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25) {
            return;
        }
        matrices.push();
        matrices.scale(this.scale, this.scale, this.scale);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        int overlay = (entity.getFuse() / 5 % 2 == 0) ? OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10) : OverlayTexture.DEFAULT_UV;
        this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), entity.getId());
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(DynamiteEntity entity) {
        return TEXTURE;
    }
}
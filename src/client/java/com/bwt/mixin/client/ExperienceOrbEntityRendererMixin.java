package com.bwt.mixin.client;

import com.bwt.data.BwtDataAttachments;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntityRenderer.class)
public abstract class ExperienceOrbEntityRendererMixin extends EntityRenderer<ExperienceOrbEntity>
{
    protected ExperienceOrbEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow @Final private static RenderLayer LAYER;

    @Inject(method = "render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRender1(ExperienceOrbEntity orb, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        var data = orb.getAttached(BwtDataAttachments.dragonOrbData);
        if (data == null || !data.isValid()) return;

        matrices.push();

        int orbSize = orb.getOrbSize();
        float u0 = (float)(orbSize % 4 * 16) / 64.0F;
        float u1 = (float)(orbSize % 4 * 16 + 16) / 64.0F;
        float v0 = (float)(orbSize / 4 * 16) / 64.0F;
        float v1 = (float)(orbSize / 4 * 16 + 16) / 64.0F;

        matrices.translate(0.0F, 0.1F, 0.0F);
        matrices.multiply(this.dispatcher.getRotation());
        matrices.scale(0.3F, 0.3F, 0.3F);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        MatrixStack.Entry entry = matrices.peek();

        int red = 255, green = 0, blue = 0;

        vertex(vertexConsumer, entry, -0.5F, -0.25F, red, green, blue, u0, v1, light);
        vertex(vertexConsumer, entry,  0.5F, -0.25F, red, green, blue, u1, v1, light);
        vertex(vertexConsumer, entry,  0.5F,  0.75F, red, green, blue, u1, v0, light);
        vertex(vertexConsumer, entry, -0.5F,  0.75F, red, green, blue, u0, v0, light);

        matrices.pop();

        ci.cancel(); // Prevent original render
    }

    @Unique
    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red,
                               int green, int blue, float u, float v, int light)
    {
        vertexConsumer.vertex(matrix, x, y, 0.0F)
                .color(red, green, blue, 128)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, 0.0F, 1.0F, 0.0F);
    }

}
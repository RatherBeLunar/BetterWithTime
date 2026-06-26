package com.bwt.mixin.client;

import com.bwt.entities.CanvasEntity;
import com.bwt.utils.Id;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(PaintingEntityRenderer.class)
public abstract class CanvasEntityCustomBackMixin extends EntityRenderer<PaintingEntity> {
    @Unique
    private static final PaintingVariant BACK = new PaintingVariant(1, 1, Id.of("back"));

    protected CanvasEntityCustomBackMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"),
            index = 6
    )
    private static Sprite bwt$renderCanvasCustomBack(Sprite paintingSprite, @Local PaintingEntity paintingEntity, @Local PaintingManager paintingManager) {
        if (paintingEntity instanceof CanvasEntity) {
            return paintingManager.getPaintingSprite(BACK);
        }
        return paintingSprite;
    }
}

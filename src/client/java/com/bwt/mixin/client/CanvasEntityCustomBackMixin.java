package com.bwt.mixin.client;

import com.bwt.entities.CanvasEntity;
import com.bwt.utils.Id;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(PaintingRenderer.class)
public abstract class CanvasEntityCustomBackMixin extends EntityRenderer<Painting> {
    @Unique
    private static final PaintingVariant BACK = new PaintingVariant(1, 1, Id.of("back"));

    protected CanvasEntityCustomBackMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/world/entity/decoration/Painting;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/PaintingRenderer;renderPainting(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/decoration/Painting;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"),
            index = 6
    )
    private static TextureAtlasSprite bwt$renderCanvasCustomBack(TextureAtlasSprite paintingSprite, @Local(argsOnly = true) Painting paintingEntity, @Local PaintingTextureManager paintingManager) {
        if (paintingEntity instanceof CanvasEntity) {
            return paintingManager.get(BACK);
        }
        return paintingSprite;
    }
}

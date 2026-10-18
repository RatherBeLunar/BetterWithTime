package com.bwt.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface RenderTypeAccessorMixin {
    @Invoker("create")
    static RenderType.CompositeRenderType create(String string, int i, boolean bl, boolean bl2, RenderPipeline renderPipeline, RenderType.CompositeState compositeState) {
        throw new AssertionError();
    }
}

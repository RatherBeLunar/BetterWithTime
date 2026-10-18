package com.bwt.mixin.client;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.CompositeState.CompositeStateBuilder.class)
public interface CompositeStateBuilderAccessorMixin {
    @Invoker("setTextureState")
    RenderType.CompositeState.CompositeStateBuilder bwt$setTextureState(RenderStateShard.EmptyTextureStateShard emptyTextureStateShard);

    @Invoker("createCompositeState")
    RenderType.CompositeState bwt$createCompositeState(boolean bl);
}

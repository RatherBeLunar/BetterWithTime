package com.bwt.render_layers;

import com.bwt.mixin.client.CompositeStateBuilderAccessorMixin;
import com.bwt.mixin.client.RenderTypeAccessorMixin;
import com.bwt.utils.Id;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class KilnBlockCookingRenderLayer extends RenderType {
    public KilnBlockCookingRenderLayer(String string, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, i, bl, bl2, runnable, runnable2);
    }

    private static final Function<ResourceLocation, RenderType> KILN_COOKING_RENDER_FUNCTION = Util.memoize(
            resourceLocation -> {
                RenderStateShard.TextureStateShard textureStateShard = new RenderStateShard.TextureStateShard(resourceLocation, false);
                return RenderTypeAccessorMixin.bwt$create(
                        "kiln_cooking",
                        1536,
                        false,
                        true,
                        RenderPipelines.CRUMBLING,
                        ((CompositeStateBuilderAccessorMixin)((CompositeStateBuilderAccessorMixin) CompositeState.builder()).bwt$setTextureState(textureStateShard)).bwt$createCompositeState(false)
                );
            }
    );
    public static final List<ResourceLocation> KILN_COOKING_STAGES = IntStream.range(0, 10)
            .mapToObj(stage -> Id.of("block/kiln_cook_stage_" + stage))
            .collect(Collectors.toList());
    public static final List<ResourceLocation> KILN_COOKING_STAGE_TEXTURES = KILN_COOKING_STAGES.stream()
            .map(id -> id.withPath(path -> "textures/" + path + ".png"))
            .collect(Collectors.toList());

    public static final List<RenderType> KILN_COOKING_RENDER_LAYERS = KILN_COOKING_STAGE_TEXTURES.stream()
            .map(KILN_COOKING_RENDER_FUNCTION)
            .collect(Collectors.toList());

}

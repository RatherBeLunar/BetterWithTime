package com.bwt.render_layers;

import com.bwt.utils.Id;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class KilnBlockCookingRenderLayer {
    private static final Function<ResourceLocation, RenderType> KILN_COOKING_RENDER_FUNCTION = Util.memoize(
            texture -> {
                RenderStateShard.TextureStateShard texture2 = new RenderStateShard.TextureStateShard(texture, false, false);
                return RenderType.create(
                        "kiln_cooking",
                        DefaultVertexFormat.BLOCK,
                        VertexFormat.Mode.QUADS,
                        1536,
                        false,
                        true,
                        RenderType.CompositeState.builder()
                                .setShaderState(RenderType.RENDERTYPE_CRUMBLING_SHADER)
                                .setTextureState(texture2)
                                .setTransparencyState(RenderType.CRUMBLING_TRANSPARENCY)
                                .setWriteMaskState(RenderType.COLOR_WRITE)
                                .setLayeringState(RenderType.POLYGON_OFFSET_LAYERING)
                                .createCompositeState(false)
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

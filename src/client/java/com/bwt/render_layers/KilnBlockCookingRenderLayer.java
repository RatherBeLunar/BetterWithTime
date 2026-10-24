package com.bwt.render_layers;

import com.bwt.utils.Id;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public interface KilnBlockCookingRenderLayer {
    Function<Identifier, RenderType> KILN_COOKING_RENDER_FUNCTION = Util.memoize(
            identifier -> RenderType.create(
                    "kiln_cooking",
                    RenderSetup
                    .builder(RenderPipelines.CRUMBLING)
                    .withTexture("Sampler0", identifier)
                    .sortOnUpload()
                    .createRenderSetup()
            )
    );
    List<Identifier> KILN_COOKING_STAGES = IntStream.range(0, 10)
            .mapToObj(stage -> Id.of("block/kiln_cook_stage_" + stage))
            .collect(Collectors.toList());
    List<Identifier> KILN_COOKING_STAGE_TEXTURES = KILN_COOKING_STAGES.stream()
            .map(id -> id.withPath(path -> "textures/" + path + ".png"))
            .collect(Collectors.toList());

    List<RenderType> KILN_COOKING_RENDER_LAYERS = KILN_COOKING_STAGE_TEXTURES.stream()
            .map(KILN_COOKING_RENDER_FUNCTION)
            .collect(Collectors.toList());

}

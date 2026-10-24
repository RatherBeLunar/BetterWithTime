package com.bwt.rendering.unfired_decorated_pot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public class UnfiredDecoratedPotSpecialRenderer implements SpecialModelRenderer<PotDecorations> {
    private final UnfiredDecoratedPotBlockEntityRenderer unfiredDecoratedPotRenderer;

    public UnfiredDecoratedPotSpecialRenderer(UnfiredDecoratedPotBlockEntityRenderer unfiredDecoratedPotRenderer) {
        this.unfiredDecoratedPotRenderer = unfiredDecoratedPotRenderer;
    }

    @Nullable
    public PotDecorations extractArgument(ItemStack itemStack) {
        return itemStack.get(DataComponents.POT_DECORATIONS);
    }

    @Override
    public void submit(
            @Nullable PotDecorations potDecorations,
            ItemDisplayContext itemDisplayContext,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int i,
            int j,
            boolean bl,
            int k
    ) {
        this.unfiredDecoratedPotRenderer.submitPotWithDecorations(
                poseStack,
                submitNodeCollector,
                i,
                j,
                Objects.requireNonNullElse(potDecorations, PotDecorations.EMPTY),
                k
        );
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        // TODO?
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<UnfiredDecoratedPotSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new UnfiredDecoratedPotSpecialRenderer.Unbaked());

        @Override
        public MapCodec<UnfiredDecoratedPotSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(BakingContext bakingContext) {
            return new UnfiredDecoratedPotSpecialRenderer(new UnfiredDecoratedPotBlockEntityRenderer());
        }
    }
}


package com.bwt.rendering.unfired_decorated_pot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Set;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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
    public void render(
            @Nullable PotDecorations potDecorations,
            ItemDisplayContext itemDisplayContext,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            int j,
            boolean bl
    ) {
        this.unfiredDecoratedPotRenderer.renderPotWithDecorations(poseStack, multiBufferSource, i, j, Objects.requireNonNullElse(potDecorations, PotDecorations.EMPTY));
    }

    @Override
    public void getExtents(Set<Vector3f> set) {
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
        public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
            return new UnfiredDecoratedPotSpecialRenderer(new UnfiredDecoratedPotBlockEntityRenderer(entityModelSet));
        }
    }
}


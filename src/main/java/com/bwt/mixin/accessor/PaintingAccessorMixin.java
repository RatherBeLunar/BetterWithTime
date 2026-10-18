package com.bwt.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Painting.class)
public interface PaintingAccessorMixin {
    @Invoker("setVariant")
    void bwt$setVariant(Holder<PaintingVariant> holder);
}

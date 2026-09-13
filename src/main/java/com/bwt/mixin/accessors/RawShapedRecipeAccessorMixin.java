package com.bwt.mixin.accessors;

import com.mojang.serialization.DataResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

@Mixin(ShapedRecipePattern.class)
public interface RawShapedRecipeAccessorMixin {
    @Accessor
    Optional<ShapedRecipePattern.Data> getData();

    @Invoker("unpack")
    static DataResult<ShapedRecipePattern> fromData(ShapedRecipePattern.Data data) {
        throw new AssertionError();
    }
}

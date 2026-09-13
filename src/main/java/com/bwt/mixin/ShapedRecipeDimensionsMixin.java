package com.bwt.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

@Mixin(ShapedRecipePattern.Data.class)
public abstract class ShapedRecipeDimensionsMixin {
    @Mutable
    @Accessor
    private static void setPATTERN_CODEC(Codec<List<String>> value) {}

    @Accessor
    private static Codec<Character> getSYMBOL_CODEC() {
        return null;
    }

    @Mutable
    @Accessor
    private static void setMAP_CODEC(MapCodec<ShapedRecipePattern.Data> value) {}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clint(CallbackInfo ci) {

        Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
            if (pattern.size() > 4) {
                return DataResult.error(() -> "Invalid pattern: too many rows, 4 is maximum");
            }
            if (pattern.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            }
            int i = pattern.get(0).length();
            for (String string : pattern) {
                if (string.length() > 4) {
                    return DataResult.error(() -> "Invalid pattern: too many columns, 4 is maximum");
                }
                if (i == string.length()) continue;
                return DataResult.error(() -> "Invalid pattern: each row must be the same width");
            }
            return DataResult.success(pattern);
        }, Function.identity());
        setPATTERN_CODEC(PATTERN_CODEC);
        setMAP_CODEC(RecordCodecBuilder.mapCodec(instance -> instance.group(
                (ExtraCodecs.strictUnboundedMap(getSYMBOL_CODEC(), Ingredient.CODEC_NONEMPTY).fieldOf("key"))
                        .forGetter(ShapedRecipePattern.Data::key), (PATTERN_CODEC.fieldOf("pattern"))
                        .forGetter(ShapedRecipePattern.Data::pattern)
        ).apply(instance, ShapedRecipePattern.Data::new)));
    }
}

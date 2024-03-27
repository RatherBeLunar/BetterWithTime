package com.bwt.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(RawShapedRecipe.Data.class)
public record ShapedRecipeDimensionsMixin() {
    @Mutable
    @Accessor
    private static void setPATTERN_CODEC(Codec<List<String>> value) {}

    @Accessor
    private static Codec<Character> getKEY_ENTRY_CODEC() {
        return null;
    }

    @Mutable
    @Accessor
    private static void setCODEC(MapCodec<RawShapedRecipe.Data> value) {}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clint(CallbackInfo ci) {

        Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
            if (pattern.size() > 4) {
                return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
            }
            if (pattern.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            }
            int i = pattern.get(0).length();
            for (String string : pattern) {
                if (string.length() > 4) {
                    return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
                }
                if (i == string.length()) continue;
                return DataResult.error(() -> "Invalid pattern: each row must be the same width");
            }
            return DataResult.success(pattern);
        }, Function.identity());
        setPATTERN_CODEC(PATTERN_CODEC);
        setCODEC(RecordCodecBuilder.mapCodec(instance -> instance.group(
                (Codecs.strictUnboundedMap(getKEY_ENTRY_CODEC(), Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("key"))
                        .forGetter(RawShapedRecipe.Data::key), (PATTERN_CODEC.fieldOf("pattern"))
                        .forGetter(RawShapedRecipe.Data::pattern)
        ).apply(instance, RawShapedRecipe.Data::new)));
    }
}

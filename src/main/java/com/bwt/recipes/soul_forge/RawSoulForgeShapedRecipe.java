package com.bwt.recipes.soul_forge;

import com.bwt.mixin.accessors.RawShapedRecipeAccessorMixin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class RawSoulForgeShapedRecipe {
    public static final MapCodec<ShapedRecipePattern> CODEC = Data.CODEC
            .flatXmap(
                    RawShapedRecipeAccessorMixin::fromData,
                    recipe -> ((RawShapedRecipeAccessorMixin) (Object) recipe).getData().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
            );

    public record Data(Map<Character, Ingredient> key, List<String> pattern) {
        private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
            if (pattern.size() > 4) {
                return DataResult.error(() -> "Invalid pattern: too many rows, 4 is maximum");
            } else if (pattern.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            } else {
                int i = pattern.get(0).length();

                for (String string : pattern) {
                    if (string.length() > 4) {
                        return DataResult.error(() -> "Invalid pattern: too many columns, 4 is maximum");
                    }

                    if (i != string.length()) {
                        return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                    }
                }

                return DataResult.success(pattern);
            }
        }, Function.identity());
        private static final Codec<Character> KEY_ENTRY_CODEC = Codec.STRING.comapFlatMap(keyEntry -> {
            if (keyEntry.length() != 1) {
                return DataResult.error(() -> "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).");
            } else {
                return " ".equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(keyEntry.charAt(0));
            }
        }, String::valueOf);
        public static final MapCodec<ShapedRecipePattern.Data> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ExtraCodecs.strictUnboundedMap(KEY_ENTRY_CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter(ShapedRecipePattern.Data::key),
                                PATTERN_CODEC.fieldOf("pattern").forGetter(ShapedRecipePattern.Data::pattern)
                        )
                        .apply(instance, ShapedRecipePattern.Data::new)
        );
    }
}

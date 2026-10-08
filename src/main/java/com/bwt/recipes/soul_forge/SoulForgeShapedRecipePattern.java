package com.bwt.recipes.soul_forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

public class SoulForgeShapedRecipePattern {
    private static final int MAX_SIZE = 4;
    public static final char EMPTY_SLOT = ' ';
    public static final MapCodec<SoulForgeShapedRecipePattern> MAP_CODEC = SoulForgeShapedRecipePattern.Data.MAP_CODEC
            .flatXmap(
                    SoulForgeShapedRecipePattern::unpack,
                    soulForgeShapedRecipePattern -> soulForgeShapedRecipePattern.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, SoulForgeShapedRecipePattern> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            soulForgeShapedRecipePattern -> soulForgeShapedRecipePattern.width,
            ByteBufCodecs.VAR_INT,
            soulForgeShapedRecipePattern -> soulForgeShapedRecipePattern.height,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            soulForgeShapedRecipePattern -> soulForgeShapedRecipePattern.ingredients,
            SoulForgeShapedRecipePattern::createFromNetwork
    );
    private final int width;
    private final int height;
    private final List<Optional<Ingredient>> ingredients;
    private final Optional<SoulForgeShapedRecipePattern.Data> data;
    private final int ingredientCount;
    private final boolean symmetrical;

    public SoulForgeShapedRecipePattern(int i, int j, List<Optional<Ingredient>> list, Optional<SoulForgeShapedRecipePattern.Data> optional) {
        this.width = i;
        this.height = j;
        this.ingredients = list;
        this.data = optional;
        this.ingredientCount = (int)list.stream().flatMap(Optional::stream).count();
        this.symmetrical = Util.isSymmetrical(i, j, list);
    }

    private static SoulForgeShapedRecipePattern createFromNetwork(Integer integer, Integer integer2, List<Optional<Ingredient>> list) {
        return new SoulForgeShapedRecipePattern(integer, integer2, list, Optional.empty());
    }

    public static SoulForgeShapedRecipePattern of(Map<Character, Ingredient> map, String... strings) {
        return of(map, List.of(strings));
    }

    public static SoulForgeShapedRecipePattern of(Map<Character, Ingredient> map, List<String> list) {
        SoulForgeShapedRecipePattern.Data data = new SoulForgeShapedRecipePattern.Data(map, list);
        return unpack(data).getOrThrow();
    }

    private static DataResult<SoulForgeShapedRecipePattern> unpack(SoulForgeShapedRecipePattern.Data data) {
        String[] strings = shrink(data.pattern);
        int i = strings[0].length();
        int j = strings.length;
        List<Optional<Ingredient>> list = new ArrayList<>(i * j);
        CharSet charSet = new CharArraySet(data.key.keySet());

        for (String string : strings) {
            for (int k = 0; k < string.length(); k++) {
                char c = string.charAt(k);
                Optional<Ingredient> optional;
                if (c == EMPTY_SLOT) {
                    optional = Optional.empty();
                } else {
                    Ingredient ingredient = data.key.get(c);
                    if (ingredient == null) {
                        return DataResult.error(() -> "Pattern references symbol '" + c + "' but it's not defined in the key");
                    }

                    optional = Optional.of(ingredient);
                }

                charSet.remove(c);
                list.add(optional);
            }
        }

        return !charSet.isEmpty()
                ? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + charSet)
                : DataResult.success(new SoulForgeShapedRecipePattern(i, j, list, Optional.of(data)));
    }

    static String[] shrink(List<String> list) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int m = 0; m < list.size(); m++) {
            String string = list.get(m);
            i = Math.min(i, firstNonEmpty(string));
            int n = lastNonEmpty(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    k++;
                }

                l++;
            } else {
                l = 0;
            }
        }

        if (list.size() == l) {
            return new String[0];
        }

        String[] strings = new String[list.size() - l - k];

        for (int o = 0; o < strings.length; o++) {
            strings[o] = list.get(o + k).substring(i, j + 1);
        }

        return strings;
    }

    private static int firstNonEmpty(String string) {
        int i = 0;

        while (i < string.length() && string.charAt(i) == EMPTY_SLOT) {
            i++;
        }

        return i;
    }

    private static int lastNonEmpty(String string) {
        int i = string.length() - 1;

        while (i >= 0 && string.charAt(i) == EMPTY_SLOT) {
            i--;
        }

        return i;
    }

    public boolean matches(CraftingInput craftingInput) {
        if (craftingInput.ingredientCount() != this.ingredientCount) {
            return false;
        }

        if (craftingInput.width() == this.width && craftingInput.height() == this.height) {
            return !this.symmetrical && this.matches(craftingInput, true) || this.matches(craftingInput, false);

        }

        return false;
    }

    private boolean matches(CraftingInput craftingInput, boolean flipped) {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                Optional<Ingredient> optional;
                if (flipped) {
                    optional = this.ingredients.get(this.width - j - 1 + i * this.width);
                } else {
                    optional = this.ingredients.get(j + i * this.width);
                }

                ItemStack itemStack = craftingInput.getItem(j, i);
                if (!Ingredient.testOptionalIngredient(optional, itemStack)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public List<Optional<Ingredient>> ingredients() {
        return this.ingredients;
    }

    public record Data(Map<Character, Ingredient> key, List<String> pattern) {
        private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(list -> {
            if (list.size() > MAX_SIZE) {
                return DataResult.error(() -> "Invalid pattern: too many rows, " + MAX_SIZE + " is maximum");
            }

            if (list.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            }

            int i = list.getFirst().length();

            for (String string : list) {
                if (string.length() > MAX_SIZE) {
                    return DataResult.error(() -> "Invalid pattern: too many columns, " + MAX_SIZE + " is maximum");
                }

                if (i != string.length()) {
                    return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                }
            }

            return DataResult.success(list);
        }, Function.identity());
        private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(string -> {
            if (string.length() != 1) {
                return DataResult.error(() -> "Invalid key entry: '" + string + "' is an invalid symbol (must be 1 character only).");
            } else {
                return " ".equals(string) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(string.charAt(0));
            }
        }, String::valueOf);
        public static final MapCodec<SoulForgeShapedRecipePattern.Data> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC).fieldOf("key").forGetter(data -> data.key),
                                PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.pattern)
                        )
                        .apply(instance, SoulForgeShapedRecipePattern.Data::new)
        );
    }
}

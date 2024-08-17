package com.bwt.screens.infernal_enchanter;

import com.mojang.datafixers.kinds.IdF;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class InfernalEnchantingPhrases {
    private static final Identifier FONT_ID = new Identifier("minecraft", "alt");
    private static final Style STYLE;
    private static final InfernalEnchantingPhrases INSTANCE;
    private final Random random = Random.create();


    private InfernalEnchantingPhrases() {
    }

    public static InfernalEnchantingPhrases getInstance() {
        return INSTANCE;
    }

    public StringVisitable generatePhrase(TextRenderer textRenderer, int width, Enchantment enchantment, int level) {
        var text = rot13ByLevel(enchantment, level).fillStyle(STYLE);
        return textRenderer.getTextHandler().trimToWidth(text, width, Style.EMPTY);
    }

    public MutableText rot13(Text text, int offset) {
        var builder = new StringBuilder();
        for (var c : text.getString().toCharArray()) {
            if (Character.isLetter(c)) {
                var base = Character.isLowerCase(c) ? 'a' : 'A';
                builder.append((char) (base + (c - base + offset) % 26));
            } else {
                builder.append(c);
            }
        }

        return Text.literal(builder.toString());
    }


    public static MutableText rot13ByLevel(Enchantment enchantment, int level) {
        var text = tooltipForEnchantment(enchantment, level);
        return INSTANCE.rot13(text, 13 + level);
    }

    public static MutableText tooltipForEnchantment(Enchantment enchantment, int level) {
        return (MutableText) enchantment.getName(level);
    }

    static {
        STYLE = Style.EMPTY.withFont(FONT_ID);
        INSTANCE = new InfernalEnchantingPhrases();
    }
}


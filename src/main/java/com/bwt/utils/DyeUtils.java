package com.bwt.utils;

import com.google.common.collect.Maps;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface DyeUtils {
    List<DyeColor> DYE_COLORS_ORDERED = List.of(
        DyeColor.WHITE,
        DyeColor.LIGHT_GRAY,
        DyeColor.GRAY,
        DyeColor.BLACK,
        DyeColor.BROWN,
        DyeColor.RED,
        DyeColor.ORANGE,
        DyeColor.YELLOW,
        DyeColor.LIME,
        DyeColor.GREEN,
        DyeColor.CYAN,
        DyeColor.LIGHT_BLUE,
        DyeColor.BLUE,
        DyeColor.PURPLE,
        DyeColor.MAGENTA,
        DyeColor.PINK
    );

    Map<DyeColor, ItemConvertible> WOOL_COLORS = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        map.put(DyeColor.LIME, Blocks.LIME_WOOL);
        map.put(DyeColor.PINK, Blocks.PINK_WOOL);
        map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        map.put(DyeColor.RED, Blocks.RED_WOOL);
        map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });

    Comparator<DyeColor> COMPARE_DYE_COLOR = Comparator.comparingInt(DYE_COLORS_ORDERED::indexOf);
    Comparator<Map.Entry<DyeColor, ?>> COMPARE_DYE_COLOR_ENTRY = Comparator.comparingInt(entry -> DYE_COLORS_ORDERED.indexOf(entry.getKey()));

    static <T> Stream<T> streamColorItemsSorted(Map<DyeColor, T> coloredItems) {
        return coloredItems.entrySet().stream().sorted(COMPARE_DYE_COLOR_ENTRY).map(Map.Entry::getValue);
    }

    static <T> List<T> getColorItemsSorted(Map<DyeColor, T> coloredItems) {
        return streamColorItemsSorted(coloredItems).toList();
    }
}

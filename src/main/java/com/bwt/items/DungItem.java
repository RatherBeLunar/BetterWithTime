package com.bwt.items;

import com.bwt.mixin.accessors.DyeItemAccessorMixin;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

import java.util.Map;

public class DungItem extends DyeItem {
    public DungItem(Item.Settings settings) {
        super(DyeColor.BROWN, settings);
        Map<DyeColor, DyeItem> dyes = DyeItemAccessorMixin.getDYES();
        if (dyes != null) {
            dyes.put(getColor(), ((DyeItem) Items.BROWN_DYE));
        }
    }
}

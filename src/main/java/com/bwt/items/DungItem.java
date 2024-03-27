package com.bwt.items;

import com.bwt.mixin.DyeItemAccessorMixin;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class DungItem extends DyeItem {
    public DungItem(Item.Settings settings) {
        super(DyeColor.BROWN, settings);
        DyeItemAccessorMixin.getDYES().put(getColor(), ((DyeItem) Items.BROWN_DYE));
    }
}

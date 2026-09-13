package com.bwt.items;

import com.bwt.mixin.accessors.DyeItemAccessorMixin;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DungItem extends DyeItem {
    public DungItem(Item.Properties settings) {
        super(DyeColor.BROWN, settings);
        Map<DyeColor, DyeItem> dyes = DyeItemAccessorMixin.getITEM_BY_COLOR();
        if (dyes != null) {
            dyes.put(getDyeColor(), ((DyeItem) Items.BROWN_DYE));
        }
    }
}

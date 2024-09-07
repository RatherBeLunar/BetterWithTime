package com.bwt.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SoulUrnItem extends Item {
    public SoulUrnItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}

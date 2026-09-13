package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;

public class MattockItem extends DiggerItem {
    public MattockItem(Tier material, Item.Properties settings) {
        super(material, BwtBlockTags.MATTOCK_MINEABLE, settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Only shovels have a right click action, so we inherit from that
        return Items.NETHERITE_SHOVEL.useOn(context);
    }
}

package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

public class MattockItem extends DiggerItem {
    public MattockItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties settings) {
        super(material, BwtBlockTags.MATTOCK_MINEABLE, attackDamage, attackSpeed, settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Only shovels have a right click action, so we inherit from that
        return Items.NETHERITE_SHOVEL.useOn(context);
    }
}

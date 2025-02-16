package com.bwt.items;

import com.bwt.tags.BwtBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;

public class BattleAxeItem extends SwordItem {
    public BattleAxeItem(ToolMaterial material, Item.Settings settings) {
        super(material, settings.component(DataComponentTypes.TOOL, material.createComponent(BwtBlockTags.BATTLEAXE_MINEABLE)));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return Items.NETHERITE_AXE.useOnBlock(context);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 15.0f;
        }
        return super.getMiningSpeed(stack, state);
    }
}
